package Modele;

public class Historique {
    private int id;
    private int patientId;
    private int rendezVousId;
    private String notes;
    private String dateHeure;
    private String specialisteNom;
    private String specialistePrenom;
    private String specialisation;
    private String patientNom;
    private String patientPrenom;
    private String statut;


    public Historique(int id, int patientId, int rendezVousId, String notes) {
        this.id = id;
        this.patientId = patientId;
        this.rendezVousId = rendezVousId;
        this.notes = notes;
    }

    public Historique(int id, int patientId, int rendezVousId, String notes, String dateHeure, String specialisteNom, String specialistePrenom, String specialisation, String patientNom, String statut, String patientPrenom) {

        this.id = id;
        this.patientId = patientId;
        this.rendezVousId = rendezVousId;
        this.notes = notes;
        this.dateHeure = dateHeure;
        this.specialisteNom = specialisteNom;
        this.specialistePrenom = specialistePrenom;
        this.specialisation = specialisation;
        this.patientNom = patientNom;
        this.patientPrenom = patientPrenom;
        this.statut = statut;

    }

    public int getId() { return id; }
    public String getPatientNom() { return patientNom; }
    public String getPatientPrenom() { return patientPrenom; }
    public void setStatut(String statut) { this.statut = statut; }
    public String getStatut() { return statut; }

    public int getPatientId() { return patientId; }
    public int getRendezVousId() { return rendezVousId; }
    public String getNotes() { return notes; }
    public String getDateHeure() { return dateHeure; }
    public String getSpecialisteNom() { return specialisteNom; }
    public String getSpecialistePrenom() { return specialistePrenom; }
    public String getSpecialisation() { return specialisation; }
    public void setId(int id) { this.id = id; }
    public void setPatientId(int patientId) { this.patientId = patientId; }
    public void setRendezVousId(int rendezVousId) { this.rendezVousId = rendezVousId; }
    public void setDateHeure(String dateHeure) { this.dateHeure = dateHeure; }
    public void setSpecialisteNom(String specialisteNom) { this.specialisteNom = specialisteNom; }
    public void setSpecialistePrenom(String specialistePrenom) { this.specialistePrenom = specialistePrenom; }
    public void setSpecialisation(String specialisation) { this.specialisation = specialisation; }


    public void setNotes(String notes) { this.notes = notes; }
}
