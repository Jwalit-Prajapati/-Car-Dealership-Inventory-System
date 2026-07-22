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

        Page<Vehicle> result = vehicleRepository.findAll(VehicleSpecifications.hasMake("toy"), PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getMake()).isEqualTo("Toyota");
    }

    @Test
    void findByCategoryIgnoreCase_findsVehicles() {
        Vehicle v1 = new Vehicle(); v1.setMake("Toyota"); v1.setModel("Camry"); v1.setCategory("Sedan"); v1.setPrice(new BigDecimal("1000")); v1.setQuantity(1);
        vehicleRepository.save(v1);

        Page<Vehicle> result = vehicleRepository.findAll(VehicleSpecifications.hasCategory("sedan"), PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCategory()).isEqualTo("Sedan");
    }

    @Test
    void findByMakeAndCategory_findsVehicles() {
        Vehicle v1 = new Vehicle(); v1.setMake("Toyota"); v1.setModel("Camry"); v1.setCategory("Sedan"); v1.setPrice(new BigDecimal("1000")); v1.setQuantity(1);
        vehicleRepository.save(v1);

        Page<Vehicle> result = vehicleRepository.findAll(
                org.springframework.data.jpa.domain.Specification.where(VehicleSpecifications.hasMake("toy"))
                        .and(VehicleSpecifications.hasCategory("sedan")),
                PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void findByModel_findsVehicles() {
        Vehicle v1 = new Vehicle(); v1.setMake("Toyota"); v1.setModel("Camry"); v1.setCategory("Sedan"); v1.setPrice(new BigDecimal("1000")); v1.setQuantity(1);
        vehicleRepository.save(v1);

        Page<Vehicle> result = vehicleRepository.findAll(VehicleSpecifications.hasModel("cam"), PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getModel()).isEqualTo("Camry");
    }

    @Test
    void findByMinimumPrice_findsVehicles() {
        Vehicle v1 = new Vehicle(); v1.setMake("Toyota"); v1.setModel("Camry"); v1.setCategory("Sedan"); v1.setPrice(new BigDecimal("20000")); v1.setQuantity(1);
        Vehicle v2 = new Vehicle(); v2.setMake("Ford"); v2.setModel("Focus"); v2.setCategory("Hatchback"); v2.setPrice(new BigDecimal("10000")); v2.setQuantity(1);
        vehicleRepository.save(v1);
        vehicleRepository.save(v2);

        Page<Vehicle> result = vehicleRepository.findAll(VehicleSpecifications.hasMinimumPrice(new BigDecimal("15000")), PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getMake()).isEqualTo("Toyota");
    }

    @Test
    void findByMaximumPrice_findsVehicles() {
        Vehicle v1 = new Vehicle(); v1.setMake("Toyota"); v1.setModel("Camry"); v1.setCategory("Sedan"); v1.setPrice(new BigDecimal("20000")); v1.setQuantity(1);
        Vehicle v2 = new Vehicle(); v2.setMake("Ford"); v2.setModel("Focus"); v2.setCategory("Hatchback"); v2.setPrice(new BigDecimal("10000")); v2.setQuantity(1);
        vehicleRepository.save(v1);
        vehicleRepository.save(v2);

        Page<Vehicle> result = vehicleRepository.findAll(VehicleSpecifications.hasMaximumPrice(new BigDecimal("15000")), PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getMake()).isEqualTo("Ford");
    }

    @Test
    void findByPriceRange_findsVehicles() {
        Vehicle v1 = new Vehicle(); v1.setMake("Toyota"); v1.setModel("Camry"); v1.setCategory("Sedan"); v1.setPrice(new BigDecimal("20000")); v1.setQuantity(1);
        Vehicle v2 = new Vehicle(); v2.setMake("Ford"); v2.setModel("Focus"); v2.setCategory("Hatchback"); v2.setPrice(new BigDecimal("10000")); v2.setQuantity(1);
        Vehicle v3 = new Vehicle(); v3.setMake("Honda"); v3.setModel("Civic"); v3.setCategory("Sedan"); v3.setPrice(new BigDecimal("30000")); v3.setQuantity(1);
        vehicleRepository.save(v1);
        vehicleRepository.save(v2);
        vehicleRepository.save(v3);

        Page<Vehicle> result = vehicleRepository.findAll(
                org.springframework.data.jpa.domain.Specification.where(VehicleSpecifications.hasMinimumPrice(new BigDecimal("15000")))
                        .and(VehicleSpecifications.hasMaximumPrice(new BigDecimal("25000"))),
                PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getMake()).isEqualTo("Toyota");
    }

    @Test
    void findByMakeAndModel_findsVehicles() {
        Vehicle v1 = new Vehicle(); v1.setMake("Toyota"); v1.setModel("Camry"); v1.setCategory("Sedan"); v1.setPrice(new BigDecimal("20000")); v1.setQuantity(1);
        Vehicle v2 = new Vehicle(); v2.setMake("Toyota"); v2.setModel("Corolla"); v2.setCategory("Sedan"); v2.setPrice(new BigDecimal("15000")); v2.setQuantity(1);
        vehicleRepository.save(v1);
        vehicleRepository.save(v2);

        Page<Vehicle> result = vehicleRepository.findAll(
                org.springframework.data.jpa.domain.Specification.where(VehicleSpecifications.hasMake("toyota"))
                        .and(VehicleSpecifications.hasModel("camry")),
                PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getModel()).isEqualTo("Camry");
    }

    @Test
    void findByAllFilters_findsVehicles() {
        Vehicle v1 = new Vehicle(); v1.setMake("Toyota"); v1.setModel("Camry"); v1.setCategory("Sedan"); v1.setPrice(new BigDecimal("20000")); v1.setQuantity(1);
        vehicleRepository.save(v1);

        Page<Vehicle> result = vehicleRepository.findAll(
                org.springframework.data.jpa.domain.Specification.where(VehicleSpecifications.hasMake("toyota"))
                        .and(VehicleSpecifications.hasModel("camry"))
                        .and(VehicleSpecifications.hasCategory("sedan"))
                        .and(VehicleSpecifications.hasMinimumPrice(new BigDecimal("10000")))
                        .and(VehicleSpecifications.hasMaximumPrice(new BigDecimal("30000"))),
                PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void search_returningNoResults() {
        Vehicle v1 = new Vehicle(); v1.setMake("Toyota"); v1.setModel("Camry"); v1.setCategory("Sedan"); v1.setPrice(new BigDecimal("20000")); v1.setQuantity(1);
        vehicleRepository.save(v1);

        Page<Vehicle> result = vehicleRepository.findAll(VehicleSpecifications.hasModel("focus"), PageRequest.of(0, 10));
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void search_paginationWorksWithFilters() {
        for(int i=0; i<15; i++) {
            Vehicle v = new Vehicle(); v.setMake("Toyota"); v.setModel("Camry"); v.setCategory("Sedan"); v.setPrice(new BigDecimal("20000")); v.setQuantity(1);
            vehicleRepository.save(v);
        }

        Page<Vehicle> result = vehicleRepository.findAll(VehicleSpecifications.hasMake("toyota"), PageRequest.of(1, 10));
        assertThat(result.getContent()).hasSize(5);
        assertThat(result.getTotalElements()).isEqualTo(15);
    }

    // ── Inventory stats JPQL projection tests ─────────────────────────────────

    @Test
    void getInventoryStatistics_mixedInventory_returnsCorrectCounts() {
        Vehicle v1 = new Vehicle(); v1.setMake("Toyota"); v1.setModel("Camry"); v1.setCategory("Sedan"); v1.setPrice(new BigDecimal("25000")); v1.setQuantity(10);
        Vehicle v2 = new Vehicle(); v2.setMake("Honda"); v2.setModel("Civic"); v2.setCategory("Sedan"); v2.setPrice(new BigDecimal("20000")); v2.setQuantity(3);
        Vehicle v3 = new Vehicle(); v3.setMake("Ford"); v3.setModel("Focus"); v3.setCategory("Hatchback"); v3.setPrice(new BigDecimal("18000")); v3.setQuantity(0);
        vehicleRepository.save(v1);
        vehicleRepository.save(v2);
        vehicleRepository.save(v3);

        com.jwalit.inventory_system.dto.InventoryStatsDto stats = vehicleRepository.getInventoryStatistics();

        assertThat(stats.getTotalVehicles()).isEqualTo(3L);
        assertThat(stats.getLowStockVehicles()).isEqualTo(2L);  // quantity 3 and 0 are < 5
        assertThat(stats.getOutOfStockVehicles()).isEqualTo(1L); // only quantity 0
    }

    @Test
    void getInventoryStatistics_emptyRepository_returnsAllZeros() {
        com.jwalit.inventory_system.dto.InventoryStatsDto stats = vehicleRepository.getInventoryStatistics();

        assertThat(stats.getTotalVehicles()).isEqualTo(0L);
        assertThat(stats.getLowStockVehicles()).isEqualTo(0L);
        assertThat(stats.getOutOfStockVehicles()).isEqualTo(0L);
    }

    @Test
    void getInventoryStatistics_allInStock_returnsZeroLowAndOutOfStock() {
        Vehicle v1 = new Vehicle(); v1.setMake("Toyota"); v1.setModel("Camry"); v1.setCategory("Sedan"); v1.setPrice(new BigDecimal("25000")); v1.setQuantity(10);
        Vehicle v2 = new Vehicle(); v2.setMake("Honda"); v2.setModel("Civic"); v2.setCategory("Sedan"); v2.setPrice(new BigDecimal("20000")); v2.setQuantity(20);
        vehicleRepository.save(v1);
        vehicleRepository.save(v2);

        com.jwalit.inventory_system.dto.InventoryStatsDto stats = vehicleRepository.getInventoryStatistics();

        assertThat(stats.getTotalVehicles()).isEqualTo(2L);
        assertThat(stats.getLowStockVehicles()).isEqualTo(0L);
        assertThat(stats.getOutOfStockVehicles()).isEqualTo(0L);
    }

    @Test
    void getInventoryStatistics_allOutOfStock_returnsAllAsLowAndOutOfStock() {
        Vehicle v1 = new Vehicle(); v1.setMake("Toyota"); v1.setModel("Camry"); v1.setCategory("Sedan"); v1.setPrice(new BigDecimal("25000")); v1.setQuantity(0);
        Vehicle v2 = new Vehicle(); v2.setMake("Honda"); v2.setModel("Civic"); v2.setCategory("Sedan"); v2.setPrice(new BigDecimal("20000")); v2.setQuantity(0);
        vehicleRepository.save(v1);
        vehicleRepository.save(v2);

        com.jwalit.inventory_system.dto.InventoryStatsDto stats = vehicleRepository.getInventoryStatistics();

        assertThat(stats.getTotalVehicles()).isEqualTo(2L);
        assertThat(stats.getLowStockVehicles()).isEqualTo(2L);
        assertThat(stats.getOutOfStockVehicles()).isEqualTo(2L);
    }
}
