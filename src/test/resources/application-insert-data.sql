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
(NOW(), 'MAINTENANCE', 'MA4', NULL);

-- Asset Type con code (prime 2 lettere maiuscole + posizione)
INSERT INTO asset_type (active, creation_date, name, code, storage, ram, update_date) VALUES
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
INSERT INTO users (name, surname, email, phone_number, user_type, business_unit_code, oid) VALUES
('Leon', 'Kennedy', 'leon.kennedy@example.it', '19982004', 'ADMIN', NULL, "1f3c9b82-7a41-4e3d-9c2a-91f4b0d7e8a1"),
('Marco', 'Rossi', 'marco.rossi@example.com', '3331112222', 'USER', 'PA1', "a72d4c10-3f55-4b8e-bc9f-0c1e2d44f7b3"),
('Luca', 'Bianchi', 'luca.bianchi@example.com', '3332223333', 'USER', 'VA2', "c9e1a4f7-2b88-4f0d-8d11-5a6c9e2f3b44"),
('Sara', 'Verdi', 'sara.verdi@example.com', '3333334444', 'ADMIN', NULL, "e54b7d22-91c3-4a0f-8f77-2d9a1c0e5f66"),
('Giulia', 'Neri', 'giulia.neri@example.com', '3334445555', 'USER', 'KI4', "f0a8c3d1-6e22-4b9a-9a33-7c4e1f2b8d90");

-- Movement usando asset_code (prime 2 lettere del serial_number + posizione)
INSERT INTO movement (date, movement_type, note, asset_code, user_code, receipt_file_name, code) VALUES
(TIMESTAMP '2024-01-10 09:00:00', 'ASSIGNED', 'Assegnato notebook a Marco',
'SN1', 'a72d4c10-3f55-4b8e-bc9f-0c1e2d44f7b3', 'SN1_Rossi_ASSIGNED_1.pdf', 'AS2SN1202401101'),

(TIMESTAMP '2024-01-15 14:30:00', 'ASSIGNED', NULL,
'SN2', 'c9e1a4f7-2b88-4f0d-8d11-5a6c9e2f3b44', 'SN2_Bianchi_ASSIGNED_2.pdf', 'AS3SN2202401152'),

(TIMESTAMP '2024-02-01 11:00:00', 'RETURNED', 'Monitor inviato in manutenzione',
'SN2', 'c9e1a4f7-2b88-4f0d-8d11-5a6c9e2f3b44', 'SN2_Bianchi_RETURNED_3.pdf', 'RE3SN2202402113'),

(TIMESTAMP '2024-02-05 16:00:00', 'DISMISSED', 'Smartphone dismesso',
'IM4', NULL, 'IM4_DISMISSED_4.pdf', 'DIIM4202402054');

INSERT INTO ticket (user_code, operation, type_code, asset_code, message, status, date, code) VALUES

-- Marco Rossi chiede assegnazione di un nuovo laptop
('a72d4c10-3f55-4b8e-bc9f-0c1e2d44f7b3', 'ASSIGNED', 'LA1', NULL,
 'Ho bisogno di un laptop per il nuovo progetto.', 'OPEN', NOW(), 'MAPA1'),

-- Luca Bianchi vuole restituire il suo laptop (SN2 che aveva assegnato)
('c9e1a4f7-2b88-4f0d-8d11-5a6c9e2f3b44', 'RETURNED', 'LA1', 'SN2',
 'Restituzione laptop HP EliteBook 840, non più necessario.', 'WORKING', '2024-02-01 10:00:00', 'LUVA2'),

-- Giulia Neri chiede assegnazione di uno smartphone
('f0a8c3d1-6e22-4b9a-9a33-7c4e1f2b8d90', 'ASSIGNED', 'SM4', NULL,
 'Richiesta smartphone aziendale per trasferte.', 'OPEN', '2024-03-01 09:00:00', 'GIKI3'),

-- Marco Rossi segnala dismissione del suo laptop (SN1)
('1f3c9b82-7a41-4e3d-9c2a-91f4b0d7e8a1', 'DISMISSED', 'LA1', 'SN1',
 'Laptop Dell Latitude 5420 non funzionante, richiedo dismissione.', 'CLOSED', '2024-03-10 11:00:00', 'LEAD4'),

-- Luca Bianchi richiede assegnazione di un monitor
('c9e1a4f7-2b88-4f0d-8d11-5a6c9e2f3b44', 'ASSIGNED', 'MO2', 'SN3',
 'Richiesta monitor per postazione fissa.', 'WORKING', '2024-03-15 14:00:00', 'GIKI5');

INSERT INTO ticket_reply (ticket_code, user_code, message, creation_date, code) VALUES

-- MAPA1: Marco chiede un laptop, admin risponde
('MAPA1', 'a72d4c10-3f55-4b8e-bc9f-0c1e2d44f7b3', 'Ho bisogno di un laptop per il nuovo progetto.', NOW(), 'MAPA11'),
('MAPA1', '1f3c9b82-7a41-4e3d-9c2a-91f4b0d7e8a1', 'Ricevuto, stiamo verificando la disponibilità.', NOW(), 'MAPA12'),

-- LUVA2: Luca vuole restituire il laptop
('LUVA2', 'c9e1a4f7-2b88-4f0d-8d11-5a6c9e2f3b44', 'Restituzione laptop HP EliteBook 840, non più necessario.', '2024-02-01 10:00:00', 'LUVA21'),
('LUVA2', '1f3c9b82-7a41-4e3d-9c2a-91f4b0d7e8a1', 'Preso in carico, procederemo con il ritiro.', '2024-02-01 10:30:00', 'LUVA22'),

-- GIKI3: Giulia chiede uno smartphone
('GIKI3', 'f0a8c3d1-6e22-4b9a-9a33-7c4e1f2b8d90', 'Richiesta smartphone aziendale per trasferte.', '2024-03-01 09:00:00', 'GIKI31'),
('GIKI3', 'e54b7d22-91c3-4a0f-8f77-2d9a1c0e5f66', 'Richiesta ricevuta, verifichiamo le disponibilità.', '2024-03-01 09:30:00', 'GIKI32'),

-- LEAD4: Dismissione laptop SN1, ticket CLOSED
('LEAD4', '1f3c9b82-7a41-4e3d-9c2a-91f4b0d7e8a1', 'Laptop Dell Latitude 5420 non funzionante, richiedo dismissione.', '2024-03-10 11:00:00', 'LEAD41'),
('LEAD4', 'e54b7d22-91c3-4a0f-8f77-2d9a1c0e5f66', 'Dismissione approvata, ticket chiuso.', '2024-03-10 11:30:00', 'LEAD42'),

-- GIKI5: Luca chiede un monitor
('GIKI5', 'c9e1a4f7-2b88-4f0d-8d11-5a6c9e2f3b44', 'Richiesta monitor per postazione fissa.', '2024-03-15 14:00:00', 'GIKI51'),
('GIKI5', '1f3c9b82-7a41-4e3d-9c2a-91f4b0d7e8a1', 'Monitor disponibile, procederemo con l assegnazione.', '2024-03-15 14:30:00', 'GIKI52');