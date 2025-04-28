package Modele;

public class Specialiste extends Utilisateur {
    private String specialisation;
    private String telephone;

    public Specialiste(int id, String nom, String prenom, String email, String motDePasse, String specialisation, String telephone) {
        super(id, nom, prenom, email, motDePasse, "specialiste");
        this.specialisation = specialisation;
        this.telephone = telephone;
    }

    public String getSpecialisation() { return specialisation; }
    public String getTelephone() { return telephone; }

    public void setSpecialisation(String specialisation) { this.specialisation = specialisation; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
}
