package org.ratha.virtualbookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.ratha.virtualbookstore.DTO.request.AuthRequest;
import org.ratha.virtualbookstore.DTO.respone.AuthResponse;
import org.ratha.virtualbookstore.model.User;
import org.ratha.virtualbookstore.repository.UserRepository;
import org.ratha.virtualbookstore.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "Authentication", description = "Endpoint for authentication operations")
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    @Operation(
            summary = "User Login",
            description = "Authenticates a user and returns a JWT token",
            tags = {"Authentication"}
    )
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userRepository.findByUsername(authRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String jwt = jwtUtil.generateToken(user.getUsername(), user.getRole());

            // Use AuthResponse DTO
            AuthResponse authResponse = new AuthResponse(jwt, user.getUsername(), user.getRole());

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
        SecurityContextHolder.clearContext(); // Clear security context for proper logout
        return ResponseEntity.ok(Map.of("status", "success", "message", "Logged out successfully"));
    }
}