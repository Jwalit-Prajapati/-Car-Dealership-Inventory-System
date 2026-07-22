package com.jwalit.inventory_system.controller;


import com.jwalit.inventory_system.dto.LoginRequest;
import com.jwalit.inventory_system.dto.LoginResponse;
import com.jwalit.inventory_system.dto.RegistrationRequest;
import com.jwalit.inventory_system.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;



    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void registerUser_validRequest_returns201() throws Exception {
        String json = """
        {
            "firstName": "John",
            "lastName": "Doe",
            "email": "john@example.com",
            "password": "password123"
        }
        """;

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void registerUser_invalidEmail_returns400() throws Exception {
        String json = """
        {
            "firstName": "John",
            "lastName": "Doe",
            "email": "invalid",
            "password": "password123"
        }
        """;

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest()); // Note: Since this is standalone setup without ControllerAdvice, it may return 400 if validation is triggered via Valid, we'll implement this properly in GREEN.
    }
    
    @Test
    void registerUser_missingFirstName_returns400() throws Exception {
        String json = """
        {
            "firstName": null,
            "lastName": "Doe",
            "email": "john@example.com",
            "password": "password123"
        }
        """;

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void registerUser_passwordTooShort_returns400() throws Exception {
        String json = """
        {
            "firstName": "John",
            "lastName": "Doe",
            "email": "john@example.com",
            "password": "short"
        }
        """;

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_duplicateEmail_returns400() throws Exception {
        String json = """
        {
            "firstName": "John",
            "lastName": "Doe",
            "email": "john@example.com",
            "password": "password123"
        }
        """;
        
        doThrow(new IllegalArgumentException("Email already in use"))
            .when(authService).register(any(RegistrationRequest.class));

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_validRequest_returns200AndToken() throws Exception {
        String json = """
        {
            "email": "user@example.com",
            "password": "password123"
        }
        """;
        
        org.mockito.BDDMockito.given(authService.login(any(LoginRequest.class))).willReturn(new LoginResponse("fake-jwt-token"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"));
    }

    @Test
    void login_invalidCredentials_returns401() throws Exception {
        String json = """
        {
            "email": "user@example.com",
            "password": "wrongpassword"
        }
        """;

        org.mockito.BDDMockito.given(authService.login(any(LoginRequest.class)))
            .willThrow(new org.springframework.security.authentication.BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_blankFields_returns400() throws Exception {
        String json = """
        {
            "email": "",
            "password": ""
        }
        """;

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }
}
