package com.jwalit.inventory_system.repository;

import com.jwalit.inventory_system.entity.Role;
import com.jwalit.inventory_system.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=update")
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private UserRepository userRepository;

    @Test
    void saveUser_successfully() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("securePassword123");
        user.setPhoneNumber("+1234567890");
        user.setDateOfBirth(LocalDate.of(1990, 1, 1));
        user.setGender("Male");
        user.setAddress("123 Main St");
        user.setCity("Metropolis");
        user.setState("NY");
        user.setCountry("USA");
        user.setPostalCode("10001");
        user.setDrivingLicenseNumber("DL12345678");
        user.setRegistrationDate(LocalDate.now());
        user.setRole(Role.CUSTOMER);

        User savedUser = userRepository.save(user);

        Optional<User> retrievedUser = userRepository.findByEmail("john.doe@example.com");
        
        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get().getFirstName()).isEqualTo("John");
        assertThat(retrievedUser.get().getEmail()).isEqualTo("john.doe@example.com");
        assertThat(retrievedUser.get().getRole()).isEqualTo(Role.CUSTOMER);
    }

    @Test
    void findByEmail_returnsEmptyWhenMissing() {
        Optional<User> retrievedUser = userRepository.findByEmail("missing@example.com");
        assertThat(retrievedUser).isEmpty();
    }

    @Test
    void duplicateEmail_throwsDataIntegrityViolationException() {
        User user1 = new User();
        user1.setFirstName("Alice");
        user1.setLastName("Smith");
        user1.setEmail("alice.smith@example.com");
        user1.setPassword("pass1");
        user1.setRole(Role.CUSTOMER);
        userRepository.saveAndFlush(user1);

        User user2 = new User();
        user2.setFirstName("Bob");
        user2.setLastName("Smith");
        user2.setEmail("alice.smith@example.com"); // Duplicate email
        user2.setPassword("pass2");
        user2.setRole(Role.CUSTOMER);

        assertThatThrownBy(() -> userRepository.saveAndFlush(user2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
