package com.jwalit.inventory_system.security;

public interface TokenService {
    String generateToken(String username);
    String extractUsername(String token);
    boolean isTokenValid(String token);
}
