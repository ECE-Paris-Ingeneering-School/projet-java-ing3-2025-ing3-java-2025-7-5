package Modele;

public class Patient extends Utilisateur {
    private String dateNaissance;
    private String adresse;
    private String telephone;

    public Patient(int id, String nom, String prenom, String email, String motDePasse, String dateNaissance, String adresse, String telephone) {
        super(id, nom, prenom, email, motDePasse, "patient");
        this.dateNaissance = dateNaissance;
        this.adresse = adresse;
        this.telephone = telephone;
    }

    public String getDateNaissance() { return dateNaissance; }
    public String getAdresse() { return adresse; }
    public String getTelephone() { return telephone; }

    public void setDateNaissance(String dateNaissance) { this.dateNaissance = dateNaissance; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
}
