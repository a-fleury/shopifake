package com.shopifake.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopifake.auth.dto.AuthResponse;
import com.shopifake.auth.dto.LoginRequest;
import com.shopifake.auth.dto.RegisterRequest;
import com.shopifake.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    void healthCheck_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/auth/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("auth-service"));
    }

    @Test
    void login_withValidCredentials_shouldReturnTokens() throws Exception {
        // Given
        String requestJson = """
                {
                    "username": "testuser",
                    "password": "password123"
                }
                """;

        AuthResponse response = new AuthResponse("eyJhbGc...", "eyJhbGc...", "Bearer", 300L, "testuser");

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("eyJhbGc..."))
                .andExpect(jsonPath("$.refreshToken").value("eyJhbGc..."))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(300))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void login_withInvalidCredentials_shouldReturnUnauthorized() throws Exception {
        // Given
        String requestJson = """
                {
                    "username": "testuser",
                    "password": "wrongpassword"
                }
                """;

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_withMissingUsername_shouldReturnBadRequest() throws Exception {
        // Given
        String requestJson = """
                {
                    "password": "password123"
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_withMissingPassword_shouldReturnBadRequest() throws Exception {
        // Given
        String requestJson = """
                {
                    "username": "testuser"
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_withValidData_shouldReturnCreated() throws Exception {
        // Given
        String requestJson = """
                {
                    "username": "newuser",
                    "email": "newuser@example.com",
                    "password": "password123",
                    "firstName": "New",
                    "lastName": "User"
                }
                """;

        when(authService.register(any(RegisterRequest.class)))
                .thenReturn("User registered successfully");

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User registered successfully"));
    }

    @Test
    void register_withExistingUsername_shouldReturnBadRequest() throws Exception {
        // Given
        String requestJson = """
                {
                    "username": "existinguser",
                    "email": "existing@example.com",
                    "password": "password123"
                }
                """;

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new RuntimeException("User already exists"));

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void register_withInvalidEmail_shouldReturnBadRequest() throws Exception {
        // Given
        String requestJson = """
                {
                    "username": "newuser",
                    "email": "invalid-email",
                    "password": "password123"
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_withShortPassword_shouldReturnBadRequest() throws Exception {
        // Given
        String requestJson = """
                {
                    "username": "newuser",
                    "email": "newuser@example.com",
                    "password": "123"
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void refreshToken_withValidToken_shouldReturnNewTokens() throws Exception {
        // Given
        String requestJson = """
                {
                    "refreshToken": "valid-refresh-token"
                }
                """;

        AuthResponse response = new AuthResponse("new-access-token", "new-refresh-token", "Bearer", 300L, null);

        when(authService.refreshToken("valid-refresh-token")).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/auth/refresh")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("new-refresh-token"));
    }

    @Test
    void refreshToken_withInvalidToken_shouldReturnUnauthorized() throws Exception {
        // Given
        String requestJson = """
                {
                    "refreshToken": "invalid-refresh-token"
                }
                """;

        when(authService.refreshToken("invalid-refresh-token"))
                .thenThrow(new RuntimeException("Invalid refresh token"));

        // When & Then
        mockMvc.perform(post("/api/auth/refresh")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isUnauthorized());
    }
}
