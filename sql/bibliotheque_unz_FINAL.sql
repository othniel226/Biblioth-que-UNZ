-- ============================================================
-- BASE DE DONNÉES FINALE — BIBLIOTHÈQUE UNZ
-- Université Norbert Zongo, Koudougou, Burkina Faso
-- Génie Logiciel L3 | Dr OUEDRAOGO Moïse | Mai 2026
-- ============================================================
-- EXÉCUTION : mysql -u root -p < bibliotheque_unz_FINAL.sql
-- MOT DE PASSE POUR TOUS LES COMPTES : password
-- ============================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- Supprimer et recréer la base
DROP DATABASE IF EXISTS bibliotheque_unz;
CREATE DATABASE bibliotheque_unz CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bibliotheque_unz;

-- ============================================================
-- TABLE : categories
-- ============================================================
CREATE TABLE categories (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    nom         VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    couleur     VARCHAR(10)  DEFAULT '#1a237e',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLE : utilisateurs
-- La colonne dtype est OBLIGATOIRE pour l'héritage JPA JOINED
-- Elle indique à Hibernate le type concret (Etudiant, Bibliothecaire, etc.)
-- ============================================================
CREATE TABLE utilisateurs (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    dtype             VARCHAR(31)  NOT NULL,
    prenom            VARCHAR(100) NOT NULL,
    nom               VARCHAR(100) NOT NULL,
    email             VARCHAR(150) NOT NULL UNIQUE,
    mot_de_passe      VARCHAR(255) NOT NULL,
    role              VARCHAR(20)  NOT NULL,
    actif             TINYINT(1)   NOT NULL DEFAULT 1,
    date_creation     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_role (role),
    KEY idx_actif (actif)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLE : etudiants
-- ============================================================
CREATE TABLE etudiants (
    id        BIGINT       NOT NULL,
    matricule VARCHAR(20)  NOT NULL UNIQUE,
    filiere   VARCHAR(100) NOT NULL,
    niveau    VARCHAR(10)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_etudiant_utilisateur
        FOREIGN KEY (id) REFERENCES utilisateurs(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLE : bibliothecaires
-- ============================================================
CREATE TABLE bibliothecaires (
    id           BIGINT      NOT NULL,
    badge_numero VARCHAR(20) NOT NULL UNIQUE,
    service      VARCHAR(100),
    PRIMARY KEY (id),
    CONSTRAINT fk_biblio_utilisateur
        FOREIGN KEY (id) REFERENCES utilisateurs(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLE : administrateurs
-- ============================================================
CREATE TABLE administrateurs (
    id          BIGINT      NOT NULL,
    departement VARCHAR(100),
    PRIMARY KEY (id),
    CONSTRAINT fk_admin_utilisateur
        FOREIGN KEY (id) REFERENCES utilisateurs(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLE : ouvrages
-- ============================================================
CREATE TABLE ouvrages (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    titre             VARCHAR(300) NOT NULL,
    isbn              VARCHAR(20)  UNIQUE,
    auteur            VARCHAR(200) NOT NULL,
    editeur           VARCHAR(150),
    annee_publication INT,
    description       TEXT,
    image_couverture  VARCHAR(500),
    categorie_id      BIGINT,
    archive           TINYINT(1)   NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT fk_ouvrage_categorie
        FOREIGN KEY (categorie_id) REFERENCES categories(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLE : exemplaires
-- ============================================================
CREATE TABLE exemplaires (
    id          BIGINT      NOT NULL AUTO_INCREMENT,
    code_barres VARCHAR(50) NOT NULL UNIQUE,
    statut      VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE',
    ouvrage_id  BIGINT      NOT NULL,
    notes       VARCHAR(300),
    PRIMARY KEY (id),
    CONSTRAINT fk_exemplaire_ouvrage
        FOREIGN KEY (ouvrage_id) REFERENCES ouvrages(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLE : emprunts
-- ============================================================
CREATE TABLE emprunts (
    id                BIGINT      NOT NULL AUTO_INCREMENT,
    etudiant_id       BIGINT      NOT NULL,
    exemplaire_id     BIGINT      NOT NULL,
    bibliothecaire_id BIGINT,
    date_emprunt      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_retour_prevu DATE        NOT NULL,
    date_retour_reel  DATE,
    statut            VARCHAR(20) NOT NULL DEFAULT 'EN_COURS',
    prolonge          TINYINT(1)  NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT fk_emprunt_etudiant
        FOREIGN KEY (etudiant_id) REFERENCES etudiants(id),
    CONSTRAINT fk_emprunt_exemplaire
        FOREIGN KEY (exemplaire_id) REFERENCES exemplaires(id),
    CONSTRAINT fk_emprunt_bibliothecaire
        FOREIGN KEY (bibliothecaire_id) REFERENCES bibliothecaires(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLE : reservations
-- ============================================================
CREATE TABLE reservations (
    id               BIGINT      NOT NULL AUTO_INCREMENT,
    etudiant_id      BIGINT      NOT NULL,
    ouvrage_id       BIGINT      NOT NULL,
    statut           VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE',
    date_reservation DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_confirmation DATETIME,
    date_expiration  DATETIME,
    position_file    INT,
    PRIMARY KEY (id),
    CONSTRAINT fk_reservation_etudiant
        FOREIGN KEY (etudiant_id) REFERENCES etudiants(id),
    CONSTRAINT fk_reservation_ouvrage
        FOREIGN KEY (ouvrage_id) REFERENCES ouvrages(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLE : penalites
-- ============================================================
CREATE TABLE penalites (
    id                 BIGINT        NOT NULL AUTO_INCREMENT,
    emprunt_id         BIGINT        NOT NULL UNIQUE,
    montant            DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    jours_retard       BIGINT        NOT NULL DEFAULT 0,
    payee              TINYINT(1)    NOT NULL DEFAULT 0,
    date_creation      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_paiement      DATETIME,
    strategie_utilisee VARCHAR(50),
    PRIMARY KEY (id),
    CONSTRAINT fk_penalite_emprunt
        FOREIGN KEY (emprunt_id) REFERENCES emprunts(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLE : notifications
-- ============================================================
CREATE TABLE notifications (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    destinataire_id BIGINT       NOT NULL,
    type            VARCHAR(30)  NOT NULL,
    sujet           VARCHAR(200) NOT NULL,
    message         TEXT,
    lu              TINYINT(1)   NOT NULL DEFAULT 0,
    date_envoi      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_notif_destinataire
        FOREIGN KEY (destinataire_id) REFERENCES utilisateurs(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLE : configurations_systeme
-- ============================================================
CREATE TABLE configurations_systeme (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    cle         VARCHAR(100) NOT NULL UNIQUE,
    valeur      VARCHAR(500) NOT NULL,
    description VARCHAR(300),
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- DONNÉES : Configuration système
-- ============================================================
INSERT INTO configurations_systeme (cle, valeur, description) VALUES
('DUREE_EMPRUNT_JOURS',         '14',  'Durée par défaut d un emprunt en jours'),
('MAX_EMPRUNTS_SIMULTANES',     '3',   'Quota emprunts simultanés par étudiant'),
('MAX_RESERVATIONS_SIMULTANES', '5',   'Quota réservations actives par étudiant'),
('PENALITE_PAR_JOUR_FCFA',      '100', 'Pénalité par jour de retard en FCFA'),
('SEUIL_BLOCAGE_FCFA',          '500', 'Seuil pénalités bloquant les emprunts'),
('DUREE_RESERVATION_HEURES',    '48',  'Délai pour récupérer ouvrage réservé'),
('JOURS_AVANT_RAPPEL',          '3',   'Jours avant retour pour envoyer rappel'),
('STRATEGIE_PENALITE',          'FIXE','Stratégie : FIXE ou PROGRESSIF');

-- ============================================================
-- DONNÉES : Catégories
-- ============================================================
INSERT INTO categories (nom, description, couleur) VALUES
('Informatique',    'Programmation, réseaux, IA, bases de données', '#1a237e'),
('Mathématiques',   'Algèbre, analyse, statistiques',              '#1b5e20'),
('Physique',        'Mécanique, thermodynamique, optique',         '#b71c1c'),
('Chimie',          'Chimie organique, inorganique',               '#e65100'),
('Biologie',        'Sciences du vivant, génétique',               '#004d40'),
('Littérature',     'Romans, poésie, littérature africaine',       '#4a148c'),
('Histoire',        'Histoire du Burkina Faso et du monde',        '#827717'),
('Économie',        'Micro, macroéconomie, finance',               '#006064'),
('Droit',           'Droit civil, pénal, OHADA',                   '#37474f'),
('Sciences sociales','Sociologie, anthropologie, géographie',      '#3e2723');

-- ============================================================
-- DONNÉES : Utilisateurs
-- IMPORTANT : dtype = nom exact de la classe Java (Etudiant, Bibliothecaire, Administrateur)
-- Mot de passe pour TOUS : password
-- Hash BCrypt Spring Security : $2a$10$N.RkCnbXOTkVfGm8RPyuheH3TmfPa/PSmr.s.dBIEFxF7eJAXNVeK
-- ============================================================
INSERT INTO utilisateurs (dtype, prenom, nom, email, mot_de_passe, role, actif) VALUES
('Administrateur', 'Super',    'Administrateur',  'admin@bibliotheque-unz.bf',
 '$2a$10$N.RkCnbXOTkVfGm8RPyuheH3TmfPa/PSmr.s.dBIEFxF7eJAXNVeK', 'ADMINISTRATEUR', 1),
('Bibliothecaire', 'Alice',    'Kaboré',          'alice.kabore@bibliotheque-unz.bf',
 '$2a$10$N.RkCnbXOTkVfGm8RPyuheH3TmfPa/PSmr.s.dBIEFxF7eJAXNVeK', 'BIBLIOTHECAIRE', 1),
('Bibliothecaire', 'Boureima', 'Sawadogo',        'boureima.sawadogo@bibliotheque-unz.bf',
 '$2a$10$N.RkCnbXOTkVfGm8RPyuheH3TmfPa/PSmr.s.dBIEFxF7eJAXNVeK', 'BIBLIOTHECAIRE', 1),
('Etudiant',       'Moussa',   'Ouédraogo',       'moussa.ouedraogo@etud.unz.bf',
 '$2a$10$N.RkCnbXOTkVfGm8RPyuheH3TmfPa/PSmr.s.dBIEFxF7eJAXNVeK', 'ETUDIANT', 1),
('Etudiant',       'Aliou',    'Barry',           'aliou.barry@etud.unz.bf',
 '$2a$10$N.RkCnbXOTkVfGm8RPyuheH3TmfPa/PSmr.s.dBIEFxF7eJAXNVeK', 'ETUDIANT', 1),
('Etudiant',       'Kadiatou', 'Coulibaly',       'kadiatou.coulibaly@etud.unz.bf',
 '$2a$10$N.RkCnbXOTkVfGm8RPyuheH3TmfPa/PSmr.s.dBIEFxF7eJAXNVeK', 'ETUDIANT', 1),
('Etudiant',       'Fatima',   'Zoungrana',       'fatima.zoungrana@etud.unz.bf',
 '$2a$10$N.RkCnbXOTkVfGm8RPyuheH3TmfPa/PSmr.s.dBIEFxF7eJAXNVeK', 'ETUDIANT', 1),
('Etudiant',       'Ibrahim',  'Traoré',          'ibrahim.traore@etud.unz.bf',
 '$2a$10$N.RkCnbXOTkVfGm8RPyuheH3TmfPa/PSmr.s.dBIEFxF7eJAXNVeK', 'ETUDIANT', 1),
('Etudiant',       'Mariam',   'Compaoré',        'mariam.compaore@etud.unz.bf',
 '$2a$10$N.RkCnbXOTkVfGm8RPyuheH3TmfPa/PSmr.s.dBIEFxF7eJAXNVeK', 'ETUDIANT', 1),
('Etudiant',       'Issouf',   'Zongo',           'issouf.zongo@etud.unz.bf',
 '$2a$10$N.RkCnbXOTkVfGm8RPyuheH3TmfPa/PSmr.s.dBIEFxF7eJAXNVeK', 'ETUDIANT', 1),
('Etudiant',       'Aminata',  'Diallo',          'aminata.diallo@etud.unz.bf',
 '$2a$10$N.RkCnbXOTkVfGm8RPyuheH3TmfPa/PSmr.s.dBIEFxF7eJAXNVeK', 'ETUDIANT', 1);

-- Administrateur
INSERT INTO administrateurs (id, departement)
SELECT id, 'Direction des Services Informatiques'
FROM utilisateurs WHERE email = 'admin@bibliotheque-unz.bf';

-- Bibliothécaires
INSERT INTO bibliothecaires (id, badge_numero, service)
SELECT id, 'BIB-001', 'Section Ouvrages Généraux'
FROM utilisateurs WHERE email = 'alice.kabore@bibliotheque-unz.bf';

INSERT INTO bibliothecaires (id, badge_numero, service)
SELECT id, 'BIB-002', 'Section Périodiques'
FROM utilisateurs WHERE email = 'boureima.sawadogo@bibliotheque-unz.bf';

-- Étudiants
INSERT INTO etudiants (id, matricule, filiere, niveau)
SELECT u.id,
    CASE u.email
        WHEN 'moussa.ouedraogo@etud.unz.bf'  THEN '2023INF001'
        WHEN 'aliou.barry@etud.unz.bf'        THEN '2023INF002'
        WHEN 'kadiatou.coulibaly@etud.unz.bf' THEN '2022INF015'
        WHEN 'fatima.zoungrana@etud.unz.bf'   THEN '2023MAT001'
        WHEN 'ibrahim.traore@etud.unz.bf'     THEN '2022INF022'
        WHEN 'mariam.compaore@etud.unz.bf'    THEN '2023ECO001'
        WHEN 'issouf.zongo@etud.unz.bf'       THEN '2021INF008'
        WHEN 'aminata.diallo@etud.unz.bf'     THEN '2023LIT001'
    END,
    CASE u.email
        WHEN 'moussa.ouedraogo@etud.unz.bf'  THEN 'Informatique'
        WHEN 'aliou.barry@etud.unz.bf'        THEN 'Informatique'
        WHEN 'kadiatou.coulibaly@etud.unz.bf' THEN 'Informatique'
        WHEN 'fatima.zoungrana@etud.unz.bf'   THEN 'Mathématiques'
        WHEN 'ibrahim.traore@etud.unz.bf'     THEN 'Informatique'
        WHEN 'mariam.compaore@etud.unz.bf'    THEN 'Économie'
        WHEN 'issouf.zongo@etud.unz.bf'       THEN 'Informatique'
        WHEN 'aminata.diallo@etud.unz.bf'     THEN 'Littérature'
    END,
    CASE u.email
        WHEN 'moussa.ouedraogo@etud.unz.bf'  THEN 'L3'
        WHEN 'aliou.barry@etud.unz.bf'        THEN 'L3'
        WHEN 'kadiatou.coulibaly@etud.unz.bf' THEN 'L2'
        WHEN 'fatima.zoungrana@etud.unz.bf'   THEN 'L3'
        WHEN 'ibrahim.traore@etud.unz.bf'     THEN 'L2'
        WHEN 'mariam.compaore@etud.unz.bf'    THEN 'L3'
        WHEN 'issouf.zongo@etud.unz.bf'       THEN 'M1'
        WHEN 'aminata.diallo@etud.unz.bf'     THEN 'L3'
    END
FROM utilisateurs u WHERE u.role = 'ETUDIANT';

-- ============================================================
-- DONNÉES : Ouvrages
-- ============================================================
INSERT INTO ouvrages (titre, isbn, auteur, editeur, annee_publication, description, categorie_id) VALUES
('Introduction au Génie Logiciel',   '978-0-13-468599-1', 'Ian Sommerville',      'Pearson',   2019, 'Référence mondiale en génie logiciel.',   1),
('Design Patterns — GoF',            '978-2-12-465612-8', 'Erich Gamma et al.',   'Vuibert',   2013, 'Les 23 patterns GoF incontournables.',    1),
('Algorithmes en Java',              '978-0-32-157351-3', 'Robert Sedgewick',     'Addison',   2011, 'Structures de données et algorithmes.',   1),
('Clean Code — Coder proprement',    '978-0-13-235088-4', 'Robert C. Martin',     'Pearson',   2008, 'Principes SOLID et bonnes pratiques.',    1),
('Spring Boot en action',            '978-2-74-402476-1', 'Craig Walls',          'Manning',   2022, 'Développement Spring Boot moderne.',      1),
('Les Soleils des Indépendances',    '978-2-07-036224-1', 'Ahmadou Kourouma',     'Seuil',     1970, 'Premier roman majeur de la littérature africaine.', 6),
('Une si longue lettre',             '978-2-26-000536-5', 'Mariama Bâ',           'NE Africaines', 1979, 'Chef-d oeuvre, Prix Noma 1980.',      6),
('Analyse Mathématique T1',          '978-3-54-065034-3', 'Walter Rudin',         'McGraw',    2009, 'Référence en analyse mathématique.',     2),
('Probabilités et Statistiques',     '978-2-84-703071-2', 'Yadolah Dodge',        'Springer',  2004, 'Introduction aux probabilités.',          2),
('Physique Générale — Mécanique',    '978-2-10-005654-1', 'Eugene Hecht',         'De Boeck',  2012, 'Mécanique classique universitaire.',      3),
('Introduction à l économie',        '978-2-04-732001-5', 'Paul Krugman',         'De Boeck',  2018, 'Micro et macroéconomie.',                 8),
('Droit des affaires — OHADA',       '978-2-27-501234-6', 'Philippe Merle',       'Dalloz',    2020, 'Droit OHADA Afrique subsaharienne.',      9),
('Histoire du Burkina Faso',         '978-2-86-537052-3', 'Joseph Ki-Zerbo',      'Présence Africaine', 1978, 'Histoire de l Afrique Noire.', 7);

-- ============================================================
-- DONNÉES : Exemplaires
-- ============================================================
INSERT INTO exemplaires (code_barres, statut, ouvrage_id) VALUES
('EX-001-A', 'DISPONIBLE', 1), ('EX-001-B', 'EMPRUNTE',   1), ('EX-001-C', 'DISPONIBLE', 1),
('EX-002-A', 'EMPRUNTE',   2), ('EX-002-B', 'RESERVE',    2),
('EX-003-A', 'DISPONIBLE', 3), ('EX-003-B', 'DISPONIBLE', 3), ('EX-003-C', 'EMPRUNTE',   3),
('EX-004-A', 'DISPONIBLE', 4), ('EX-004-B', 'DISPONIBLE', 4),
('EX-005-A', 'DISPONIBLE', 5), ('EX-005-B', 'EMPRUNTE',   5),
('EX-006-A', 'DISPONIBLE', 6), ('EX-006-B', 'DISPONIBLE', 6),
('EX-007-A', 'DISPONIBLE', 7), ('EX-007-B', 'EMPRUNTE',   7),
('EX-008-A', 'DISPONIBLE', 8), ('EX-008-B', 'DISPONIBLE', 8),
('EX-009-A', 'DISPONIBLE', 9),
('EX-010-A', 'DISPONIBLE', 10), ('EX-010-B', 'DISPONIBLE', 10),
('EX-011-A', 'EMPRUNTE',   11), ('EX-011-B', 'DISPONIBLE', 11),
('EX-012-A', 'DISPONIBLE', 12), ('EX-012-B', 'DISPONIBLE', 12),
('EX-013-A', 'DISPONIBLE', 13), ('EX-013-B', 'DISPONIBLE', 13);

-- ============================================================
-- DONNÉES : Emprunts de test
-- ============================================================
INSERT INTO emprunts (etudiant_id, exemplaire_id, date_emprunt, date_retour_prevu, statut, prolonge) VALUES
((SELECT id FROM utilisateurs WHERE email='moussa.ouedraogo@etud.unz.bf'),
 (SELECT id FROM exemplaires WHERE code_barres='EX-001-B'),
 NOW() - INTERVAL 5 DAY, CURDATE() + INTERVAL 9 DAY, 'EN_COURS', 0),

((SELECT id FROM utilisateurs WHERE email='aliou.barry@etud.unz.bf'),
 (SELECT id FROM exemplaires WHERE code_barres='EX-002-A'),
 NOW() - INTERVAL 19 DAY, CURDATE() - INTERVAL 5 DAY, 'EN_RETARD', 0),

((SELECT id FROM utilisateurs WHERE email='fatima.zoungrana@etud.unz.bf'),
 (SELECT id FROM exemplaires WHERE code_barres='EX-003-C'),
 NOW() - INTERVAL 3 DAY, CURDATE() + INTERVAL 11 DAY, 'EN_COURS', 0),

((SELECT id FROM utilisateurs WHERE email='ibrahim.traore@etud.unz.bf'),
 (SELECT id FROM exemplaires WHERE code_barres='EX-005-B'),
 NOW() - INTERVAL 10 DAY, CURDATE() + INTERVAL 4 DAY, 'PROLONGE', 1),

((SELECT id FROM utilisateurs WHERE email='issouf.zongo@etud.unz.bf'),
 (SELECT id FROM exemplaires WHERE code_barres='EX-007-B'),
 NOW() - INTERVAL 17 DAY, CURDATE() - INTERVAL 3 DAY, 'EN_RETARD', 0),

((SELECT id FROM utilisateurs WHERE email='mariam.compaore@etud.unz.bf'),
 (SELECT id FROM exemplaires WHERE code_barres='EX-011-A'),
 NOW() - INTERVAL 2 DAY, CURDATE() + INTERVAL 12 DAY, 'EN_COURS', 0);

-- ============================================================
-- DONNÉES : Pénalités
-- ============================================================
INSERT INTO penalites (emprunt_id, montant, jours_retard, payee, strategie_utilisee) VALUES
((SELECT e.id FROM emprunts e JOIN utilisateurs u ON e.etudiant_id = u.id
  WHERE u.email='aliou.barry@etud.unz.bf' AND e.statut='EN_RETARD'),
 500.00, 5, 0, 'Tarif fixe — 100 FCFA/jour'),

((SELECT e.id FROM emprunts e JOIN utilisateurs u ON e.etudiant_id = u.id
  WHERE u.email='issouf.zongo@etud.unz.bf' AND e.statut='EN_RETARD'),
 300.00, 3, 0, 'Tarif fixe — 100 FCFA/jour');

-- ============================================================
-- DONNÉES : Réservation
-- ============================================================
INSERT INTO reservations (etudiant_id, ouvrage_id, statut, date_reservation, position_file) VALUES
((SELECT id FROM utilisateurs WHERE email='kadiatou.coulibaly@etud.unz.bf'),
 (SELECT id FROM ouvrages WHERE isbn='978-2-12-465612-8'),
 'EN_ATTENTE', NOW() - INTERVAL 2 DAY, 1);

-- ============================================================
-- VÉRIFICATION
-- ============================================================
SELECT '=== VÉRIFICATION FINALE ===' AS info;
SELECT CONCAT('Utilisateurs : ', COUNT(*)) AS info FROM utilisateurs;
SELECT CONCAT('Étudiants    : ', COUNT(*)) AS info FROM etudiants;
SELECT CONCAT('Ouvrages     : ', COUNT(*)) AS info FROM ouvrages;
SELECT CONCAT('Exemplaires  : ', COUNT(*)) AS info FROM exemplaires;
SELECT CONCAT('Emprunts     : ', COUNT(*)) AS info FROM emprunts;
SELECT CONCAT('Pénalités    : ', COUNT(*)) AS info FROM penalites;
SELECT '=== BASE CRÉÉE AVEC SUCCÈS ===' AS info;
SELECT 'Mot de passe pour tous : password' AS comptes;
