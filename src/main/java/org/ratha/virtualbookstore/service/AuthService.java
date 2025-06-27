package org.ratha.virtualbookstore.service;

import org.ratha.virtualbookstore.DTO.request.AuthRequest;
import org.ratha.virtualbookstore.DTO.response.AuthResponse;
import org.ratha.virtualbookstore.model.User;
import org.ratha.virtualbookstore.repository.UserRepository;
import org.ratha.virtualbookstore.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    public AuthResponse login(AuthRequest authRequest) {
        try {
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Find the user
            User user = userRepository.findByUsername(authRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Generate JWT token
            String jwt = jwtUtil.generateToken(user.getUsername(), user.getRole());

            // Return AuthResponse
            return new AuthResponse(jwt, user.getUsername(), user.getRole());

        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Invalid username or password");
        } catch (RuntimeException ex) {
            throw ex; // User not found
        } catch (Exception ex) {
            throw new RuntimeException("Login failed: " + ex.getMessage());
        }
    }

    public void logout() {
        SecurityContextHolder.clearContext();
    }
}