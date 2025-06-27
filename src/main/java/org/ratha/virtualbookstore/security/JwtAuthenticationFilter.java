package org.ratha.virtualbookstore.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtils;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtils, CustomUserDetailsService customUserDetailsService) {
        this.jwtUtils = jwtUtils;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Skip JWT validation for public routes and Swagger
        if (path.startsWith("/api/auth/") ||
                path.startsWith("/api/") ||
                path.startsWith("/swagger-ui/") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-ui.html")) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        String token = null;
        String username = null;

        filterChain.doFilter(request, response);
    }
}