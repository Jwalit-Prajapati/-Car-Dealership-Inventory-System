CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    phone_number VARCHAR(255),
    date_of_birth DATE,
    gender VARCHAR(50),
    address VARCHAR(255),
    city VARCHAR(255),
    state VARCHAR(255),
    country VARCHAR(255),
    postal_code VARCHAR(50),
    driving_license_number VARCHAR(255),
    registration_date DATE,
    role VARCHAR(50)
);
