package com.jwalit.inventory_system.security;

import java.util.Map;

public interface TokenService {
    String generateToken(String username);
    String generateToken(Map<String, Object> extraClaims, String username);
    String extractUsername(String token);
    boolean isTokenValid(String token);
}
