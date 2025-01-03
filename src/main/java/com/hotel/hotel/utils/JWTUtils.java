package com.hotel.hotel.utils;

import com.hotel.hotel.constants.JWTConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Service
public class JWTUtils {

    // Secret key used for signing the JWT
    private final SecretKey key;

    // Constructor initializes the secret key used for JWT signing
    public JWTUtils() {
        // A base64-encoded secret string for signing JWTs (this should ideally be stored securely)
        String secretString = JWTConstants.SECRET_KEY;

        // Decode the secret string to bytes and create a SecretKey for HmacSHA256
        byte[] keyBytes = Base64.getDecoder().decode(secretString.getBytes(StandardCharsets.UTF_8));
        this.key = new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    // Generates a new JWT token for a given user
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername()) // Set the username as the subject of the token
                .issuedAt(new Date(System.currentTimeMillis())) // Token creation time
                .expiration(new Date(System.currentTimeMillis() + JWTConstants.EXPIRE_TIME)) // Token expiration time
                .signWith(key) // Sign the token with the secret key
                .compact(); // Build and return the JWT token
    }

    // Extracts the username (subject) from the token
    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    // Extracts specific claims from the token using a function
    private <T> T extractClaims(String token, Function<Claims, T> claimsTFunction) {
        // Parse the token and apply the function to extract the desired claim
        return claimsTFunction.apply(Jwts.parser()
                .verifyWith(key) // Set the secret key for parsing
                .build()
                .parseSignedClaims(token) // Parse the token and get the claims
                .getPayload()); // Get the claims body
    }

    // Validates the token by checking if the username matches and the token isn't expired
    public boolean isValidToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token); // Extract username from token
        // Check if the token's username matches and if it's not expired
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    // Checks if the token is expired by comparing its expiration date with the current date
    private boolean isTokenExpired(String token) {
        return extractClaims(token, Claims::getExpiration).before(new Date());
    }
}
