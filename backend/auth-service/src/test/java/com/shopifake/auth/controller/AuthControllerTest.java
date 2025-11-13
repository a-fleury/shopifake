package com.shopifake.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopifake.auth.dto.*;
import com.shopifake.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

class AuthControllerTest {

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

        // A tiny test stub subclass to avoid hitting real Keycloak/HTTP in unit tests
        static class StubAuthService extends AuthService {
                StubAuthService() {
                        super(null);
                }

                // We'll override methods in each test by creating anonymous subclasses
        }
        StubAuthService authService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        // default - simple stub instance (methods overridden per-test as needed)
        this.authService = new StubAuthService();
        AuthController controller = new AuthController(this.authService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("GET /api/v1/auth/health returns OK message")
    void healthEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/auth/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Auth Service is running"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login returns tokens on success")
    void loginSuccess() throws Exception {
        LoginRequest req = new LoginRequest("alice", "p@ssword");
        AuthResponse resp = AuthResponse.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .expiresIn(3600L)
                .tokenType("Bearer")
                .scope("openid profile email")
                .build();

                // replace stub with anonymous subclass that returns the expected response
                this.authService = new StubAuthService() {
                        @Override
                        public AuthResponse login(LoginRequest request) {
                                return resp;
                        }
                };
                this.mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(this.authService)).build();

                mockMvc.perform(post("/api/v1/auth/login")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.accessToken", is("access-token")))
                                .andExpect(jsonPath("$.refreshToken", is("refresh-token")))
                                .andExpect(jsonPath("$.tokenType", is("Bearer")));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login returns 400 on failure")
    void loginFailure() throws Exception {
        LoginRequest req = new LoginRequest("bob", "wrong");
                this.authService = new StubAuthService() {
                        @Override
                        public AuthResponse login(LoginRequest request) {
                                throw new RuntimeException("auth failed");
                        }
                };
                this.mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(this.authService)).build();

                mockMvc.perform(post("/api/v1/auth/login")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/auth/register returns created user")
    void registerSuccess() throws Exception {
        RegisterRequest req = new RegisterRequest("charlie", "charlie@example.com", "pwd", "Charlie", "Brown");
        UserResponse resp = UserResponse.builder()
                .id("user-123")
                .username("charlie")
                .email("charlie@example.com")
                .firstName("Charlie")
                .lastName("Brown")
                .enabled(true)
                .build();

                this.authService = new StubAuthService() {
                        @Override
                        public UserResponse register(RegisterRequest request) {
                                return resp;
                        }
                };
                this.mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(this.authService)).build();

                mockMvc.perform(post("/api/v1/auth/register")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.username", is("charlie")))
                                .andExpect(jsonPath("$.email", is("charlie@example.com")));
    }

    @Test
    @DisplayName("GET /api/v1/auth/user returns user info when authorized")
    void getUserInfoSuccess() throws Exception {
        UserResponse resp = UserResponse.builder()
                .id("u-1")
                .username("alice")
                .email("alice@example.com")
                .firstName("Alice")
                .lastName("Liddell")
                .enabled(true)
                .build();

                this.authService = new StubAuthService() {
                        @Override
                        public UserResponse getUserInfo(String token) {
                                return resp;
                        }
                };
                this.mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(this.authService)).build();

                mockMvc.perform(get("/api/v1/auth/user")
                                                .header("Authorization", "Bearer valid-token"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.username", is("alice")))
                                .andExpect(jsonPath("$.email", is("alice@example.com")));
    }

    @Test
    @DisplayName("GET /api/v1/auth/user returns 400 on invalid token")
    void getUserInfoFailure() throws Exception {
                this.authService = new StubAuthService() {
                        @Override
                        public UserResponse getUserInfo(String token) {
                                throw new RuntimeException("invalid token");
                        }
                };
                this.mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(this.authService)).build();

                mockMvc.perform(get("/api/v1/auth/user")
                                                .header("Authorization", "Bearer bad-token"))
                                .andExpect(status().isBadRequest());
    }
}
