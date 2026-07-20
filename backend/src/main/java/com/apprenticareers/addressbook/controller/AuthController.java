package com.apprenticareers.addressbook.controller;

import com.apprenticareers.addressbook.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // Implementation to follow in Iteration 1 tasks
    // @PostMapping("/register") - register(RegisterRequest)
    // @PostMapping("/login") - login(LoginRequest) -> JWT response
}
