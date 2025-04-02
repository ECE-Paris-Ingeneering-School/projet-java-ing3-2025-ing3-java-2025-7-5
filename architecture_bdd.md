````mermaid
erDiagram
    UTILISATEUR {
        int id PK
        string nom
        string prenom
        string email
        string mot_de_passe
        string type_utilisateur "patient/admin"
    }
    
    PATIENT {
        int id PK, FK
        date date_naissance
        string adresse
        string telephone
    }
    
    SPECIALISTE {
        int id PK
        string nom
        string prenom
        string specialisation
        string telephone
        string email
    }

    RENDEZ_VOUS {
        int id PK
        int patient_id FK
        int specialiste_id FK
        datetime date_heure
        string statut "confirmé, annulé, terminé"
    }

    HISTORIQUE {
        int id PK
        int patient_id FK
        int rendez_vous_id FK
        text notes
    }

    UTILISATEUR ||--|| PATIENT : "est un"
    PATIENT ||--o{ RENDEZ_VOUS : "prend"
    SPECIALISTE ||--o{ RENDEZ_VOUS : "donne"
    PATIENT ||--o{ HISTORIQUE : "a"
    RENDEZ_VOUS ||--o{ HISTORIQUE : "est lié à"
````