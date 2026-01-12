package se.kth.authservice.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import se.kth.authservice.domain.AuthUser;
import se.kth.authservice.repository.AuthUserRepository;

import java.time.LocalDate;

import static se.kth.authservice.web.dto.AuthDto.*;
import static se.kth.authservice.web.dto.PractitionerDto.*;

@Service
public class AuthService {

    private final AuthUserRepository userRepo;
    private final RestClient restClient;
    private final KeycloakAdminService keycloakAdminService;

    public AuthService(AuthUserRepository userRepo, KeycloakAdminService keycloakAdminService) {
        this.userRepo = userRepo;
        this.keycloakAdminService = keycloakAdminService;
        this.restClient = RestClient.builder()
                .baseUrl("http://journal-service:8081")
                .build();
    }

    @Transactional
    public AuthResponse registerPatient(PatientRegisterRequest req) {

        if (userRepo.existsByUsername(req.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        String keycloakUserId = null;

        try {
            keycloakUserId = keycloakAdminService.createUser(
                    req.username(),
                    req.password(),
                    req.username() + "@example.com",
                    "PATIENT"
            );

            AuthUser newUser = AuthUser.builder()
                    .username(req.username())
                    .password(req.password())
                    .role(AuthUser.Role.PATIENT)
                    .active(true)
                    .build();

            userRepo.save(newUser);

            CreatePatientInternalRequest payload = new CreatePatientInternalRequest(
                    req.username(),
                    req.firstName(),
                    req.lastName(),
                    req.ssn(),
                    req.birthDate(),
                    req.gender().name()
            );

            restClient.post()
                    .uri("/internal/patients")
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();

            return new AuthResponse(
                    null,
                    AuthUser.Role.PATIENT,
                    "Patient registered successfully"
            );

        } catch (Exception e) {
            System.err.println("=== REGISTRATION ERROR ===");
            e.printStackTrace();

            if (keycloakUserId != null) {
                try {
                    keycloakAdminService.deleteUser(keycloakUserId);
                } catch (Exception rollbackEx) {
                    System.err.println("Rollback failed: " + rollbackEx.getMessage());
                }
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Registration failed: " + e.getMessage());
        }
    }

    @Transactional
    public AuthResponse completePatientProfile(CompleteProfileRequest req) {

        if (userRepo.existsByUsername(req.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Profile already completed");
        }

        try {
            // 1. Assign PATIENT role in Keycloak
            keycloakAdminService.assignRoleToUser(req.username(), "PATIENT");

            // 2. Create user in our auth database
            AuthUser newUser = AuthUser.builder()
                    .username(req.username())
                    .password("KEYCLOAK_MANAGED")
                    .role(AuthUser.Role.PATIENT)
                    .active(true)
                    .build();

            userRepo.save(newUser);

            // 3. Create patient record in journal-service
            CreatePatientInternalRequest payload = new CreatePatientInternalRequest(
                    req.username(),
                    req.firstName(),
                    req.lastName(),
                    req.ssn(),
                    req.birthDate(),
                    req.gender().name()
            );

            restClient.post()
                    .uri("/internal/patients")
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();

            return new AuthResponse(
                    null,
                    AuthUser.Role.PATIENT,
                    "Profile completed successfully"
            );

        } catch (Exception e) {
            System.err.println("=== COMPLETE PROFILE ERROR ===");
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to complete profile: " + e.getMessage());
        }
    }

    @Transactional
    public PractitionerRegisterResponse createPractitionerUser(PractitionerRegisterRequest req) {

        if (userRepo.existsByUsername(req.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        String keycloakUserId = null;

        try {
            keycloakUserId = keycloakAdminService.createUser(
                    req.username(),
                    req.password(),
                    req.username() + "@example.com",
                    "PRACTITIONER"
            );

            AuthUser newUser = AuthUser.builder()
                    .username(req.username())
                    .password(req.password())
                    .role(AuthUser.Role.PRACTITIONER)
                    .active(true)
                    .build();

            userRepo.save(newUser);

            CreatePractitionerInternalRequest payload = new CreatePractitionerInternalRequest(
                    req.username(),
                    req.firstName(),
                    req.lastName(),
                    req.ssn(),
                    req.birthDate(),
                    req.gender().name(),
                    req.role().name(),
                    req.organizationId()
            );

            restClient.post()
                    .uri("/internal/practitioners")
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();

            return new PractitionerRegisterResponse(
                    null,
                    AuthUser.Role.PRACTITIONER,
                    "Practitioner registered successfully"
            );

        } catch (Exception e) {
            System.err.println("=== REGISTRATION ERROR ===");
            e.printStackTrace();

            if (keycloakUserId != null) {
                try {
                    keycloakAdminService.deleteUser(keycloakUserId);
                } catch (Exception rollbackEx) {
                    System.err.println("Rollback failed: " + rollbackEx.getMessage());
                }
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Registration failed: " + e.getMessage());
        }
    }

    public AuthUser getUserByUsername(String username) {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));
    }

    private record CreatePatientInternalRequest(
            String username,
            String firstName,
            String lastName,
            String ssn,
            LocalDate birthDate,
            String gender
    ) {}

    private record CreatePractitionerInternalRequest(
            String username,
            String firstName,
            String lastName,
            String ssn,
            LocalDate birthDate,
            String gender,
            String role,
            Long organizationId
    ) {}
}