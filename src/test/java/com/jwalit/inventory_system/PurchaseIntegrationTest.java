package com.jwalit.inventory_system;

import com.jwalit.inventory_system.dto.PurchaseRequestDTO;
import com.jwalit.inventory_system.entity.Purchase;
import com.jwalit.inventory_system.entity.User;
import com.jwalit.inventory_system.entity.Vehicle;
import com.jwalit.inventory_system.repository.PurchaseRepository;
import com.jwalit.inventory_system.repository.UserRepository;
import com.jwalit.inventory_system.repository.VehicleRepository;
import com.jwalit.inventory_system.service.PurchaseService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=update")
@Testcontainers
class PurchaseIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    private Long vehicleId;
    private Long userId;
    
    // We create a mock UserDetails
    private UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
            .username("integration@test.com")
            .password("password")
            .roles("CUSTOMER")
            .build();

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setFirstName("Integration");
        user.setLastName("User");
        user.setEmail("integration@test.com");
        user.setPassword("password");
        user.setRole(com.jwalit.inventory_system.entity.Role.USER);
        user = userRepository.save(user);
        userId = user.getId();

        Vehicle vehicle = new Vehicle();
        vehicle.setMake("Ford");
        vehicle.setCategory("Truck");
        vehicle.setPrice(BigDecimal.valueOf(30000));
        vehicle.setQuantity(10);
        vehicle = vehicleRepository.save(vehicle);
        vehicleId = vehicle.getId();
    }

    @AfterEach
    void tearDown() {
        purchaseRepository.deleteAll();
        vehicleRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testPurchaseFlow_Success() {
        PurchaseRequestDTO requestDTO = new PurchaseRequestDTO(3);

        Purchase purchase = purchaseService.purchaseVehicle(vehicleId, requestDTO, userDetails);

        assertThat(purchase).isNotNull();
        assertThat(purchase.getId()).isNotNull();

        Optional<Vehicle> updatedVehicle = vehicleRepository.findById(vehicleId);
        assertThat(updatedVehicle).isPresent();
        assertThat(updatedVehicle.get().getQuantity()).isEqualTo(7);

        Iterable<Purchase> purchases = purchaseRepository.findAll();
        assertThat(purchases).hasSize(1);
    }

    @Test
    void testPurchaseFlow_Concurrency() throws InterruptedException {
        PurchaseRequestDTO requestDTO = new PurchaseRequestDTO(1);
        
        java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newFixedThreadPool(2);
        java.util.concurrent.CountDownLatch startLatch = new java.util.concurrent.CountDownLatch(1);
        java.util.concurrent.CountDownLatch doneLatch = new java.util.concurrent.CountDownLatch(2);
        java.util.concurrent.atomic.AtomicInteger errorCount = new java.util.concurrent.atomic.AtomicInteger(0);
        java.util.concurrent.atomic.AtomicInteger successCount = new java.util.concurrent.atomic.AtomicInteger(0);

        Runnable purchaseTask = () -> {
            try {
                startLatch.await();
                purchaseService.purchaseVehicle(vehicleId, requestDTO, userDetails);
                successCount.incrementAndGet();
            } catch (org.springframework.dao.OptimisticLockingFailureException e) {
                errorCount.incrementAndGet();
            } catch (Exception e) {
                // If it fails for another reason, we can log it
                e.printStackTrace();
            } finally {
                doneLatch.countDown();
            }
        };

        executor.submit(purchaseTask);
        executor.submit(purchaseTask);

        startLatch.countDown();
        doneLatch.await();
        executor.shutdown();

        assertThat(successCount.get()).isEqualTo(1);
        assertThat(errorCount.get()).isEqualTo(1);

        Optional<Vehicle> updatedVehicle = vehicleRepository.findById(vehicleId);
        assertThat(updatedVehicle).isPresent();
        assertThat(updatedVehicle.get().getQuantity()).isEqualTo(9);

        Iterable<Purchase> purchases = purchaseRepository.findAll();
        assertThat(purchases).hasSize(1);
    }
}
