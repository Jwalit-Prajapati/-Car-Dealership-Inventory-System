package com.jwalit.inventory_system.validation;

import com.jwalit.inventory_system.dto.VehicleSearchRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PriceRangeValidator implements ConstraintValidator<ValidPriceRange, VehicleSearchRequest> {

    @Override
    public boolean isValid(VehicleSearchRequest request, ConstraintValidatorContext context) {
        if (request.getMinPrice() != null && request.getMaxPrice() != null) {
            return request.getMinPrice().compareTo(request.getMaxPrice()) <= 0;
        }
        return true;
    }
}
