INSERT INTO business_unit (name) VALUES
('A'),
('B'),
('C'),
('D');

INSERT INTO type (name) VALUES
('Laptop'),
('Smartphone'),
('Monitor'),
('Stampante');

INSERT INTO asset (brand, model, serial_number, note, business_unit_id, type_id) VALUES
('Dell', 'Latitude 5520', 'SN12345', 'Notebook aziendale', 1, 1),
('Apple', 'iPhone 13', 'SN67890', 'Telefono personale aziendale', 2, 2),
('Samsung', 'Odyssey G5', 'SN54321', 'Monitor ufficio', 2, 3),
('HP', 'LaserJet 400', 'SN98765', 'Stampante reparto', 4, 1);


INSERT INTO users (name, surname, email, phone_number, user_type, business_unit_id) VALUES
('Marco', 'Rossi', 'marco.rossi@example.com', '3331112222', 'Utente', 1),
('Luca', 'Bianchi', 'luca.bianchi@example.com', '3332223333', 'Utente', 2),
('Sara', 'Verdi', 'sara.verdi@example.com', '3333334444', 'Amministrazione', 2),
('Giulia', 'Neri', 'giulia.neri@example.com', '3334445555', 'Amministrazione', 4);

INSERT INTO movement (date, movement_type, note, asset_id, users_id) VALUES
(TIMESTAMP '2024-01-10 09:00:00', 'Assegnazione', 'Assegnato notebook a Marco', 1, 1),
(TIMESTAMP '2024-01-15 14:30:00', 'Assegnazione', NULL, 2, 2),
(TIMESTAMP '2024-02-01 11:00:00', 'Riconsegna', 'Monitor inviato in manutenzione', 3, NULL),
(TIMESTAMP '2024-02-05 16:00:00', 'Dismissione', 'Stampante rotta', 4, 4);
