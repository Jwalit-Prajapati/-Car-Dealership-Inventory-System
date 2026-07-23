package com.jwalit.inventory_system.validation;

import com.jwalit.inventory_system.dto.VehicleSearchRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PriceRangeValidator implements ConstraintValidator<ValidPriceRange, VehicleSearchRequest> {

    @Override
    public boolean isValid(VehicleSearchRequest request, ConstraintValidatorContext context) {
        if (request.minPrice() != null && request.maxPrice() != null) {
            return request.minPrice().compareTo(request.maxPrice()) <= 0;
        }
        return true;
    }
}
