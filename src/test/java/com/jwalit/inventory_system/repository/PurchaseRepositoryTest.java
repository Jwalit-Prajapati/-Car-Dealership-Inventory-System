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

    private void createPurchase(Vehicle vehicle, BigDecimal price, int quantity) {
        Purchase purchase = new Purchase();
        purchase.setUser(savedUser);
        purchase.setVehicle(vehicle);
        purchase.setPurchasedPrice(price);
        purchase.setQuantityPurchased(quantity);
        purchase.setPurchaseDate(LocalDateTime.now());
        purchaseRepository.save(purchase);
    }

    @Test
    void getTotalRevenue_calculatesCorrectly_withMultiplePurchases() {
        createPurchase(savedVehicle, new BigDecimal("100.00"), 2); // 200
        createPurchase(savedVehicle, new BigDecimal("50.00"), 3);  // 150
        
        BigDecimal revenue = purchaseRepository.getTotalRevenue();
        assertThat(revenue).isEqualByComparingTo("350.00");
    }

    @Test
    void getTotalSalesCount_calculatesCorrectly() {
        createPurchase(savedVehicle, new BigDecimal("100.00"), 2);
        createPurchase(savedVehicle, new BigDecimal("50.00"), 3);
        
        Long count = purchaseRepository.getTotalSalesCount();
        assertThat(count).isEqualTo(2L);
    }

    @Test
    void findMostPurchasedVehicleData_findsMostPurchasedVehicle_handlesMultiplePurchasesAcrossVehicles() {
        Vehicle vehicle2 = new Vehicle();
        vehicle2.setMake("Honda");
        vehicle2.setModel("Civic");
        vehicle2.setCategory("Sedan");
        vehicle2.setPrice(new BigDecimal("22000.00"));
        vehicle2.setQuantity(5);
        vehicle2 = vehicleRepository.save(vehicle2);

        // savedVehicle bought 1 time
        createPurchase(savedVehicle, new BigDecimal("25000.00"), 1);

        // vehicle2 bought 3 times
        createPurchase(vehicle2, new BigDecimal("22000.00"), 1);
        createPurchase(vehicle2, new BigDecimal("22000.00"), 1);
        createPurchase(vehicle2, new BigDecimal("22000.00"), 2);

        java.util.List<VehiclePurchaseCountProjection> results = purchaseRepository.findMostPurchasedVehicleData(org.springframework.data.domain.PageRequest.of(0, 1));
        assertThat(results).isNotEmpty();
        VehiclePurchaseCountProjection firstResult = results.get(0);
        Vehicle topVehicle = firstResult.getVehicle();
        Long purchaseCount = firstResult.getPurchaseCount();
        
        assertThat(topVehicle.getId()).isEqualTo(vehicle2.getId());
        assertThat(purchaseCount).isEqualTo(3L);
    }

    @Test
    void getTotalRevenue_returnsZero_whenNoPurchasesExist() {
        BigDecimal revenue = purchaseRepository.getTotalRevenue();
        assertThat(revenue).isNull();
    }

    @Test
    void getTotalSalesCount_returnsZero_whenNoPurchasesExist() {
        Long count = purchaseRepository.getTotalSalesCount();
        assertThat(count).isEqualTo(0L);
    }

    @Test
    void findMostPurchasedVehicleData_returnsEmpty_whenNoPurchasesExist() {
        java.util.List<VehiclePurchaseCountProjection> results = purchaseRepository.findMostPurchasedVehicleData(org.springframework.data.domain.PageRequest.of(0, 1));
        assertThat(results).isEmpty();
    }
}
