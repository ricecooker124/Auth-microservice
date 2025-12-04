package se.kth.authservice.service;

import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import se.kth.authservice.domain.AuthUser;
import se.kth.authservice.repository.AuthUserRepository;

import static se.kth.authservice.web.dto.AuthDto.*;
import static se.kth.authservice.web.dto.PractitionerDto.*;

@Service
public class AuthService {

    private final AuthUserRepository userRepo;

    public AuthService(AuthUserRepository userRepo) {
        this.userRepo = userRepo;
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

        // Samma fejk-token-upplägg som innan
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

        // OBS: SSN-unikhet sköts nu av journal-microservice
        // (Patient-tabellen där). Här bryr vi oss bara om användaren.

        AuthUser newUser = AuthUser.builder()
                .username(req.username())
                .password(req.password()) // plaintext i lab
                .role(AuthUser.Role.PATIENT)
                .active(true)
                .build();

        userRepo.save(newUser);

        // TODO: i en "riktig" microservice-setup skulle vi här
        // trigga ett event / HTTP-call till journal-service
        // för att skapa Patient där baserat på firstName, lastName, ssn osv.

        return new AuthResponse(
                null,
                AuthUser.Role.PATIENT,
                "Patient user registered successfully (auth-service)"
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

        // Som med patient: själva Practitioner-entiteten i journal-DB
        // får skapas via journal-service senare.
        return new PractitionerRegisterResponse(
                null,
                AuthUser.Role.PRACTITIONER,
                "Practitioner user registered successfully (auth-service)"
        );
    }

    // -------------------------
    // INIT ADMIN (om du vill ha det)
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
}