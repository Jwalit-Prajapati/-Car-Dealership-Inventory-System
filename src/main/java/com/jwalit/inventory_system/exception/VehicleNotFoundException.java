package com.jwalit.inventory_system.exception;

public class VehicleNotFoundException extends ResourceNotFoundException {
    public VehicleNotFoundException(String message) {
        super(message);
    }
}
