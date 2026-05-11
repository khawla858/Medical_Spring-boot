-- ============================================================
--  CLINIQUE MÉDICALE — Script SQL Complet
--  Base de données : clinique_db
--  Moteur : MySQL 8.x
-- ============================================================

CREATE DATABASE IF NOT EXISTS clinique_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE clinique_db;

-- ============================================================
-- TABLE : utilisateurs
-- Centralise patients, médecins et admin (pattern SINGLETON config)
-- ============================================================
CREATE TABLE IF NOT EXISTS utilisateurs (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom           VARCHAR(100) NOT NULL,
    prenom        VARCHAR(100) NOT NULL,
    email         VARCHAR(150) NOT NULL UNIQUE,
    mot_de_passe  VARCHAR(255) NOT NULL,
    telephone     VARCHAR(20),
    role          ENUM('PATIENT','DOCTOR','ADMIN') NOT NULL DEFAULT 'PATIENT',
    actif         BOOLEAN NOT NULL DEFAULT TRUE,
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_maj      DATETIME ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_role  (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLE : medecins
-- Spécialisation d'utilisateur DOCTOR (Factory Method)
-- ============================================================
CREATE TABLE IF NOT EXISTS medecins (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    utilisateur_id  BIGINT NOT NULL UNIQUE,
    specialite      VARCHAR(100) NOT NULL,
    numero_ordre    VARCHAR(30)  NOT NULL UNIQUE,
    tarif_base      DECIMAL(10,2) NOT NULL DEFAULT 150.00,
    bio             TEXT,
    photo_url       VARCHAR(255),
    date_creation   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_medecin_user FOREIGN KEY (utilisateur_id)
        REFERENCES utilisateurs(id) ON DELETE CASCADE,
    INDEX idx_specialite (specialite)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLE : patients
-- Spécialisation d'utilisateur PATIENT (Factory Method)
-- ============================================================
CREATE TABLE IF NOT EXISTS patients (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    utilisateur_id  BIGINT NOT NULL UNIQUE,
    cin             VARCHAR(20) UNIQUE,
    date_naissance  DATE,
    groupe_sanguin  VARCHAR(5),
    adresse         VARCHAR(255),
    allergies       TEXT,
    antecedents     TEXT,
    numero_dossier  VARCHAR(30) UNIQUE,
    date_creation   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_patient_user FOREIGN KEY (utilisateur_id)
        REFERENCES utilisateurs(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLE : plannings
-- Horaires de disponibilité des médecins (Iterator pattern)
-- ============================================================
CREATE TABLE IF NOT EXISTS plannings (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    medecin_id      BIGINT NOT NULL,
    jour_semaine    ENUM('LUNDI','MARDI','MERCREDI','JEUDI','VENDREDI','SAMEDI','DIMANCHE') NOT NULL,
    heure_debut     TIME NOT NULL,
    heure_fin       TIME NOT NULL,
    actif           BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_planning_medecin FOREIGN KEY (medecin_id)
        REFERENCES medecins(id) ON DELETE CASCADE,
    UNIQUE KEY uq_planning (medecin_id, jour_semaine, heure_debut),
    INDEX idx_medecin_jour (medecin_id, jour_semaine)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLE : rendez_vous
-- Cœur du système (State pattern sur statut)
-- ============================================================
CREATE TABLE IF NOT EXISTS rendez_vous (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id      BIGINT NOT NULL,
    medecin_id      BIGINT NOT NULL,
    date_rdv        DATETIME NOT NULL,
    motif           VARCHAR(255),
    statut          ENUM('PENDING','CONFIRMED','CANCELED','COMPLETED','RESCHEDULED')
                    NOT NULL DEFAULT 'PENDING',
    type_facturation ENUM('CNAM','PRIVE','ASSURANCE') NOT NULL DEFAULT 'PRIVE',
    montant_final   DECIMAL(10,2),
    notes_medecin   TEXT,
    date_creation   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_maj        DATETIME ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_rdv_patient FOREIGN KEY (patient_id)
        REFERENCES patients(id) ON DELETE CASCADE,
    CONSTRAINT fk_rdv_medecin FOREIGN KEY (medecin_id)
        REFERENCES medecins(id) ON DELETE CASCADE,
    INDEX idx_patient_rdv  (patient_id),
    INDEX idx_medecin_rdv  (medecin_id),
    INDEX idx_date_rdv     (date_rdv),
    INDEX idx_statut       (statut)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLE : consultations
-- Résultat d'un RDV CONFIRMED (Template Method)
-- ============================================================
CREATE TABLE IF NOT EXISTS consultations (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    rendez_vous_id  BIGINT NOT NULL UNIQUE,
    diagnostic      TEXT,
    prescription    TEXT,
    observations    TEXT,
    duree_minutes   INT DEFAULT 30,
    date_consultation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_consult_rdv FOREIGN KEY (rendez_vous_id)
        REFERENCES rendez_vous(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLE : notifications (Observer pattern)
-- Historique de toutes les notifications envoyées
-- ============================================================
CREATE TABLE IF NOT EXISTS notifications (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    utilisateur_id  BIGINT NOT NULL,
    type_event      VARCHAR(50) NOT NULL,
    canal           ENUM('EMAIL','SMS','IN_APP') NOT NULL DEFAULT 'IN_APP',
    sujet           VARCHAR(255),
    message         TEXT NOT NULL,
    lu              BOOLEAN NOT NULL DEFAULT FALSE,
    date_envoi      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notif_user FOREIGN KEY (utilisateur_id)
        REFERENCES utilisateurs(id) ON DELETE CASCADE,
    INDEX idx_notif_user (utilisateur_id),
    INDEX idx_notif_lu   (lu)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLE : dossiers_medicaux (Prototype pattern)
-- Dossier clonable pour chaque patient
-- ============================================================
CREATE TABLE IF NOT EXISTS dossiers_medicaux (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id      BIGINT NOT NULL,
    numero_dossier  VARCHAR(30) NOT NULL UNIQUE,
    antecedents     TEXT,
    allergies       TEXT,
    groupe_sanguin  VARCHAR(5),
    traitements_en_cours TEXT,
    annee           INT NOT NULL DEFAULT (YEAR(CURDATE())),
    date_creation   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_maj        DATETIME ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_dossier_patient FOREIGN KEY (patient_id)
        REFERENCES patients(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- DONNÉES INITIALES (Admin + médecins + patients demo)
-- ============================================================

-- Admin (mot de passe = admin123 en BCrypt)
INSERT IGNORE INTO utilisateurs (nom, prenom, email, mot_de_passe, telephone, role) VALUES
('Admin', 'Clinique', 'admin@clinique.ma',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
 '0537-000000', 'ADMIN');

-- Médecins (mot de passe = doctor123)
INSERT IGNORE INTO utilisateurs (nom, prenom, email, mot_de_passe, telephone, role) VALUES
('Alami',     'Youssef', 'y.alami@clinique.ma',
 '$2a$10$8K1p/a0dR1xqM8K3a5kXOu67c5RnMh5LZ9hVFN.kNVIZ2FJsFSZ5G', '0661-111111', 'DOCTOR'),
('Benali',    'Fatima',  'f.benali@clinique.ma',
 '$2a$10$8K1p/a0dR1xqM8K3a5kXOu67c5RnMh5LZ9hVFN.kNVIZ2FJsFSZ5G', '0662-222222', 'DOCTOR'),
('Cherkaoui', 'Omar',    'o.cherkaoui@clinique.ma',
 '$2a$10$8K1p/a0dR1xqM8K3a5kXOu67c5RnMh5LZ9hVFN.kNVIZ2FJsFSZ5G', '0663-333333', 'DOCTOR'),
('Rachidi',   'Leila',   'l.rachidi@clinique.ma',
 '$2a$10$8K1p/a0dR1xqM8K3a5kXOu67c5RnMh5LZ9hVFN.kNVIZ2FJsFSZ5G', '0664-444444', 'DOCTOR');

-- Patients (mot de passe = patient123)
INSERT IGNORE INTO utilisateurs (nom, prenom, email, mot_de_passe, telephone, role) VALUES
('Idrissi',  'Mohamed', 'm.idrissi@gmail.com',
 '$2a$10$UxU8vWnLkNKSq7zFbZ5xDeghGNWj5tK.mhCZsuvKzv0N/0ynUEKKG', '0671-111111', 'PATIENT'),
('Zaki',     'Samira',  's.zaki@gmail.com',
 '$2a$10$UxU8vWnLkNKSq7zFbZ5xDeghGNWj5tK.mhCZsuvKzv0N/0ynUEKKG', '0672-222222', 'PATIENT'),
('El Fassi', 'Karim',   'k.elfassi@gmail.com',
 '$2a$10$UxU8vWnLkNKSq7zFbZ5xDeghGNWj5tK.mhCZsuvKzv0N/0ynUEKKG', '0673-333333', 'PATIENT');

-- Profils médecins
INSERT IGNORE INTO medecins (utilisateur_id, specialite, numero_ordre, tarif_base, bio)
SELECT id, 'Médecine Générale', 'MG-001', 150.00, 'Médecin généraliste avec 10 ans d expérience.'
FROM utilisateurs WHERE email='y.alami@clinique.ma';

INSERT IGNORE INTO medecins (utilisateur_id, specialite, numero_ordre, tarif_base, bio)
SELECT id, 'Cardiologie', 'CARD-001', 350.00, 'Cardiologue spécialisée en maladies cardiovasculaires.'
FROM utilisateurs WHERE email='f.benali@clinique.ma';

INSERT IGNORE INTO medecins (utilisateur_id, specialite, numero_ordre, tarif_base, bio)
SELECT id, 'Pédiatrie', 'PED-001', 250.00, 'Pédiatre dédié aux soins des enfants.'
FROM utilisateurs WHERE email='o.cherkaoui@clinique.ma';

INSERT IGNORE INTO medecins (utilisateur_id, specialite, numero_ordre, tarif_base, bio)
SELECT id, 'Dermatologie', 'DERM-001', 300.00, 'Dermatologue experte en maladies cutanées.'
FROM utilisateurs WHERE email='l.rachidi@clinique.ma';

-- Profils patients
INSERT IGNORE INTO patients (utilisateur_id, cin, date_naissance, groupe_sanguin, adresse, numero_dossier)
SELECT id, 'AB123456', '1985-03-15', 'A+', 'Rue des Roses, Oujda', 'DOS-2025-0001'
FROM utilisateurs WHERE email='m.idrissi@gmail.com';

INSERT IGNORE INTO patients (utilisateur_id, cin, date_naissance, groupe_sanguin, adresse, numero_dossier)
SELECT id, 'CD789012', '1992-07-22', 'O+', 'Boulevard Mohammed V, Nador', 'DOS-2025-0002'
FROM utilisateurs WHERE email='s.zaki@gmail.com';

INSERT IGNORE INTO patients (utilisateur_id, cin, date_naissance, groupe_sanguin, adresse, numero_dossier)
SELECT id, 'EF345678', '1978-11-05', 'B+', 'Avenue de la Paix, Berkane', 'DOS-2025-0003'
FROM utilisateurs WHERE email='k.elfassi@gmail.com';

-- Plannings médecins
INSERT IGNORE INTO plannings (medecin_id, jour_semaine, heure_debut, heure_fin)
SELECT m.id, 'LUNDI', '08:00', '17:00' FROM medecins m JOIN utilisateurs u ON m.utilisateur_id=u.id WHERE u.email='y.alami@clinique.ma';
INSERT IGNORE INTO plannings (medecin_id, jour_semaine, heure_debut, heure_fin)
SELECT m.id, 'MERCREDI', '08:00', '17:00' FROM medecins m JOIN utilisateurs u ON m.utilisateur_id=u.id WHERE u.email='y.alami@clinique.ma';
INSERT IGNORE INTO plannings (medecin_id, jour_semaine, heure_debut, heure_fin)
SELECT m.id, 'VENDREDI', '08:00', '12:00' FROM medecins m JOIN utilisateurs u ON m.utilisateur_id=u.id WHERE u.email='y.alami@clinique.ma';

INSERT IGNORE INTO plannings (medecin_id, jour_semaine, heure_debut, heure_fin)
SELECT m.id, 'MARDI', '09:00', '17:00' FROM medecins m JOIN utilisateurs u ON m.utilisateur_id=u.id WHERE u.email='f.benali@clinique.ma';
INSERT IGNORE INTO plannings (medecin_id, jour_semaine, heure_debut, heure_fin)
SELECT m.id, 'JEUDI', '09:00', '17:00' FROM medecins m JOIN utilisateurs u ON m.utilisateur_id=u.id WHERE u.email='f.benali@clinique.ma';

-- Rendez-vous de démo
INSERT IGNORE INTO rendez_vous (patient_id, medecin_id, date_rdv, motif, statut, type_facturation)
SELECT p.id, m.id, DATE_ADD(NOW(), INTERVAL 2 DAY), 'Consultation générale', 'PENDING', 'CNAM'
FROM patients p JOIN utilisateurs up ON p.utilisateur_id=up.id,
     medecins m JOIN utilisateurs um ON m.utilisateur_id=um.id
WHERE up.email='m.idrissi@gmail.com' AND um.email='y.alami@clinique.ma'
LIMIT 1;

INSERT IGNORE INTO rendez_vous (patient_id, medecin_id, date_rdv, motif, statut, type_facturation)
SELECT p.id, m.id, DATE_ADD(NOW(), INTERVAL 5 DAY), 'Douleurs thoraciques', 'CONFIRMED', 'ASSURANCE'
FROM patients p JOIN utilisateurs up ON p.utilisateur_id=up.id,
     medecins m JOIN utilisateurs um ON m.utilisateur_id=um.id
WHERE up.email='s.zaki@gmail.com' AND um.email='f.benali@clinique.ma'
LIMIT 1;

-- ============================================================
-- VUES utiles pour statistiques
-- ============================================================
CREATE OR REPLACE VIEW v_stats_dashboard AS
SELECT
    (SELECT COUNT(*) FROM utilisateurs WHERE role='PATIENT' AND actif=TRUE)   AS total_patients,
    (SELECT COUNT(*) FROM utilisateurs WHERE role='DOCTOR'  AND actif=TRUE)   AS total_medecins,
    (SELECT COUNT(*) FROM rendez_vous)                                          AS total_rdv,
    (SELECT COUNT(*) FROM rendez_vous WHERE statut='PENDING')                  AS rdv_pending,
    (SELECT COUNT(*) FROM rendez_vous WHERE statut='CONFIRMED')                AS rdv_confirmes,
    (SELECT COUNT(*) FROM rendez_vous WHERE statut='CANCELED')                 AS rdv_annules,
    (SELECT COUNT(*) FROM rendez_vous WHERE statut='COMPLETED')                AS rdv_termines,
    (SELECT COUNT(*) FROM notifications WHERE lu=FALSE)                        AS notifs_non_lues;

-- ============================================================
-- FIN DU SCRIPT SQL
-- Connexion : mysql -u root -p < schema.sql
-- ============================================================