USE football_manager;

-- =============================================
-- UTILISATEURS
-- admin@football.com  / admin123
-- user@football.com   / user123
-- =============================================
INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, role) VALUES
('Admin',    'Systeme',   'admin@football.com',  SHA2('admin123', 256), 'admin'),
('Kachaf',   'Meriem',    'meriem@gmail.com',    SHA2('user123',  256), 'user'),
('Benali',   'Karim',     'karim@gmail.com',     SHA2('user123',  256), 'user'),
('Martin',   'Sophie',    'sophie@gmail.com',    SHA2('user123',  256), 'user');

-- =============================================
-- STADES
-- =============================================
INSERT INTO stade (nom, ville, capacite) VALUES
('Parc des Princes',    'Paris',        48583),
('Santiago Bernabeu',   'Madrid',       81044),
('Stamford Bridge',     'Londres',      40834),
('Allianz Arena',       'Munich',       75024),
('Stade Velodrome',     'Marseille',    67394),
('Camp Nou',            'Barcelone',    99354),
('Old Trafford',        'Manchester',   74140),
('San Siro',            'Milan',        80018);

-- =============================================
-- MATCHS (references stade_id)
-- =============================================
INSERT INTO match_football (equipe_domicile, equipe_exterieure, stade_id, date_match) VALUES
('PSG',           'OM',           1, '2025-06-10'),
('Real Madrid',   'Barcelona',    2, '2025-06-20'),
('Chelsea',       'Arsenal',      3, '2025-07-01'),
('Bayern Munich', 'Dortmund',     4, '2025-07-05'),
('OM',            'Lyon',         5, '2025-07-12'),
('Barcelona',     'Atletico',     6, '2025-07-18'),
('Man United',    'Man City',     7, '2025-07-25'),
('AC Milan',      'Inter Milan',  8, '2025-08-02'),
('PSG',           'Lyon',         1, '2025-08-10'),
('Real Madrid',   'Sevilla',      2, '2025-08-15'),
('Chelsea',       'Liverpool',    3, '2025-08-22'),
('Bayern Munich', 'Leverkusen',   4, '2025-08-30');

-- =============================================
-- SUPPORTERS
-- =============================================
INSERT INTO supporter (nom, email, equipe_favorite) VALUES
('Ahmed Benali',    'ahmed@gmail.com',   'PSG'),
('Sofia Martin',    'sofia@gmail.com',   'Barcelona'),
('Lucas Dubois',    'lucas@gmail.com',   'Real Madrid'),
('Meriem Kachaf',   'meriem@gmail.com',  'PSG'),
('Karim Benali',    'karim@gmail.com',   'OM'),
('Sophie Martin',   'sophie@gmail.com',  'Chelsea'),
('Youssef Haddad',  'youssef@gmail.com', 'Bayern Munich'),
('Emma Laurent',    'emma@gmail.com',    'Barcelona'),
('Thomas Petit',    'thomas@gmail.com',  'Man United'),
('Lina Morel',      'lina@gmail.com',    'PSG'),
('Hugo Bernard',    'hugo@gmail.com',    'AC Milan'),
('Camille Roy',     'camille@gmail.com', 'Real Madrid'),
('Nadia Saoudi',    'nadia@gmail.com',   'OM'),
('Pierre Blanc',    'pierre@gmail.com',  'Bayern Munich'),
('Fatima Zahra',    'fatima@gmail.com',  'Chelsea');

-- =============================================
-- TICKETS (3 categories par match)
-- =============================================
INSERT INTO ticket (match_id, prix, categorie) VALUES
-- Match 1 : PSG vs OM
(1, 150.00, 'VIP'),
(1,  80.00, 'Tribune'),
(1,  45.00, 'Standard'),
-- Match 2 : Real vs Barca
(2, 200.00, 'VIP'),
(2,  95.00, 'Tribune'),
(2,  55.00, 'Standard'),
-- Match 3 : Chelsea vs Arsenal
(3, 120.00, 'VIP'),
(3,  70.00, 'Tribune'),
(3,  40.00, 'Standard'),
-- Match 4 : Bayern vs Dortmund
(4, 130.00, 'VIP'),
(4,  75.00, 'Tribune'),
(4,  40.00, 'Standard'),
-- Match 5 : OM vs Lyon
(5,  90.00, 'VIP'),
(5,  55.00, 'Tribune'),
(5,  30.00, 'Standard'),
-- Match 6 : Barca vs Atletico
(6, 160.00, 'VIP'),
(6,  85.00, 'Tribune'),
(6,  50.00, 'Standard'),
-- Match 7 : Man Utd vs Man City
(7, 140.00, 'VIP'),
(7,  80.00, 'Tribune'),
(7,  45.00, 'Standard'),
-- Match 8 : AC Milan vs Inter
(8, 110.00, 'VIP'),
(8,  65.00, 'Tribune'),
(8,  35.00, 'Standard');

-- =============================================
-- RESERVATIONS
-- =============================================
INSERT INTO reservation (supporter_id, ticket_id, date_reservation) VALUES
(1,  1, '2025-05-01'),
(2,  4, '2025-05-02'),
(3,  5, '2025-05-03'),
(4,  2, '2025-05-04'),
(5, 13, '2025-05-05'),
(6,  7, '2025-05-06'),
(7, 10, '2025-05-07'),
(8, 16, '2025-05-08'),
(9, 19, '2025-05-09'),
(10, 3, '2025-05-10'),
(11,22, '2025-05-11'),
(12, 6, '2025-05-12');

-- =============================================
-- PAIEMENTS (lies aux reservations)
-- =============================================
INSERT INTO paiement (reservation_id, montant, date_paiement, mode_paiement, statut) VALUES
(1,  150.00, '2025-05-01', 'Carte bancaire', 'paye'),
(2,  200.00, '2025-05-02', 'Carte bancaire', 'paye'),
(3,   95.00, '2025-05-03', 'Virement',       'paye'),
(4,   80.00, '2025-05-04', 'Carte bancaire', 'en_attente'),
(5,   30.00, '2025-05-05', 'Especes',        'paye'),
(6,  120.00, '2025-05-06', 'Carte bancaire', 'en_attente'),
(7,  130.00, '2025-05-07', 'Virement',       'paye'),
(8,  160.00, '2025-05-08', 'Carte bancaire', 'en_attente'),
(9,  140.00, '2025-05-09', 'Carte bancaire', 'annule'),
(10,  45.00, '2025-05-10', 'Especes',        'paye'),
(11, 110.00, '2025-05-11', 'Carte bancaire', 'en_attente'),
(12,  55.00, '2025-05-12', 'Virement',       'paye');
