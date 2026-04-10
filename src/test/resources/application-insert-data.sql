-- Business Unit con code (prime 2 lettere maiuscole + posizione)
INSERT INTO business_unit (active, creation_date, name, code, update_date) VALUES
(1, NOW(), 'Particle', 'PA1', NULL),
(1, NOW(), 'Value', 'VA2', NULL),
(1, NOW(), 'Ask', 'AS3', NULL),
(1, NOW(), 'Kite', 'KI4', NULL);

-- Asset Status Type con code (prime 2 lettere maiuscole + posizione)
-- INSERT INTO asset_status_type (active, creation_date, name, code, update_date) VALUES
-- (1, NOW(), 'Available', 'AV1', NULL),
-- (1, NOW(), 'Assigned', 'AS2', NULL),
-- (1, NOW(), 'Under Maintenance', 'UN3', NULL),
-- (1, NOW(), 'Dismissed', 'DI4', NULL);

INSERT INTO asset_status_type (creation_date, name, code, update_date) VALUES
(NOW(), 'AVAILABLE', 'AV1', NULL),
(NOW(), 'ASSIGNED', 'AS2', NULL),
(NOW(), 'DISMISSED', 'DI3', NULL),
(NOW(), 'Under Maintenance', 'UN4', NULL);

-- Asset Type con code (prime 2 lettere maiuscole + posizione)
INSERT INTO asset_type (active, creation_date, name, code, hard_disk, ram, update_date) VALUES
(1, NOW(), 'Laptop', 'LA1', 1, 1, NULL),
(1, NOW(), 'Monitor', 'MO2', 0, 0, NULL),
(1, NOW(), 'Stampante', 'ST3', 0, 0, NULL),
(1, NOW(), 'Smartphone', 'SM4', 1, 1, NULL);

-- Asset usando i code (prime 2 lettere del serial_number + posizione)
INSERT INTO asset (brand, model, note, serial_number, type_code, business_unit_code, creation_date, update_date, status_code, code) VALUES
('Dell', 'Latitude 5420', 'Laptop aziendale', 'SN001234', 'LA1', 'PA1', NOW(), NULL, 'AS2', 'SN1'),
('HP', 'EliteBook 840', NULL, 'SN001235', 'LA1', 'VA2', NOW(), NULL, 'AV1', 'SN2'),
('LG', '27UK850', 'Monitor 4K', 'SN002001', 'MO2', 'VA2', NOW(), NULL, 'AV1', 'SN3'),
('Samsung', 'Galaxy S23', NULL, 'IMEI123456', 'SM4', 'PA1', NOW(), NULL, 'DI3', 'IM4');

-- Users (rimane con business_unit_id)
INSERT INTO users (name, surname, email, phone_number, user_type, business_unit_code) VALUES
('Leon', 'Kennedy', 'leon.kennedy@example.it', '19982004', 'ADMIN', NULL),
('Marco', 'Rossi', 'marco.rossi@example.com', '3331112222', 'USER', 'PA1'),
('Luca', 'Bianchi', 'luca.bianchi@example.com', '3332223333', 'USER', 'VA2'),
('Sara', 'Verdi', 'sara.verdi@example.com', '3333334444', 'ADMIN', NULL),
('Giulia', 'Neri', 'giulia.neri@example.com', '3334445555', 'USER', 'KI4');

-- Movement usando asset_code (prime 2 lettere del serial_number + posizione)
INSERT INTO movement (date, movement_type, note, asset_code, users_id, receipt_file_name, code) VALUES
(TIMESTAMP '2024-01-10 09:00:00', 'ASSIGNED', 'Assegnato notebook a Marco', 'SN1', 2, 'SN1_Rossi_ASSIGNED_1.pdf', 'AS2SN1202401101'),
(TIMESTAMP '2024-01-15 14:30:00', 'ASSIGNED', NULL, 'SN2', 3, 'SN2_Bianchi_ASSIGNED_2.pdf', 'AS3SN2202401152'),
(TIMESTAMP '2024-02-01 11:00:00', 'RETURNED', 'Monitor inviato in manutenzione', 'SN2', 3, 'SN2_Bianchi_RETURNED_3.pdf', 'RE3SN2202402113'),
(TIMESTAMP '2024-02-05 16:00:00', 'DISMISSED', 'Smartphone dismesso', 'IM4', NULL, 'IM4_DISMISSED_4.pdf', 'DIIM4202402054');