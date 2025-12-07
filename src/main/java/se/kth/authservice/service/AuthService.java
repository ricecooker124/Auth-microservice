package se.kth.authservice.service;

import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import se.kth.authservice.domain.AuthUser;
import se.kth.authservice.repository.AuthUserRepository;
import se.kth.authservice.web.dto.AuthDto;
import se.kth.authservice.web.dto.PractitionerDto;
import java.util.Optional;

import java.time.LocalDate;

import static se.kth.authservice.web.dto.AuthDto.*;
import static se.kth.authservice.web.dto.PractitionerDto.*;

@Service
public class AuthService {

    private final AuthUserRepository userRepo;
    private final RestClient restClient;

    public AuthService(AuthUserRepository userRepo) {
        this.userRepo = userRepo;
        // Viktigt: hostnamn = service-namnet i docker-compose (journal-service)
        this.restClient = RestClient.builder()
                .baseUrl("http://journal-service:8081")
                .build();
    }

    // -------------------------
    // LOGIN
    // -------------------------
    public AuthResponse login(LoginRequest req) {
        AuthUser user = userRepo.findByUsername(req.username())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!user.getPassword().equals(req.password())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid password");
        }

        return new AuthResponse(
                "fake-jwt-token",
                user.getRole(),
                "Login successful"
        );
    }

    // -------------------------
    // REGISTER PATIENT
    // -------------------------
    @Transactional
    public AuthResponse registerPatient(PatientRegisterRequest req) {

        // Username måste vara unikt i auth-systemet
        if (userRepo.existsByUsername(req.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        AuthUser newUser = AuthUser.builder()
                .username(req.username())
                .password(req.password()) // plaintext i lab
                .role(AuthUser.Role.PATIENT)
                .active(true)
                .build();

        userRepo.save(newUser);

        // ⚡ Skicka vidare patient-info till journal-microservice
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
                "Patient user registered successfully (auth-service + journal-service)"
        );
    }

    // -------------------------
    // ADMIN: CREATE PRACTITIONER USER
    // -------------------------
    @Transactional
    public PractitionerRegisterResponse createPractitionerUser(PractitionerRegisterRequest req) {

        if (userRepo.existsByUsername(req.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        AuthUser newUser = AuthUser.builder()
                .username(req.username())
                .password(req.password())
                .role(AuthUser.Role.PRACTITIONER)
                .active(true)
                .build();

        userRepo.save(newUser);

        // ⚡ Skicka practitioner-data till journal-microservice
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
                "Practitioner user registered successfully (auth-service + journal-service)"
        );
    }

    // -------------------------
    // INIT ADMIN
    // -------------------------
    @Transactional
    public void ensureAdminExists() {
        boolean adminExists = userRepo.existsByRole(AuthUser.Role.ADMIN);
        if (!adminExists) {
            AuthUser admin = AuthUser.builder()
                    .username("admin")
                    .password("admin") // labb: enkel default
                    .role(AuthUser.Role.ADMIN)
                    .active(true)
                    .build();
            userRepo.save(admin);
        }
    }

    public AuthUser getUserByUsername(String username) {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));
    }

    // -------------------------
    // Internal DTOs for journal-service calls
    // -------------------------
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