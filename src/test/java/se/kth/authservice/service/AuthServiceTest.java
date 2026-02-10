package se.kth.authservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import se.kth.authservice.domain.AuthUser;
import se.kth.authservice.repository.AuthUserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthUserRepository userRepo;
    @Mock
    private KeycloakAdminService keycloakAdminService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepo, keycloakAdminService, "http://journal-service:8081");
    }

    @Test
    void getUserByUsername_Found() {
        var user = AuthUser.builder().id(1L).username("test").password("pass").role(AuthUser.Role.PATIENT).build();
        when(userRepo.findByUsername("test")).thenReturn(Optional.of(user));

        var result = authService.getUserByUsername("test");

        assertEquals("test", result.getUsername());
        assertEquals(AuthUser.Role.PATIENT, result.getRole());
    }

    @Test
    void getUserByUsername_NotFound() {
        when(userRepo.findByUsername("missing")).thenReturn(Optional.empty());

        var ex = assertThrows(ResponseStatusException.class, () -> authService.getUserByUsername("missing"));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}
