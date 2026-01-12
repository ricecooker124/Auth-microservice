package se.kth.authservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.kth.authservice.domain.AuthUser;
import se.kth.authservice.repository.AuthUserRepository;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private AuthUserRepository userRepo;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepo);
    }

    @Test
    void findAll_ReturnsUsers() {
        var user1 = AuthUser.builder().id(1L).username("user1").password("pass").role(AuthUser.Role.PATIENT).active(true).build();
        var user2 = AuthUser.builder().id(2L).username("user2").password("pass").role(AuthUser.Role.PRACTITIONER).active(true).build();
        when(userRepo.findAll()).thenReturn(Arrays.asList(user1, user2));

        var result = userService.findAll();

        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).username());
    }

    @Test
    void findAll_EmptyList() {
        when(userRepo.findAll()).thenReturn(Collections.emptyList());

        var result = userService.findAll();

        assertTrue(result.isEmpty());
    }
}
