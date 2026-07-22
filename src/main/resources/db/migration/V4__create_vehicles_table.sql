CREATE TABLE vehicles (
    id BIGSERIAL PRIMARY KEY,
    make VARCHAR(255) NOT NULL,
    model VARCHAR(255) NOT NULL,
    category VARCHAR(255) NOT NULL,
    price NUMERIC(15, 2) NOT NULL CHECK (price > 0),
    quantity INTEGER NOT NULL CHECK (quantity >= 0)
);
