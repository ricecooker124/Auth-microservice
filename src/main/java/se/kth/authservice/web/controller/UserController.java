package se.kth.authservice.web.controller;

import org.springframework.http.HttpStatus;
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

    // ---------------- LISTA USERS ----------------

    @GetMapping("/users")
    public List<UserResponse> getAllUsers() {
        return userService.findAll();
    }

    // ---------------- SKAPA PRACTITIONER (nytt: POST /api/admin/users) ----------------

    /**
     * Den här endpointen är till för admin-frontend (och Postman):
     * POST /api/admin/users
     *
     * Body (exempel):
     * {
     *   "username": "doctor1",
     *   "password": "doc123",
     *   "firstName": "Josefine",
     *   "lastName": "Hossain",
     *   "ssn": "20060125-1234",
     *   "birthDate": "2006-01-25",
     *   "gender": "MALE",
     *   "role": "DOCTOR",
     *   "organizationId": null
     * }
     */
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public PractitionerRegisterResponse createPractitionerViaUsers(
            @RequestBody PractitionerRegisterRequest req
    ) {
        return authService.createPractitionerUser(req);
    }

    // ---------------- ALTERNATIV PATH (du kan behålla eller ta bort) ----------------

    /**
     * Extra alias: /api/admin/create-practitioner
     * Behövs egentligen inte om frontend bara använder /api/admin/users,
     * men du kan låta den vara kvar.
     */
    @PostMapping("/create-practitioner")
    @ResponseStatus(HttpStatus.CREATED)
    public PractitionerRegisterResponse createPractitioner(
            @RequestBody PractitionerRegisterRequest req
    ) {
        return authService.createPractitionerUser(req);
    }
}