package com.shopifake.auth.dto;

public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
    private String username;

    public AuthResponse() {}

    public AuthResponse(String accessToken, String refreshToken, String tokenType, Long expiresIn, String username) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.username = username;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final AuthResponse instance = new AuthResponse();

        public Builder accessToken(String accessToken) { instance.setAccessToken(accessToken); return this; }
        public Builder refreshToken(String refreshToken) { instance.setRefreshToken(refreshToken); return this; }
        public Builder tokenType(String tokenType) { instance.setTokenType(tokenType); return this; }
        public Builder expiresIn(Long expiresIn) { instance.setExpiresIn(expiresIn); return this; }
        public Builder username(String username) { instance.setUsername(username); return this; }
        public AuthResponse build() { return instance; }
    }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public Long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(Long expiresIn) { this.expiresIn = expiresIn; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
