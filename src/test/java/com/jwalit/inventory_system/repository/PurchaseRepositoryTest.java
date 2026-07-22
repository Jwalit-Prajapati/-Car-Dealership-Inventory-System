package com.jwalit.inventory_system.repository;

import com.jwalit.inventory_system.entity.Purchase;
import com.jwalit.inventory_system.entity.Role;
import com.jwalit.inventory_system.entity.User;
import com.jwalit.inventory_system.entity.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=update")
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PurchaseRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    private User savedUser;
    private Vehicle savedVehicle;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setFirstName("Buyer");
        user.setLastName("One");
        user.setEmail("buyer@example.com");
        user.setPassword("password");
        user.setRole(Role.CUSTOMER);
        user.setCreatedAt(LocalDateTime.now());
        savedUser = userRepository.save(user);

        Vehicle vehicle = new Vehicle();
        vehicle.setMake("Toyota");
        vehicle.setModel("Camry");
        vehicle.setCategory("Sedan");
        vehicle.setPrice(new BigDecimal("25000.00"));
        vehicle.setQuantity(10);
        savedVehicle = vehicleRepository.save(vehicle);
    }

    @Test
    void savePurchase_andRetrieve_verifiesAllFieldsAndRelationships() {
        Purchase purchase = new Purchase();
        purchase.setUser(savedUser);
        purchase.setVehicle(savedVehicle);
        purchase.setPurchasedPrice(new BigDecimal("24500.00"));
        purchase.setQuantityPurchased(1);
        LocalDateTime now = LocalDateTime.now();
        purchase.setPurchaseDate(now);

        Purchase savedPurchase = purchaseRepository.save(purchase);

        Optional<Purchase> retrievedOptional = purchaseRepository.findById(savedPurchase.getId());
        assertThat(retrievedOptional).isPresent();

        Purchase retrievedPurchase = retrievedOptional.get();

        // Verify basic fields
        assertThat(retrievedPurchase.getPurchasedPrice()).isEqualByComparingTo("24500.00");
        assertThat(retrievedPurchase.getQuantityPurchased()).isEqualTo(1);
        assertThat(retrievedPurchase.getPurchaseDate()).isNotNull();

        // Verify relationships
        assertThat(retrievedPurchase.getUser()).isNotNull();
        assertThat(retrievedPurchase.getUser().getId()).isEqualTo(savedUser.getId());
        assertThat(retrievedPurchase.getUser().getEmail()).isEqualTo("buyer@example.com");

        assertThat(retrievedPurchase.getVehicle()).isNotNull();
        assertThat(retrievedPurchase.getVehicle().getId()).isEqualTo(savedVehicle.getId());
        assertThat(retrievedPurchase.getVehicle().getMake()).isEqualTo("Toyota");
    }
}
