package com.apprenticareers.addressbook.controller;

import com.apprenticareers.addressbook.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AuthService authService;

    // Implementation to follow in Iteration 1 tasks
    // @PutMapping("/password") - changePassword(ChangePasswordRequest)
}
