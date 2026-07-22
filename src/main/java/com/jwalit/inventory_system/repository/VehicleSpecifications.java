package com.jwalit.inventory_system.repository;

import com.jwalit.inventory_system.entity.Vehicle;
import org.springframework.data.jpa.domain.Specification;

public class VehicleSpecifications {

    public static Specification<Vehicle> hasMake(String make) {
        return (root, query, cb) -> {
            if (make == null || make.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("make")), "%" + make.trim().toLowerCase() + "%");
        };
    }

    public static Specification<Vehicle> hasCategory(String category) {
        return (root, query, cb) -> {
            if (category == null || category.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.equal(cb.lower(root.get("category")), category.trim().toLowerCase());
        };
    }

    public static Specification<Vehicle> hasModel(String model) {
        return (root, query, cb) -> {
            if (model == null || model.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("model")), "%" + model.trim().toLowerCase() + "%");
        };
    }

    public static Specification<Vehicle> hasMinimumPrice(java.math.BigDecimal minimumPrice) {
        return (root, query, cb) -> {
            if (minimumPrice == null) {
                return cb.conjunction();
            }
            return cb.greaterThanOrEqualTo(root.get("price"), minimumPrice);
        };
    }

    public static Specification<Vehicle> hasMaximumPrice(java.math.BigDecimal maximumPrice) {
        return (root, query, cb) -> {
            if (maximumPrice == null) {
                return cb.conjunction();
            }
            return cb.lessThanOrEqualTo(root.get("price"), maximumPrice);
        };
    }
}
