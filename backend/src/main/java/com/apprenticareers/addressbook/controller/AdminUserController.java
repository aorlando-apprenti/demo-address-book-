package com.apprenticareers.addressbook.controller;

import com.apprenticareers.addressbook.dto.CreateUserRequest;
import com.apprenticareers.addressbook.dto.CreateUserResponse;
import com.apprenticareers.addressbook.dto.MessageResponse;
import com.apprenticareers.addressbook.dto.ResetPasswordResponse;
import com.apprenticareers.addressbook.service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    /** FR-04: Admin adds a new user account. */
    @PostMapping
    public ResponseEntity<CreateUserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminUserService.createUser(request));
    }

    /** FR-05: Admin removes an existing user account. */
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> removeUser(@PathVariable Long id) {
        adminUserService.removeUser(id);
        return ResponseEntity.ok(new MessageResponse("User removed successfully."));
    }

    /** FR-06: Admin resets a user's password. */
    @PostMapping("/{id}/reset-password")
    public ResponseEntity<ResetPasswordResponse> resetPassword(@PathVariable Long id) {
        return ResponseEntity.ok(adminUserService.resetUserPassword(id));
    }
}
