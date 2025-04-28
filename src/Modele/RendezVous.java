package Modele;

public class RendezVous {
    private int id;
    private String specialisteNom;
    private String specialistePrenom;
    private String specialisation;
    private String dateHeure;
    private String statut;

    public RendezVous(int id, String specialisteNom, String specialistePrenom, String specialisation, String dateHeure, String statut) {
        this.id = id;
        this.specialisteNom = specialisteNom;
        this.specialistePrenom = specialistePrenom;
        this.specialisation = specialisation;
        this.dateHeure = dateHeure;
        this.statut = statut;
    }

    public int getId() { return id; }
    public String getSpecialisteNom() { return specialisteNom; }
    public String getSpecialistePrenom() { return specialistePrenom; }
    public String getSpecialisation() { return specialisation; }
    public String getDateHeure() { return dateHeure; }
    public String getStatut() { return statut; }

    public void setStatut(String statut) { this.statut = statut; }
}
