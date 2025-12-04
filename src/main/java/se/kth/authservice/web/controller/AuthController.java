package se.kth.authservice.web.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import se.kth.authservice.service.AuthService;

import static se.kth.authservice.web.dto.AuthDto.*;
import static se.kth.authservice.web.dto.PractitionerDto.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
        // se till att admin finns
        this.authService.ensureAdminExists();
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }

    @PostMapping("/register-patient")
    public AuthResponse registerPatient(@Valid @RequestBody PatientRegisterRequest req) {
        return authService.registerPatient(req);
    }

    // Admin-skapande av practitioner-user.
    // Path matchar din gamla backend: /api/admin/create-practitioner
    // MEN för att slippa CORS-strul kan vi även låta den ligga här:
    @PostMapping("/register-practitioner")
    public PractitionerRegisterResponse registerPractitioner(
            @Valid @RequestBody PractitionerRegisterRequest req
    ) {
        return authService.createPractitionerUser(req);
    }
}