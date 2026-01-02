package se.kth.authservice.web.dto;

import se.kth.authservice.domain.AuthUser;

public class UserDto {

    public static record UserResponse(
            Long id,
            String username,
            AuthUser.Role role,
            boolean active,
            Long organizationId,
            String organizationName,
            String firstName,
            String lastName
    ) {}
}