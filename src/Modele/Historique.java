package Modele;

/**
 * Classe représentant l'historique des rendez-vous d'un patient
 * @author [Votre nom]
 */
public class Historique {
    private int id;
    private Patient patient;
    private RendezVous rendezVous;
    private String notes;

    /**
     * Constructeur complet
     * @param id Identifiant unique
     * @param patient Patient concerné
     * @param rendezVous Rendez-vous concerné
     * @param notes Notes sur le rendez-vous
     */
    public Historique(int id, Patient patient, RendezVous rendezVous, String notes) {
        this.id = id;
        this.patient = patient;
        this.rendezVous = rendezVous;
        this.notes = notes;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public RendezVous getRendezVous() {
        return rendezVous;
    }

    public void setRendezVous(RendezVous rendezVous) {
        this.rendezVous = rendezVous;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}