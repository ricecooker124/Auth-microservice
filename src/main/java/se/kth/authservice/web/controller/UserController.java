package se.kth.authservice.web.controller;

import org.springframework.web.bind.annotation.*;
import se.kth.authservice.service.AuthService;
import se.kth.authservice.service.UserService;

import java.util.List;

import static se.kth.authservice.web.dto.PractitionerDto.*;
import static se.kth.authservice.web.dto.UserDto.*;

@RestController
@RequestMapping("/api/admin")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    public UserController(UserService userService,
                          AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    // Admin: se alla users (typ samma som original)
    @GetMapping("/users")
    public List<UserResponse> getAllUsers() {
        return userService.findAll();
    }

    // Admin: skapa practitioner-user (auth-sidan)
    @PostMapping("/create-practitioner")
    public PractitionerRegisterResponse createPractitioner(@RequestBody PractitionerRegisterRequest req) {
        return authService.createPractitionerUser(req);
    }
}