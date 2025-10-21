package com.shopifake.auth.service;

import com.shopifake.auth.dto.AuthResponse;
import com.shopifake.auth.dto.LoginRequest;
import com.shopifake.auth.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private Keycloak keycloak;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RealmResource realmResource;

    @Mock
    private UsersResource usersResource;

    @Mock
    private Response keycloakResponse;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        // Inject les valeurs nécessaires via reflection
    ReflectionTestUtils.setField(authService, "serverUrl", "http://keycloak:8080");
        ReflectionTestUtils.setField(authService, "realm", "shopifake");
        ReflectionTestUtils.setField(authService, "clientId", "shopifake-client");
        ReflectionTestUtils.setField(authService, "clientSecret", "secret");
        ReflectionTestUtils.setField(authService, "restTemplate", restTemplate);
    }

    @Test
    void login_withValidCredentials_shouldReturnAuthResponse() {
        // Given
        LoginRequest request = new LoginRequest();
        ReflectionTestUtils.setField(request, "username", "testuser");
        ReflectionTestUtils.setField(request, "password", "password123");

        Map<String, Object> tokenResponse = Map.of(
                "access_token", "access-token-value",
                "refresh_token", "refresh-token-value",
                "token_type", "Bearer",
                "expires_in", 300
        );

        ResponseEntity<Map> responseEntity = ResponseEntity.ok(tokenResponse);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(responseEntity);

        // When
        AuthResponse response = authService.login(request);

        // Then
        assertNotNull(response);
        assertEquals("access-token-value", response.getAccessToken());
        assertEquals("refresh-token-value", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(300L, response.getExpiresIn());
        assertEquals("testuser", response.getUsername());

        verify(restTemplate).exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        );
    }

    @Test
    void refreshToken_shouldReturnNewTokens() {
        // Given
        String refreshToken = "valid-refresh-token";

        Map<String, Object> tokenResponse = Map.of(
                "access_token", "new-access-token",
                "refresh_token", "new-refresh-token",
                "token_type", "Bearer",
                "expires_in", 300
        );

        ResponseEntity<Map> responseEntity = ResponseEntity.ok(tokenResponse);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(responseEntity);

        // When
        AuthResponse response = authService.refreshToken(refreshToken);

        // Then
        assertNotNull(response);
        assertEquals("new-access-token", response.getAccessToken());
        assertEquals("new-refresh-token", response.getRefreshToken());
        verify(restTemplate).exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        );
    }

    @Test
    void register_withValidData_shouldCreateUser() {
        // Given
        RegisterRequest request = new RegisterRequest();
        ReflectionTestUtils.setField(request, "username", "newuser");
        ReflectionTestUtils.setField(request, "email", "newuser@example.com");
        ReflectionTestUtils.setField(request, "password", "password123");
        ReflectionTestUtils.setField(request, "firstName", "New");
        ReflectionTestUtils.setField(request, "lastName", "User");

        org.keycloak.admin.client.resource.UserResource userResource = mock(org.keycloak.admin.client.resource.UserResource.class);

        when(keycloak.realm(anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.create(any())).thenReturn(keycloakResponse);
        when(keycloakResponse.getStatus()).thenReturn(201);
    when(keycloakResponse.getLocation()).thenReturn(URI.create("http://keycloak:8080/users/user-id-123"));
        when(usersResource.get("user-id-123")).thenReturn(userResource);

        // When
        String result = authService.register(request);

        // Then
        assertEquals("User registered successfully", result);
        verify(keycloak).realm("shopifake");
        verify(realmResource).users();
        verify(usersResource).create(any());
        verify(userResource).resetPassword(any());
    }

    @Test
    void register_whenCreationFails_shouldThrowException() {
        // Given
        RegisterRequest request = new RegisterRequest();
        ReflectionTestUtils.setField(request, "username", "newuser");
        ReflectionTestUtils.setField(request, "email", "newuser@example.com");
        ReflectionTestUtils.setField(request, "password", "password123");

        when(keycloak.realm(anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.create(any())).thenReturn(keycloakResponse);
        when(keycloakResponse.getStatus()).thenReturn(409); // Conflict

        // When & Then
        assertThrows(RuntimeException.class, () -> authService.register(request));
    }
}
