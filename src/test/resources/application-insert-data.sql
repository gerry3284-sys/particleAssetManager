-- Business Unit con code (prime 2 lettere maiuscole + posizione)
INSERT INTO business_unit (active, creation_date, name, code, update_date) VALUES
(1, NOW(), 'Particle', 'PA1', NULL),
(1, NOW(), 'Value', 'VA2', NULL),
(1, NOW(), 'Ask', 'AS3', NULL),
(1, NOW(), 'Kite', 'KI4', NULL);

-- Asset Status Type con code (prime 2 lettere maiuscole + posizione)
INSERT INTO asset_status_type (active, creation_date, name, code, update_date) VALUES
(1, NOW(), 'Available', 'AV1', NULL),
(1, NOW(), 'Assigned', 'AS2', NULL),
(1, NOW(), 'Under Maintenance', 'UN3', NULL),
(1, NOW(), 'Dismissed', 'DI4', NULL);

-- Asset Type con code (prime 2 lettere maiuscole + posizione)
INSERT INTO asset_type (active, creation_date, name, code, update_date) VALUES
(1, NOW(), 'Laptop', 'LA1', NULL),
(1, NOW(), 'Monitor', 'MO2', NULL),
(1, NOW(), 'Stampante', 'ST3', NULL),
(1, NOW(), 'Smartphone', 'SM4', NULL);

-- Asset usando i code (prime 2 lettere del serial_number + posizione)
INSERT INTO asset (brand, model, note, serial_number, type_code, business_unit_code, creation_date, update_date, status_code, code) VALUES
('Dell', 'Latitude 5420', 'Laptop aziendale', 'SN001234', 'LA1', 'PA1', NOW(), NULL, 'AS2', 'SN1'),
('HP', 'EliteBook 840', NULL, 'SN001235', 'LA1', 'PA1', NOW(), NULL, 'AV1', 'SN2'),
('LG', '27UK850', 'Monitor 4K', 'SN002001', 'MO2', 'VA2', NOW(), NULL, 'AS2', 'SN3'),
('Samsung', 'Galaxy S23', NULL, 'IMEI123456', 'SM4', 'PA1', NOW(), NULL, 'AV1', 'IM4');

-- Users (rimane con business_unit_id)
INSERT INTO users (name, surname, email, phone_number, user_type, business_unit_id) VALUES
('Leon', 'Kennedy', 'leon.kennedy@example.it', '19982004', 'ADMIN', 1),
('Marco', 'Rossi', 'marco.rossi@example.com', '3331112222', 'USER', 1),
('Luca', 'Bianchi', 'luca.bianchi@example.com', '3332223333', 'USER', 2),
('Sara', 'Verdi', 'sara.verdi@example.com', '3333334444', 'ADMIN', 2),
('Giulia', 'Neri', 'giulia.neri@example.com', '3334445555', 'ADMIN', 4);

-- Movement usando asset_code (prime 2 lettere del serial_number + posizione)
INSERT INTO movement (date, movement_type, note, asset_code, users_id) VALUES
(TIMESTAMP '2024-01-10 09:00:00', 'Assegnazione', 'Assegnato notebook a Marco', 'SN1', 1),
(TIMESTAMP '2024-01-15 14:30:00', 'Assegnazione', NULL, 'SN2', 2),
(TIMESTAMP '2024-02-01 11:00:00', 'Riconsegna', 'Monitor inviato in manutenzione', 'SN3', 3),
(TIMESTAMP '2024-02-05 16:00:00', 'Dismissione', 'Smartphone dismesso', 'IM4', 4);