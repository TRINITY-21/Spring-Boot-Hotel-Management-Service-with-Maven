package com.hotel.hotel.interceptor;

import com.hotel.hotel.service.UserService;
import com.hotel.hotel.utils.JWTUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private UserService userService;

    // Method intercepts requests to validate JWT tokens and setup authentication context if valid
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Get the Authorization header from the request
        final String authorizationHeader = request.getHeader("Authorization");

        // Extract token from the Authorization header if it starts with "Bearer "
        String token = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7); // Extract token by removing "Bearer " prefix
        }

        // Extract the user email (username) from the token
        final String userEmail = token != null ? jwtUtils.extractUsername(token) : null;

        // If user email is found, and no authentication is currently set in the SecurityContext
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Load user details by the extracted user email
            UserDetails userDetails = userService.loadUserByUsername(userEmail);

            // Validate the token against the user details to ensure it’s valid
            if (userDetails != null && jwtUtils.isValidToken(token, userDetails)) {

                // Create an authentication token and populate it with the user details
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // Add request details to the authentication token (e.g., remote IP address)
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set the created authentication token into the SecurityContext
                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                securityContext.setAuthentication(authenticationToken);
                SecurityContextHolder.setContext(securityContext);
            }
        }

        // Continue with the filter chain to process the next filter or target resource
        filterChain.doFilter(request, response);
    }
}
