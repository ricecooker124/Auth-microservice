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
import se.kth.authservice.web.dto.AuthDto.AuthResponse;
import se.kth.authservice.web.dto.AuthDto.Gender;
import se.kth.authservice.web.dto.AuthDto.PatientRegisterRequest;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void registerPatient_Success() throws Exception {
        var request = new PatientRegisterRequest("user", "pass", "First", "Last", "199001011234", LocalDate.of(1990, 1, 1), Gender.MALE);
        var response = new AuthResponse(null, AuthUser.Role.PATIENT, "Patient registered successfully");
        when(authService.registerPatient(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/register-patient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("PATIENT"));
    }

    @Test
    void getUser_Success() throws Exception {
        var user = AuthUser.builder().id(1L).username("test").password("pass").role(AuthUser.Role.PATIENT).active(true).build();
        when(authService.getUserByUsername("test")).thenReturn(user);

        mockMvc.perform(get("/api/auth/user").param("username", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("test"));
    }
}

