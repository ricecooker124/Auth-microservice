package se.kth.authservice.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import se.kth.authservice.domain.AuthUser;
import se.kth.authservice.web.dto.AuthDto.Gender;

import java.time.LocalDate;

public class PractitionerDto {

    // separat enum för practitioner-roll i journalvärlden,
    // men AuthUser.Role.PRACTITIONER används på auth-sidan.
    public enum PractitionerRole { DOCTOR, STAFF }

    public static record PractitionerRegisterRequest(
            @NotBlank String username,
            @NotBlank String password,
            @NotBlank String firstName,
            @NotBlank String lastName,
            @NotBlank String ssn,
            @NotNull LocalDate birthDate,
            @NotNull Gender gender,
            @NotNull PractitionerRole role,
            Long organizationId // journal-service bryr sig om detta senare
    ) {}

    public static record PractitionerRegisterResponse(
            String token,
            AuthUser.Role role,
            String message
    ) {}
}