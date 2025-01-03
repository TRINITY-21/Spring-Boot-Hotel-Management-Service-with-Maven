package com.hotel.hotel.configs;

import com.hotel.hotel.interceptor.JWTAuthFilter;
import com.hotel.hotel.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Enables method-level security with annotations
public class SpringSecurityConfig {

    @Autowired
    private UserService userService;

    @Autowired
    private JWTAuthFilter jwtAuthFilter;

    // Configures the main security filter chain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable) // Disables CSRF protection since JWT is used
                .cors(Customizer.withDefaults()) // Configures CORS with default settings
                .authorizeHttpRequests(authorizeRequests -> // Sets up endpoint-specific authorization
                        authorizeRequests
                                .requestMatchers(
                                        "/api/auth/**", // Allows open access to authentication endpoints
                                        "/api/rooms/**",  // Allows open access to rooms endpoints
                                        "/api/bookings/**" // Allows open access to booking endpoints
                                ).permitAll() // These endpoints are public
                                .anyRequest().authenticated() // All other requests require authentication
                )
                .sessionManagement(sessionManager -> // Sets session management policy
                        sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Stateless sessions for JWT
                )
                .authenticationProvider(authenticationProvider()) // Sets up the custom authentication provider
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // Adds JWT filter before authentication
        return httpSecurity.build(); // Builds and returns the security configuration
    }

    // Configures the custom authentication provider using UserService for user details
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userService); // Uses UserService to retrieve user details
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder()); // Sets password encoder for password hashing
        return daoAuthenticationProvider;
    }

    // Configures BCrypt as the password encoder for hashing user passwords
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configures the AuthenticationManager with the custom authentication provider
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationProvider authenticationProvider) {
        return new ProviderManager(authenticationProvider); // ProviderManager allows custom providers
    }

}
