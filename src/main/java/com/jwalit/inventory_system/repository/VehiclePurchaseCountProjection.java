package com.jwalit.inventory_system.repository;

import com.jwalit.inventory_system.entity.Vehicle;

public interface VehiclePurchaseCountProjection {
    Vehicle getVehicle();
    Long getPurchaseCount();
}
