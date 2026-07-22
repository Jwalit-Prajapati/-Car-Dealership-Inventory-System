package com.jwalit.inventory_system.service;

import com.jwalit.inventory_system.dto.LoginRequest;
import com.jwalit.inventory_system.dto.LoginResponse;
import com.jwalit.inventory_system.dto.RegistrationRequest;
import com.jwalit.inventory_system.entity.Role;
import com.jwalit.inventory_system.entity.User;
import com.jwalit.inventory_system.repository.UserRepository;
import com.jwalit.inventory_system.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void registerUser_success() {
        // Given
        RegistrationRequest request = new RegistrationRequest("John", "Doe", "john.doe@example.com", "password123");
        User mappedUser = new User();
        mappedUser.setFirstName("John");
        mappedUser.setLastName("Doe");
        mappedUser.setEmail("john.doe@example.com");

        given(userRepository.findByEmail(request.email())).willReturn(Optional.empty());
        given(userMapper.toEntity(request)).willReturn(mappedUser);
        given(passwordEncoder.encode(request.password())).willReturn("encoded_password");

        // When
        authService.register(request);

        // Then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getFirstName()).isEqualTo("John");
        assertThat(savedUser.getLastName()).isEqualTo("Doe");
        assertThat(savedUser.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(savedUser.getPassword()).isEqualTo("encoded_password");
        assertThat(savedUser.getRole()).isEqualTo(Role.USER);
    }

    @Test
    void registerUser_duplicateEmail() {
        // Given
        RegistrationRequest request = new RegistrationRequest("John", "Doe", "john.doe@example.com", "password123");
        User existingUser = new User();
        existingUser.setEmail(request.email());
        given(userRepository.findByEmail(request.email())).willReturn(Optional.of(existingUser));

        // When / Then
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already in use");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_validCredentials_returnsJwt() {
        LoginRequest request = new LoginRequest("user@example.com", "password123");
        User user = new User();
        user.setEmail("user@example.com");

        given(userRepository.findByEmail(request.email())).willReturn(Optional.of(user));
        given(jwtService.generateToken("user@example.com")).willReturn("jwt-token");

        LoginResponse response = authService.login(request);
        
        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo("jwt-token");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void login_wrongPassword_throwsException() {
        LoginRequest request = new LoginRequest("user@example.com", "wrong");
        
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .willThrow(new org.springframework.security.authentication.BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(request))
            .isInstanceOf(org.springframework.security.core.AuthenticationException.class);
    }

    @Test
    void login_userNotFound_throwsException() {
        LoginRequest request = new LoginRequest("unknown@example.com", "password123");
        
        given(userRepository.findByEmail(request.email())).willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
            .isInstanceOf(org.springframework.security.core.AuthenticationException.class);
    }
}
