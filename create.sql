CREATE TABLE UTILISATEUR (
                             id INT PRIMARY KEY AUTO_INCREMENT,
                             nom VARCHAR(191) NOT NULL,
                             prenom VARCHAR(191) NOT NULL,
                             email VARCHAR(191) UNIQUE NOT NULL,
                             mot_de_passe VARCHAR(255) NOT NULL,
                             type_utilisateur ENUM('patient', 'admin') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE PATIENT (
                         id INT PRIMARY KEY,
                         date_naissance DATE NOT NULL,
                         adresse TEXT NOT NULL,
                         telephone VARCHAR(20) NOT NULL,
                         FOREIGN KEY (id) REFERENCES UTILISATEUR(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE SPECIALISTE (
                             id INT PRIMARY KEY AUTO_INCREMENT,
                             nom VARCHAR(191) NOT NULL,
                             prenom VARCHAR(191) NOT NULL,
                             specialisation VARCHAR(191) NOT NULL,
                             telephone VARCHAR(20) NOT NULL,
                             email VARCHAR(191) UNIQUE NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE RENDEZ_VOUS (
                             id INT PRIMARY KEY AUTO_INCREMENT,
                             patient_id INT NOT NULL,
                             specialiste_id INT NOT NULL,
                             date_heure DATETIME NOT NULL,
                             statut ENUM('confirmé', 'annulé', 'terminé') NOT NULL,
                             FOREIGN KEY (patient_id) REFERENCES PATIENT(id) ON DELETE CASCADE,
                             FOREIGN KEY (specialiste_id) REFERENCES SPECIALISTE(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE HISTORIQUE (
                            id INT PRIMARY KEY AUTO_INCREMENT,
                            patient_id INT NOT NULL,
                            rendez_vous_id INT NOT NULL,
                            notes TEXT NOT NULL,
                            FOREIGN KEY (patient_id) REFERENCES PATIENT(id) ON DELETE CASCADE,
                            FOREIGN KEY (rendez_vous_id) REFERENCES RENDEZ_VOUS(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;