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
}
