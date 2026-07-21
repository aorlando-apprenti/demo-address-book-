package com.apprenticareers.addressbook.controller;

import com.apprenticareers.addressbook.dto.ChangePasswordRequest;
import com.apprenticareers.addressbook.dto.MessageResponse;
import com.apprenticareers.addressbook.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AuthService authService;

    /**
     * FR-03: self-service password change, available to any authenticated
     * account (USER or ADMIN) for their own account only.
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/password")
    public ResponseEntity<MessageResponse> changePassword(Authentication authentication,
                                                           @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(authentication.getName(), request);
        return ResponseEntity.ok(new MessageResponse("Password updated successfully."));
    }
}
