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
    id           INT AUTO_INCREMENT PRIMARY KEY,
    nom          VARCHAR(100) NOT NULL,
    prenom       VARCHAR(100) NOT NULL,
    email        VARCHAR(100) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL,
    role         ENUM('admin', 'user') NOT NULL DEFAULT 'user'
);

-- =============================================
-- TABLE : stade
-- =============================================
CREATE TABLE stade (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    nom      VARCHAR(100) NOT NULL,
    ville    VARCHAR(100) NOT NULL,
    capacite INT NOT NULL
);

-- =============================================
-- TABLE : match_football
-- stade_id est une cle etrangere vers stade(id)
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
-- TABLE : supporter
-- =============================================
CREATE TABLE supporter (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    nom             VARCHAR(100) NOT NULL,
    email           VARCHAR(100) NOT NULL,
    equipe_favorite VARCHAR(100) NOT NULL
);

-- =============================================
-- TABLE : ticket
-- =============================================
CREATE TABLE ticket (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    match_id  INT NOT NULL,
    prix      DOUBLE NOT NULL,
    categorie VARCHAR(50) NOT NULL,
    CONSTRAINT fk_ticket_match
        FOREIGN KEY (match_id) REFERENCES match_football(id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- =============================================
-- TABLE : reservation
-- =============================================
CREATE TABLE reservation (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    supporter_id     INT NOT NULL,
    ticket_id        INT NOT NULL,
    date_reservation DATE NOT NULL,
    CONSTRAINT fk_res_supporter
        FOREIGN KEY (supporter_id) REFERENCES supporter(id) ON DELETE CASCADE,
    CONSTRAINT fk_res_ticket
        FOREIGN KEY (ticket_id) REFERENCES ticket(id) ON DELETE CASCADE
);

-- =============================================
-- TABLE : paiement
-- Cree automatiquement a chaque reservation
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
