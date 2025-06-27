package org.ratha.virtualbookstore.DTO.response;


import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(hidden = true)
public class AuthResponse {
    private String token;
    private String username;
    private String role;

    public AuthResponse(String token, String username, String role) {
        this.token = token;
        this.username = username;
        this.role = role;
    }

    // Getters and setters (or use Lombok @Data for brevity)

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
