-- Cancellazione tabelle esistenti
DROP TABLE IF EXISTS movement;
DROP TABLE IF EXISTS asset;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS business_unit;
DROP TABLE IF EXISTS type;

-- Creazione tabella businessUnit
CREATE TABLE business_unit (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Creazione tabella type
CREATE TABLE type (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Creazione tabella asset
CREATE TABLE asset (
    id INT AUTO_INCREMENT PRIMARY KEY,
    brand VARCHAR(255) NOT NULL,
    model VARCHAR(255) NOT NULL,
    serial_number VARCHAR(255) NOT NULL,
    note TEXT,
    business_unit_id INT NOT NULL,
    type_id INT NOT NULL,
    FOREIGN KEY (business_unit_id) REFERENCES business_unit(id),
    FOREIGN KEY (type_id) REFERENCES type(id)
);

-- Creazione tabella users
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    surname VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(200) NOT NULL,
    user_type VARCHAR(100) NOT NULL,
    business_unit_id INT NOT NULL,
    FOREIGN KEY (business_unit_id) REFERENCES business_unit(id)
);

-- Creazione tabella movement
CREATE TABLE movement (
    id INT AUTO_INCREMENT PRIMARY KEY,
    date TIMESTAMP NOT NULL,
    movement_type VARCHAR(100) NOT NULL,
    note TEXT,
    asset_id INT NOT NULL,
    users_id INT,
    FOREIGN KEY (asset_id) REFERENCES asset(id),
    FOREIGN KEY (users_id) REFERENCES users(id)
);
