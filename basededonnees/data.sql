USE football_manager;

-- Vider les tables dans le bon ordre (enfants avant parents)
DELETE FROM paiement;
DELETE FROM reservation;
DELETE FROM ticket;
DELETE FROM match_football;
DELETE FROM stade;
DELETE FROM utilisateur;

-- Remettre les compteurs d'ID à 1
ALTER TABLE paiement       AUTO_INCREMENT = 1;
ALTER TABLE reservation    AUTO_INCREMENT = 1;
ALTER TABLE ticket         AUTO_INCREMENT = 1;
ALTER TABLE match_football AUTO_INCREMENT = 1;
ALTER TABLE stade          AUTO_INCREMENT = 1;
ALTER TABLE utilisateur    AUTO_INCREMENT = 1;

-- =============================================
-- UTILISATEURS
-- admin@football.com  / admin123
-- user@football.com   / user123
-- =============================================
INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, role, equipe_favorite) VALUES
('Admin',    'Systeme',   'admin@football.com',  SHA2('admin123', 256), 'admin', ''),
('Kachaf',   'Meriem',    'meriem@gmail.com',    SHA2('user123',  256), 'user',  'PSG'),
('Benali',   'Karim',     'karim@gmail.com',     SHA2('user123',  256), 'user',  'OM'),
('Martin',   'Sophie',    'sophie@gmail.com',    SHA2('user123',  256), 'user',  'Chelsea');

-- =============================================
-- STADES
-- =============================================
INSERT INTO stade (nom, ville, capacite, localisation) VALUES
('Parc des Princes',    'Paris',        48583, '24 Rue du Commandant Guilbaud, 75016 Paris'),
('Santiago Bernabeu',   'Madrid',       81044, 'Av. de Concha Espina, 1, 28036 Madrid'),
('Stamford Bridge',     'Londres',      40834, 'Fulham Rd, London SW6 1HS'),
('Allianz Arena',       'Munich',       75024, 'Werner-Heisenberg-Allee 25, 80939 München'),
('Stade Velodrome',     'Marseille',    67394, '3 Boulevard Michelet, 13008 Marseille'),
('Camp Nou',            'Barcelone',    99354, 'C. d''Arístides Maillol, 08028 Barcelona'),
('Old Trafford',        'Manchester',   74140, 'Sir Matt Busby Way, Stretford, Manchester'),
('San Siro',            'Milan',        80018, 'Piazzale Angelo Moratti, 20151 Milano');

-- =============================================
-- MATCHS (references stade_id)
-- =============================================
INSERT INTO match_football (equipe_domicile, equipe_exterieure, stade_id, date_match) VALUES
('PSG',           'OM',           1, '2026-06-10'),
('Real Madrid',   'Barcelona',    2, '2026-06-20'),
('Chelsea',       'Arsenal',      3, '2026-07-01'),
('Bayern Munich', 'Dortmund',     4, '2026-07-05'),
('OM',            'Lyon',         5, '2026-07-12'),
('Barcelona',     'Atletico',     6, '2026-07-18'),
('Man United',    'Man City',     7, '2026-07-25'),
('AC Milan',      'Inter Milan',  8, '2026-08-02');

-- =============================================
-- TICKETS (3 categories par match)
-- =============================================
INSERT INTO ticket (match_id, prix, categorie, zone, quantite) VALUES
-- Match 1 : PSG vs OM
(1, 150.00, 'VIP',      'Carré Or',          50),
(1,  80.00, 'Tribune',  'Virage Nord',       500),
(1,  45.00, 'Standard', 'Virage Sud',        300),
-- Match 2 : Real vs Barca
(2, 200.00, 'VIP',      'Palco VIP',         100),
(2,  95.00, 'Tribune',  'Tribune Principal', 500),
(2,  55.00, 'Standard', 'Fondo Norte',       800),
-- Match 3 : Chelsea vs Arsenal
(3, 120.00, 'VIP',      'Executive Box',      30),
(3,  70.00, 'Tribune',  'East Stand',        400),
(3,  40.00, 'Standard', 'West Stand',        600),
-- Match 4 : Bayern vs Dortmund
(4, 130.00, 'VIP',      'Business Club',      80),
(4,  75.00, 'Tribune',  'Haupttribüne',      300),
(4,  40.00, 'Standard', 'Südkurve',          500),
-- Match 5 : OM vs Lyon
(5,  90.00, 'VIP',      'Loge Présidentielle', 30),
(5,  55.00, 'Tribune',  'Tribune Jean Bouin', 150),
(5,  30.00, 'Standard', 'Virage Massilia',   600),
-- Match 6 : Barca vs Atletico
(6, 160.00, 'VIP',      'Palco VIP',         100),
(6,  85.00, 'Tribune',  'Tribune Principal', 400),
(6,  50.00, 'Standard', 'Gol Nord',          800),
-- Match 7 : Man Utd vs Man City
(7, 140.00, 'VIP',      'Executive Suite',    40),
(7,  80.00, 'Tribune',  'North Stand',       500),
(7,  45.00, 'Standard', 'South Stand',       600),
-- Match 8 : AC Milan vs Inter
(8, 110.00, 'VIP',      'Sky Box',            50),
(8,  65.00, 'Tribune',  'Tribuna',           400),
(8,  35.00, 'Standard', 'Curva Sud',         800);

-- =============================================
-- RESERVATIONS (references utilisateur_id : 2=Meriem, 3=Karim, 4=Sophie)
-- =============================================
INSERT INTO reservation (utilisateur_id, ticket_id, quantite, date_reservation) VALUES
(2,  1, 2, '2026-05-01'),
(3,  5, 1, '2026-05-02'),
(4,  7, 1, '2026-05-03'),
(2,  2, 1, '2026-05-04'),
(3, 14, 3, '2026-05-05'),
(4, 10, 1, '2026-05-06'),
(2, 16, 1, '2026-05-07'),
(3, 19, 2, '2026-05-08'),
(4,  3, 1, '2026-05-09'),
(2, 22, 1, '2026-05-10'),
(3,  6, 1, '2026-05-11'),
(4, 11, 2, '2026-05-12');

-- =============================================
-- PAIEMENTS (lies aux reservations)
-- =============================================
INSERT INTO paiement (reservation_id, montant, date_paiement, mode_paiement, statut) VALUES
(1,  150.00, '2026-05-01', 'Carte bancaire', 'paye'),
(2,   95.00, '2026-05-02', 'Carte bancaire', 'paye'),
(3,  120.00, '2026-05-03', 'Virement',       'paye'),
(4,   80.00, '2026-05-04', 'Carte bancaire', 'en_attente'),
(5,   55.00, '2026-05-05', 'Especes',        'paye'),
(6,   75.00, '2026-05-06', 'Carte bancaire', 'en_attente'),
(7,  160.00, '2026-05-07', 'Virement',       'paye'),
(8,   80.00, '2026-05-08', 'Carte bancaire', 'en_attente'),
(9,   45.00, '2026-05-09', 'Especes',        'annule'),
(10, 110.00, '2026-05-10', 'Carte bancaire', 'paye'),
(11,  55.00, '2026-05-11', 'Virement',       'paye'),
(12,  75.00, '2026-05-12', 'Carte bancaire', 'en_attente');
