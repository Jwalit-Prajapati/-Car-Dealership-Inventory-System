package com.jwalit.inventory_system.service;

import com.jwalit.inventory_system.dto.RegistrationRequest;
import com.jwalit.inventory_system.entity.Role;
import com.jwalit.inventory_system.entity.User;
import com.jwalit.inventory_system.repository.UserRepository;
import com.jwalit.inventory_system.mapper.UserMapper;
import com.jwalit.inventory_system.security.CustomUserDetails;
import com.jwalit.inventory_system.security.TokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.jwalit.inventory_system.dto.LoginRequest;
import com.jwalit.inventory_system.dto.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final TokenService jwtService;

    public LoginResponse login(LoginRequest request) {
        org.springframework.security.core.Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        User user = ((CustomUserDetails) auth.getPrincipal()).getUser();
        String jwtToken = jwtService.generateToken(Map.of("role", user.getRole().name()), auth.getName());
        return new LoginResponse(jwtToken, user.getRole().name(), user.getFullName());
    }

    @Transactional
    public void register(RegistrationRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);

        userRepository.save(user);
    }
}
