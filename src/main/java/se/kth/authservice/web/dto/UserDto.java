package se.kth.authservice.web.dto;

import se.kth.authservice.domain.AuthUser;

public class UserDto {

    // Vi behåller samma shape som din gamla UserResponse
    // men organization/namn sätts till null här.
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