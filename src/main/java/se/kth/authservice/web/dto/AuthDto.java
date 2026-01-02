package se.kth.authservice.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import se.kth.authservice.domain.AuthUser;

import java.time.LocalDate;

public class AuthDto {

    public enum Gender { MALE, FEMALE, OTHER, UNKNOWN }

    public static record LoginRequest(
            @NotBlank String username,
            @NotBlank String password
    ) {}

    public static record PatientRegisterRequest(
            @NotBlank String username,
            @NotBlank String password,
            @NotBlank String firstName,
            @NotBlank String lastName,
            @NotBlank String ssn,
            @NotNull LocalDate birthDate,
            @NotNull Gender gender
    ) {}

    public static record AuthResponse(
            String token,
            AuthUser.Role role,
            String message
    ) {}
}