-- Cancellazione tabelle esistenti
SET FOREIGN_KEY_CHECKS = 0; -- Disattiviamo i vincoli di FK
DROP TABLE IF EXISTS ticket;
DROP TABLE IF EXISTS movement;
DROP TABLE IF EXISTS asset;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS business_unit;
DROP TABLE IF EXISTS asset_type;
DROP TABLE IF EXISTS asset_status_type;
SET FOREIGN_KEY_CHECKS = 1; -- Riattiviamo i vincoli di FK

-- Tabella business_unit
CREATE TABLE business_unit (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    active BIT(1) NOT NULL,
    creation_date DATETIME(6) NOT NULL,
    name VARCHAR(255) NOT NULL,
    update_date DATETIME(6) DEFAULT NULL,
    code VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY (name),
    UNIQUE KEY (code)  -- Aggiunto vincolo UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabella asset_type
CREATE TABLE asset_type (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    active BIT(1) NOT NULL,
    creation_date DATETIME(6) NOT NULL,
    name VARCHAR(255) NOT NULL,
    update_date DATETIME(6) DEFAULT NULL,
    code VARCHAR(255) NOT NULL,
    storage BIT(1) NOT NULL,
    ram BIT(1) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY (name),
    UNIQUE KEY (code)  -- Aggiunto vincolo UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabella asset_status_type
CREATE TABLE asset_status_type (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    -- active BIT(1) NOT NULL,
    creation_date DATETIME(6) NOT NULL,
    name VARCHAR(255) NOT NULL,
    update_date DATETIME(6) DEFAULT NULL,
    code VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY (name),
    UNIQUE KEY (code)  -- Aggiunto vincolo UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabella users
CREATE TABLE users (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(255) NOT NULL,
    surname VARCHAR(255) NOT NULL,
    user_type ENUM('ADMIN', 'USER') NOT NULL,
    business_unit_code VARCHAR(255),
    oid VARCHAR(36) NOT NULL,
    PRIMARY KEY (id),
    KEY (business_unit_code),
    UNIQUE KEY(oid),
    CONSTRAINT fk_users_business_unit FOREIGN KEY (business_unit_code) REFERENCES business_unit(code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabella asset (modificata per usare code invece di id)
CREATE TABLE asset (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    brand VARCHAR(255) NOT NULL,
    creation_date DATETIME(6) NOT NULL,
    storage VARCHAR(255) DEFAULT NULL,
    model VARCHAR(255) NOT NULL,
    note VARCHAR(255) DEFAULT NULL,
    ram SMALLINT(6) DEFAULT NULL,
    serial_number VARCHAR(255) NOT NULL,
    update_date DATETIME(6) DEFAULT NULL,
    status_code VARCHAR(255) NOT NULL,           -- Cambiato da status_id
    type_code VARCHAR(255) NOT NULL,             -- Cambiato da type_id
    business_unit_code VARCHAR(255) NOT NULL,    -- Cambiato da business_unit_code
    end_maintenance_date DATE DEFAULT NULL,
    code VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY (serial_number),
    UNIQUE KEY (code), -- Aggiunto vincolo UNIQUE
    KEY (status_code),
    KEY (type_code),
    KEY (business_unit_code),
    CONSTRAINT fk_asset_status FOREIGN KEY (status_code) REFERENCES asset_status_type(code),
    CONSTRAINT fk_asset_type FOREIGN KEY (type_code) REFERENCES asset_type(code),
    CONSTRAINT fk_asset_business_unit FOREIGN KEY (business_unit_code) REFERENCES business_unit(code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabella movement
CREATE TABLE movement (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    date DATETIME(6) NOT NULL,
    movement_type ENUM('ASSIGNED', 'RETURNED', 'DISMISSED') NOT NULL,
    note VARCHAR(255) DEFAULT NULL,
    asset_code VARCHAR(255) NOT NULL,
    users_id BIGINT(20),
    receipt_file_name VARCHAR(255),
    code VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY (code),
    KEY (asset_code),
    KEY (users_id),
    CONSTRAINT fk_movement_asset FOREIGN KEY (asset_code) REFERENCES asset(code),
    CONSTRAINT fk_movement_users FOREIGN KEY (users_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabella ticket
CREATE TABLE ticket
(
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    user_code VARCHAR(255) NOT NULL,
    operation ENUM('ASSIGNED', 'RETURNED', 'DISMISSED') NOT NULL,
    type_code VARCHAR(255),
    asset_code VARCHAR(255),
    message VARCHAR(500) NOT NULL,
    status ENUM('OPEN', 'WORKING', 'CLOSED'),
    date DATETIME(6) NOT NULL,
    code VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY (code),
    KEY (user_code),
    KEY (type_code),
    KEY (asset_code),
    CONSTRAINT fk_ticket_users FOREIGN KEY (user_code) REFERENCES users(oid),
    CONSTRAINT fk_ticket_type FOREIGN KEY (type_code) REFERENCES asset_type(code),
    CONSTRAINT fk_ticket_asset FOREIGN KEY (asset_code) REFERENCES asset(code)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;