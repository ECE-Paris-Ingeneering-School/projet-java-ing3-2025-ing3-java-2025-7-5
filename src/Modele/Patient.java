package Modele;

import java.time.LocalDate;

/**
 * Classe représentant un patient, héritant d'Utilisateur
 * @author [Votre nom]
 */
public class Patient extends Utilisateur {
    private LocalDate dateNaissance;
    private String adresse;
    private String telephone;

    /**
     * Constructeur complet
     * @param id Identifiant (hérité d'Utilisateur)
     * @param nom Nom (hérité)
     * @param prenom Prénom (hérité)
     * @param email Email (hérité)
     * @param motDePasse Mot de passe (hérité)
     * @param dateNaissance Date de naissance
     * @param adresse Adresse postale
     * @param telephone Numéro de téléphone
     */
    public Patient(int id, String nom, String prenom, String email,
                   String motDePasse, LocalDate dateNaissance,
                   String adresse, String telephone) {
        super(id, nom, prenom, email, motDePasse, "patient");
        this.dateNaissance = dateNaissance;
        this.adresse = adresse;
        this.telephone = telephone;
    }

    // Getters et Setters spécifiques à Patient
    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}