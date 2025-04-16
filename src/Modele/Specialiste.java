package Modele;

/**
 * Classe représentant un spécialiste médical
 * @author [Votre nom]
 */
public class Specialiste {
    private int id;
    private String nom;
    private String prenom;
    private String specialisation;
    private String telephone;
    private String email;

    /**
     * Constructeur complet
     * @param id Identifiant unique
     * @param nom Nom de famille
     * @param prenom Prénom
     * @param specialisation Spécialité médicale
     * @param telephone Numéro de téléphone
     * @param email Email (unique)
     */
    public Specialiste(int id, String nom, String prenom,
                       String specialisation, String telephone, String email) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.specialisation = specialisation;
        this.telephone = telephone;
        this.email = email;
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

    public String getSpecialisation() {
        return specialisation;
    }

    public void setSpecialisation(String specialisation) {
        this.specialisation = specialisation;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}