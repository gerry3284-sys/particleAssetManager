INSERT INTO business_unit (active, creation_date, name, update_date) VALUES
(1, NOW(), 'Particle', NULL),
(1, NOW(), 'Value', NULL),
(1, NOW(), 'Ask', NULL),
(1, NOW(), 'Kite', NULL);

INSERT INTO asset_status_type (active, creation_date, name, update_date) VALUES
(1, NOW(), 'Available', NULL),
(1, NOW(), 'Assigned', NULL),
(1, NOW(), 'Under Maintenance', NULL),
(1, NOW(), 'Dismissed', NULL);

INSERT INTO asset_type (active, creation_date, name, update_date) VALUES
(1, NOW(), 'Laptop', NULL),
(1, NOW(), 'Monitor', NULL),
(1, NOW(), 'Stampante', NULL),
(1, NOW(), 'Smartphone', NULL);

INSERT INTO asset (brand, model, note, serial_number, type_id, business_unit_id, creation_date, update_date, status_id) VALUES
('Dell', 'Latitude 5420', 'Laptop aziendale', 'SN001234', 1, 1, NOW(), NULL, 2),
('HP', 'EliteBook 840', NULL, 'SN001235', 1, 1, NOW(), NULL, 1),
('LG', '27UK850', 'Monitor 4K', 'SN002001', 2, 2, NOW(), NULL, 2),
('Samsung', 'Galaxy S23', NULL, 'IMEI123456', 4, 1, NOW(), NULL, 1);

INSERT INTO users (name, surname, email, phone_number, user_type, business_unit_id) VALUES
('Leon', 'Kennedy', 'leon.kennedy@example.it', '19982004', 'ADMIN', 1),
('Marco', 'Rossi', 'marco.rossi@example.com', '3331112222', 'USER', 1),
('Luca', 'Bianchi', 'luca.bianchi@example.com', '3332223333', 'USER', 2),
('Sara', 'Verdi', 'sara.verdi@example.com', '3333334444', 'ADMIN', 2),
('Giulia', 'Neri', 'giulia.neri@example.com', '3334445555', 'ADMIN', 4);

INSERT INTO movement (date, movement_type, note, asset_id, users_id) VALUES
(TIMESTAMP '2024-01-10 09:00:00', 'Assegnazione', 'Assegnato notebook a Marco', 1, 1),
(TIMESTAMP '2024-01-15 14:30:00', 'Assegnazione', NULL, 2, 2),
(TIMESTAMP '2024-02-01 11:00:00', 'Riconsegna', 'Monitor inviato in manutenzione', 3, 3),
(TIMESTAMP '2024-02-05 16:00:00', 'Dismissione', 'Stampante rotta', 4, 4);
