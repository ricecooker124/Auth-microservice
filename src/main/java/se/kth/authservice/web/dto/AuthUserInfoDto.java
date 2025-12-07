package se.kth.authservice.web.dto;

import se.kth.authservice.domain.AuthUser;

public class AuthUserInfoDto {

    public static record AuthUserInfoResponse(
            Long id,
            String username,
            AuthUser.Role role,
            boolean active
    ) {}
}