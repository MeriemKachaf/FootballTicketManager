CREATE DATABASE IF NOT EXISTS football_manager;
USE football_manager;

-- Suppression dans le bon ordre (respect des cles etrangeres)
DROP TABLE IF EXISTS paiement;
DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS ticket;
DROP TABLE IF EXISTS supporter;
DROP TABLE IF EXISTS match_football;
DROP TABLE IF EXISTS stade;
DROP TABLE IF EXISTS utilisateur;

-- =============================================
-- TABLE : utilisateur
-- =============================================
CREATE TABLE utilisateur (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    nom             VARCHAR(100) NOT NULL,
    prenom          VARCHAR(100) NOT NULL DEFAULT '',
    email           VARCHAR(100) NOT NULL UNIQUE,
    mot_de_passe    VARCHAR(255) NOT NULL,
    role            ENUM('admin', 'user') NOT NULL DEFAULT 'user',
    equipe_favorite VARCHAR(100) DEFAULT ''
);

-- =============================================
-- TABLE : stade
-- =============================================
CREATE TABLE stade (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    nom          VARCHAR(100) NOT NULL,
    ville        VARCHAR(100) NOT NULL,
    capacite     INT NOT NULL,
    localisation VARCHAR(200) DEFAULT ''
);

-- =============================================
-- TABLE : match_football
-- =============================================
CREATE TABLE match_football (
    id                INT AUTO_INCREMENT PRIMARY KEY,
    equipe_domicile   VARCHAR(100) NOT NULL,
    equipe_exterieure VARCHAR(100) NOT NULL,
    stade_id          INT NOT NULL,
    date_match        DATE NOT NULL,
    CONSTRAINT fk_match_stade
        FOREIGN KEY (stade_id) REFERENCES stade(id)
        ON DELETE RESTRICT ON UPDATE CASCADE
);

-- =============================================
-- TABLE : ticket
-- =============================================
CREATE TABLE ticket (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    match_id  INT NOT NULL,
    prix      DOUBLE NOT NULL,
    categorie VARCHAR(50)  NOT NULL,
    zone      VARCHAR(100) DEFAULT '',
    quantite  INT NOT NULL DEFAULT 100,
    CONSTRAINT fk_ticket_match
        FOREIGN KEY (match_id) REFERENCES match_football(id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- =============================================
-- TABLE : reservation
-- =============================================
CREATE TABLE reservation (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    utilisateur_id   INT NOT NULL,
    ticket_id        INT NOT NULL,
    quantite         INT NOT NULL DEFAULT 1,
    date_reservation DATE NOT NULL,
    CONSTRAINT chk_quantite CHECK (quantite BETWEEN 1 AND 3),
    CONSTRAINT fk_res_utilisateur
        FOREIGN KEY (utilisateur_id) REFERENCES utilisateur(id) ON DELETE CASCADE,
    CONSTRAINT fk_res_ticket
        FOREIGN KEY (ticket_id) REFERENCES ticket(id) ON DELETE CASCADE
);

-- =============================================
-- TABLE : paiement
-- =============================================
CREATE TABLE paiement (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    reservation_id INT NOT NULL,
    montant        DOUBLE NOT NULL,
    date_paiement  DATE NOT NULL,
    mode_paiement  ENUM('Carte bancaire','Especes','Virement') NOT NULL DEFAULT 'Carte bancaire',
    statut         ENUM('en_attente','paye','annule') NOT NULL DEFAULT 'en_attente',
    CONSTRAINT fk_paie_resa
        FOREIGN KEY (reservation_id) REFERENCES reservation(id) ON DELETE CASCADE
);

-- =============================================
-- DONNEES INITIALES
-- =============================================

-- Comptes (mot de passe hashé SHA-256)
-- admin@football.com / admin123
-- user@football.com  / user123
INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, role, equipe_favorite) VALUES
('Admin',   'Système', 'admin@football.com', SHA2('admin123', 256), 'admin', ''),
('Martin',  'Sophie',  'user@football.com',  SHA2('user123',  256), 'user',  'PSG');

-- Stades
INSERT INTO stade (nom, ville, capacite, localisation) VALUES
('Parc des Princes',   'Paris',       48583, '24 Rue du Commandant Guilbaud, 75016 Paris'),
('Orange Vélodrome',   'Marseille',   67394, '3 Boulevard Michelet, 13008 Marseille'),
('Stade de France',    'Saint-Denis', 81338, 'ZAC du Cornillon Nord, 93216 Saint-Denis'),
('Allianz Arena',      'Munich',      75024, 'Werner-Heisenberg-Allee 25, 80939 München'),
('Santiago Bernabéu',  'Madrid',      81044, 'Av. de Concha Espina, 1, 28036 Madrid');

-- Matchs
INSERT INTO match_football (equipe_domicile, equipe_exterieure, stade_id, date_match) VALUES
('PSG',          'OM',        1, '2026-09-15'),
('PSG',          'Lyon',      1, '2026-10-05'),
('OM',           'Lyon',      2, '2026-09-28'),
('Real Madrid',  'Barcelona', 5, '2026-10-19'),
('Bayern Munich','Dortmund',  4, '2026-11-02'),
('PSG',          'Real Madrid',3,'2026-11-25');

-- Tickets : 3 catégories par match
INSERT INTO ticket (match_id, prix, categorie, zone, quantite) VALUES
-- Match 1 : PSG vs OM
(1,  25.00, 'Tribune',  'Virage Nord',          500),
(1,  75.00, 'Loge',     'Tribune Officielle',   200),
(1, 150.00, 'VIP',      'Carré Or',              50),
-- Match 2 : PSG vs Lyon
(2,  20.00, 'Tribune',  'Virage Nord',          500),
(2,  60.00, 'Loge',     'Tribune Officielle',   200),
(2, 120.00, 'VIP',      'Carré Or',              50),
-- Match 3 : OM vs Lyon
(3,  20.00, 'Tribune',  'Virage Massilia',      600),
(3,  55.00, 'Loge',     'Tribune Jean Bouin',   150),
(3, 100.00, 'VIP',      'Loge Présidentielle',   30),
-- Match 4 : Real Madrid vs Barcelona
(4,  80.00, 'Tribune',  'Fondo Norte',         1000),
(4, 200.00, 'Loge',     'Tribune Principal',    500),
(4, 400.00, 'VIP',      'Palco VIP',            100),
-- Match 5 : Bayern Munich vs Dortmund
(5,  50.00, 'Tribune',  'Südkurve',             500),
(5, 130.00, 'Loge',     'Haupttribüne',         300),
(5, 250.00, 'VIP',      'Business Club',         80),
-- Match 6 : PSG vs Real Madrid
(6,  60.00, 'Tribune',  'Virage Nord',          800),
(6, 150.00, 'Loge',     'Tribune Officielle',   300),
(6, 300.00, 'VIP',      'Carré Or',              60);
