INSERT INTO business_unit (active, creation_date, name, update_date) VALUES
(1, NOW(), 'Particle', NOW()),
(1, NOW(), 'Value', NOW()),
(1, NOW(), 'Ask', NOW()),
(1, NOW(), 'Kite', NOW());

INSERT INTO status_type (active, creation_date, name, update_date) VALUES
(1, NOW(), 'Available', NOW()),
(1, NOW(), 'Assigned', NOW()),
(1, NOW(), 'Under Maintenance', NOW()),
(1, NOW(), 'Dismissed', NOW());

INSERT INTO asset_type (active, creation_date, name, update_date) VALUES
(1, NOW(), 'Laptop', NOW()),
(1, NOW(), 'Monitor', NOW()),
(1, NOW(), 'Stampante', NOW()),
(1, NOW(), 'Smartphone', NOW());

INSERT INTO asset (brand, model, note, serial_number, type_id, business_unit_id, creation_date, update_date, status_id) VALUES
('Dell', 'Latitude 5420', 'Laptop aziendale', 'SN001234', 1, 1, NOW(), NOW(), 2),
('HP', 'EliteBook 840', NULL, 'SN001235', 1, 1, NOW(), NOW(), 1),
('LG', '27UK850', 'Monitor 4K', 'SN002001', 2, 2, NOW(), NOW(), 2),
('Samsung', 'Galaxy S23', NULL, 'IMEI123456', 4, 1, NOW(), NOW(), 1);

INSERT INTO users (name, surname, email, phone_number, user_type, business_unit_id) VALUES
('Marco', 'Rossi', 'marco.rossi@example.com', '3331112222', 'Utente', 1),
('Luca', 'Bianchi', 'luca.bianchi@example.com', '3332223333', 'Utente', 2),
('Sara', 'Verdi', 'sara.verdi@example.com', '3333334444', 'Amministrazione', 2),
('Giulia', 'Neri', 'giulia.neri@example.com', '3334445555', 'Amministrazione', 4);

INSERT INTO movement (date, movement_type, note, asset_id, users_id) VALUES
(TIMESTAMP '2024-01-10 09:00:00', 'Assegnazione', 'Assegnato notebook a Marco', 1, 1),
(TIMESTAMP '2024-01-15 14:30:00', 'Assegnazione', NULL, 2, 2),
(TIMESTAMP '2024-02-01 11:00:00', 'Riconsegna', 'Monitor inviato in manutenzione', 3, 3),
(TIMESTAMP '2024-02-05 16:00:00', 'Dismissione', 'Stampante rotta', 4, 4);
