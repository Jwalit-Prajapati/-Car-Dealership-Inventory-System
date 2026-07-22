package com.jwalit.inventory_system.repository;

import com.jwalit.inventory_system.entity.Vehicle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import jakarta.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=update")
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class VehicleRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private VehicleRepository vehicleRepository;

    @Test
    void saveVehicle_successfully() {
        Vehicle vehicle = new Vehicle();
        vehicle.setMake("Toyota");
        vehicle.setModel("Camry");
        vehicle.setCategory("Sedan");
        vehicle.setPrice(new BigDecimal("25000.00"));
        vehicle.setQuantity(10);

        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        assertThat(savedVehicle.getId()).isNotNull();

        Optional<Vehicle> retrievedVehicle = vehicleRepository.findById(savedVehicle.getId());
        
        assertThat(retrievedVehicle).isPresent();
        assertThat(retrievedVehicle.get().getMake()).isEqualTo("Toyota");
        assertThat(retrievedVehicle.get().getModel()).isEqualTo("Camry");
        assertThat(retrievedVehicle.get().getPrice()).isEqualByComparingTo(new BigDecimal("25000.00"));
        assertThat(retrievedVehicle.get().getQuantity()).isEqualTo(10);
    }

    @Test
    void findById_returnsEmptyWhenMissing() {
        Optional<Vehicle> retrievedVehicle = vehicleRepository.findById(999L);
        assertThat(retrievedVehicle).isEmpty();
    }

    @Test
    void saveVehicle_failsWhenPriceIsNegative() {
        Vehicle vehicle = new Vehicle();
        vehicle.setMake("Honda");
        vehicle.setModel("Civic");
        vehicle.setCategory("Sedan");
        vehicle.setPrice(new BigDecimal("-5000.00")); // Negative price
        vehicle.setQuantity(5);

        assertThatThrownBy(() -> vehicleRepository.saveAndFlush(vehicle))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void saveVehicle_failsWhenQuantityIsNegative() {
        Vehicle vehicle = new Vehicle();
        vehicle.setMake("Ford");
        vehicle.setModel("Mustang");
        vehicle.setCategory("Coupe");
        vehicle.setPrice(new BigDecimal("35000.00"));
        vehicle.setQuantity(-2); // Negative quantity

        assertThatThrownBy(() -> vehicleRepository.saveAndFlush(vehicle))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void findByMakeContainingIgnoreCase_findsVehicles() {
        Vehicle v1 = new Vehicle(); v1.setMake("Toyota"); v1.setModel("Camry"); v1.setCategory("Sedan"); v1.setPrice(new BigDecimal("1000")); v1.setQuantity(1);
        Vehicle v2 = new Vehicle(); v2.setMake("Ford"); v2.setModel("Focus"); v2.setCategory("Hatchback"); v2.setPrice(new BigDecimal("1000")); v2.setQuantity(1);
        vehicleRepository.save(v1);
        vehicleRepository.save(v2);

        Page<Vehicle> result = vehicleRepository.findByMakeContainingIgnoreCase("toy", PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getMake()).isEqualTo("Toyota");
    }

    @Test
    void findByCategoryIgnoreCase_findsVehicles() {
        Vehicle v1 = new Vehicle(); v1.setMake("Toyota"); v1.setModel("Camry"); v1.setCategory("Sedan"); v1.setPrice(new BigDecimal("1000")); v1.setQuantity(1);
        vehicleRepository.save(v1);

        Page<Vehicle> result = vehicleRepository.findByCategoryIgnoreCase("sedan", PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCategory()).isEqualTo("Sedan");
    }

    @Test
    void findByMakeAndCategory_findsVehicles() {
        Vehicle v1 = new Vehicle(); v1.setMake("Toyota"); v1.setModel("Camry"); v1.setCategory("Sedan"); v1.setPrice(new BigDecimal("1000")); v1.setQuantity(1);
        vehicleRepository.save(v1);

        Page<Vehicle> result = vehicleRepository.findByMakeContainingIgnoreCaseAndCategoryIgnoreCase("toy", "sedan", PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
    }
}
