package com.apprenticareers.addressbook.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String tokenType = "Bearer";
    private String email;
    private String role;

    public AuthResponse(String token, String email, String role) {
        this.token = token;
        this.tokenType = "Bearer";
        this.email = email;
        this.role = role;
    }
}
