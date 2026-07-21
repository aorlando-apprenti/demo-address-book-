package com.apprenticareers.addressbook.dto;

import com.apprenticareers.addressbook.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String email;
    private String address;
    private String telephoneNumber;
    private User.Role role;
    private LocalDateTime createdAt;

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getAddress(),
                user.getTelephoneNumber(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
