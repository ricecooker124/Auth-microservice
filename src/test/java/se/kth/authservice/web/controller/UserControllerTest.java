package se.kth.authservice.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import se.kth.authservice.domain.AuthUser;
import se.kth.authservice.service.AuthService;
import se.kth.authservice.service.UserService;
import se.kth.authservice.web.dto.AuthDto.Gender;
import se.kth.authservice.web.dto.PractitionerDto.PractitionerRegisterRequest;
import se.kth.authservice.web.dto.PractitionerDto.PractitionerRegisterResponse;
import se.kth.authservice.web.dto.PractitionerDto.PractitionerRole;
import se.kth.authservice.web.dto.UserDto.UserResponse;

import java.time.LocalDate;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;
    @Mock
    private AuthService authService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getAllUsers_Success() throws Exception {
        var users = Arrays.asList(
                new UserResponse(1L, "user1", AuthUser.Role.PATIENT, true, null, null, null, null),
                new UserResponse(2L, "user2", AuthUser.Role.PRACTITIONER, true, null, null, null, null)
        );
        when(userService.findAll()).thenReturn(users);

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void createPractitioner_Success() throws Exception {
        var request = new PractitionerRegisterRequest("doc", "pass", "First", "Last", "198001011234", LocalDate.of(1980, 1, 1), Gender.MALE, PractitionerRole.DOCTOR, 1L);
        var response = new PractitionerRegisterResponse(null, AuthUser.Role.PRACTITIONER, "Practitioner registered successfully");
        when(authService.createPractitionerUser(any())).thenReturn(response);

        mockMvc.perform(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("PRACTITIONER"));
    }
}
