package com.workforcehub.dto.response;

import lombok.*;

import java.util.List;

/**
 * JWT authentication response DTO.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
    private String username;
    private String email;
    private List<String> roles;
}
