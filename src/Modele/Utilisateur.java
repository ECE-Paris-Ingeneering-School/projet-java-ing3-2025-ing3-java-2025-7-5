package Modele;

public class Utilisateur {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String typeUtilisateur; // "patient" ou "admin"

    /**
     * Constructeur complet
     * @param id Identifiant unique
     * @param nom Nom de famille
     * @param prenom Prénom
     * @param email Email (unique)
     * @param motDePasse Mot de passe crypté
     * @param typeUtilisateur "patient" ou "admin"
     */
    public Utilisateur(int id, String nom, String prenom, String email,
                       String motDePasse, String typeUtilisateur) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.typeUtilisateur = typeUtilisateur;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getTypeUtilisateur() {
        return typeUtilisateur;
    }

    public void setTypeUtilisateur(String typeUtilisateur) {
        this.typeUtilisateur = typeUtilisateur;
    }

    /**
     * Vérifie si le mot de passe fourni correspond
     * @param motDePasse Mot de passe à vérifier
     * @return true si correspondance, false sinon
     */
    public boolean verifierMotDePasse(String motDePasse) {
        return this.motDePasse.equals(motDePasse);
    }
}
