CREATE TABLE purchases (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    vehicle_id BIGINT NOT NULL,
    purchased_price DECIMAL(10, 2) NOT NULL,
    quantity_purchased INT NOT NULL,
    purchase_date TIMESTAMP NOT NULL,
    
    CONSTRAINT fk_purchase_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_purchase_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicles (id)
);
