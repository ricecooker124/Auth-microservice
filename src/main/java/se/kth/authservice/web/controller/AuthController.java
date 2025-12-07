package se.kth.authservice.web.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import se.kth.authservice.domain.AuthUser;
import se.kth.authservice.service.AuthService;

import static se.kth.authservice.web.dto.AuthDto.*;
import static se.kth.authservice.web.dto.AuthUserInfoDto.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
        this.authService.ensureAdminExists();
    }

    // --- LOGIN ---
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }

    // --- REGISTRERA PATIENT (öppen, t.ex. via register-formuläret) ---
    @PostMapping("/register-patient")
    public AuthResponse registerPatient(@Valid @RequestBody PatientRegisterRequest req) {
        return authService.registerPatient(req);
    }

    // 🚫 INGEN register-practitioner här längre.
    // Practitioners skapas enbart via admin-endpoints i UserController:
    // POST /api/admin/users  eller  POST /api/admin/create-practitioner

    // --- HÄMTA USER-INFO ---
    @GetMapping("/user")
    public AuthUserInfoResponse getUser(@RequestParam String username) {
        AuthUser u = authService.getUserByUsername(username);
        return new AuthUserInfoResponse(
                u.getId(),
                u.getUsername(),
                u.getRole(),
                u.isActive()
        );
    }
}