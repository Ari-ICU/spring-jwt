package org.ratha.virtualbookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.ratha.virtualbookstore.DTO.request.AuthRequest;
import org.ratha.virtualbookstore.DTO.response.AuthResponse;
import org.ratha.virtualbookstore.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "Authentication", description = "Endpoint for authentication operations")
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    @Operation(
            summary = "User Login",
            description = "Authenticates a user and returns a JWT token",
            tags = {"Authentication"}
    )
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            AuthResponse authResponse = authService.login(authRequest);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("data", authResponse);

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid username or password"));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(Map.of("error", "Login failed: " + ex.getMessage()));
        }
    }

    @PostMapping("/logout")
    @Operation(
            summary = "User Logout",
            description = "Logs out the current user and clears the security context",
            tags = {"Authentication"}
    )
    public ResponseEntity<?> logout() {
        authService.logout();
        return ResponseEntity.ok(Map.of("status", "success", "message", "Logged out successfully"));
    }
}