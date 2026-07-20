package com.apprenticareers.addressbook.controller;

import com.apprenticareers.addressbook.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    // Implementation to follow in Iteration 1 tasks
    // @PostMapping - createUser(CreateUserRequest)
    // @DeleteMapping("/{id}") - removeUser(id)
    // @PostMapping("/{id}/reset-password") - resetUserPassword(id)
}
