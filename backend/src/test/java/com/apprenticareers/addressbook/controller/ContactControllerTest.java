package com.apprenticareers.addressbook.controller;

import com.apprenticareers.addressbook.config.SecurityConfig;
import com.apprenticareers.addressbook.domain.User;
import com.apprenticareers.addressbook.dto.ContactRequest;
import com.apprenticareers.addressbook.dto.ContactResponse;
import com.apprenticareers.addressbook.exception.ContactNotFoundException;
import com.apprenticareers.addressbook.security.CustomUserDetailsService;
import com.apprenticareers.addressbook.security.JwtService;
import com.apprenticareers.addressbook.security.UserPrincipal;
import com.apprenticareers.addressbook.service.ContactService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContactController.class)
@Import(SecurityConfig.class)
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ContactService contactService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private static final UserPrincipal OWNER_ONE =
            UserPrincipal.from(buildUser(1L, "owner1@example.com", User.Role.USER));
    private static final UserPrincipal OWNER_TWO =
            UserPrincipal.from(buildUser(2L, "owner2@example.com", User.Role.ADMIN));

    private static User buildUser(Long id, String email, User.Role role) {
        User u = new User();
        u.setId(id);
        u.setEmail(email);
        u.setPasswordHash("hash");
        u.setRole(role);
        return u;
    }

    private ContactRequest validRequest() {
        return new ContactRequest("Bob Jones", "2 Oak Ave", "555-0200", "bob@example.com");
    }

    private ContactResponse sampleResponse() {
        return new ContactResponse(100L, "Bob Jones", "2 Oak Ave", "555-0200", "bob@example.com",
                LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void addContact_returns201AndScopesToAuthenticatedOwner() throws Exception {
        when(contactService.addContact(eq(1L), any(ContactRequest.class))).thenReturn(sampleResponse());

        mockMvc.perform(post("/contacts")
                        .with(user(OWNER_ONE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Bob Jones"));

        verify(contactService).addContact(eq(1L), any(ContactRequest.class));
    }

    @Test
    void addContact_returns401WhenUnauthenticated() throws Exception {
        mockMvc.perform(post("/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void addContact_returns400ForInvalidPayload() throws Exception {
        ContactRequest invalid = new ContactRequest("", "", "", "not-an-email");

        mockMvc.perform(post("/contacts")
                        .with(user(OWNER_ONE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getContacts_returnsOnlyAuthenticatedOwnersContacts() throws Exception {
        when(contactService.getContacts(1L)).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/contacts").with(user(OWNER_ONE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Bob Jones"));

        verify(contactService).getContacts(1L);
    }

    @Test
    void getContacts_returns401WhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/contacts")).andExpect(status().isUnauthorized());
    }

    @Test
    void searchContacts_passesQueryAndOwnerToService() throws Exception {
        when(contactService.searchContacts(1L, "bob")).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/contacts/search").with(user(OWNER_ONE)).param("query", "bob"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("bob@example.com"));

        verify(contactService).searchContacts(1L, "bob");
    }

    @Test
    void updateContact_returns200ForOwnedContact() throws Exception {
        when(contactService.updateContact(eq(100L), eq(1L), any(ContactRequest.class)))
                .thenReturn(sampleResponse());

        mockMvc.perform(put("/contacts/100")
                        .with(user(OWNER_ONE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Bob Jones"));
    }

    @Test
    void updateContact_returns404WhenNotOwnedByCaller() throws Exception {
        when(contactService.updateContact(eq(100L), eq(2L), any(ContactRequest.class)))
                .thenThrow(new ContactNotFoundException(100L));

        mockMvc.perform(put("/contacts/100")
                        .with(user(OWNER_TWO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Resource not found"))
                .andExpect(jsonPath("$.message", not(containsString("100"))));
    }

    @Test
    void deleteContact_returns200ForOwnedContact() throws Exception {
        mockMvc.perform(delete("/contacts/100").with(user(OWNER_ONE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Contact deleted successfully."));

        verify(contactService).deleteContact(100L, 1L);
    }

    @Test
    void deleteContact_returns404WhenNotOwnedByCaller_evenForAdmin() throws Exception {
        doThrow(new ContactNotFoundException(100L))
                .when(contactService).deleteContact(anyLong(), eq(2L));

        mockMvc.perform(delete("/contacts/100").with(user(OWNER_TWO)))
                .andExpect(status().isNotFound());

        verify(contactService).deleteContact(100L, 2L);
    }

    @Test
    void deleteContact_returns401WhenUnauthenticated() throws Exception {
        mockMvc.perform(delete("/contacts/100")).andExpect(status().isUnauthorized());
    }
}
