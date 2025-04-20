package Modele;

import java.time.LocalDateTime;

/**
 * Classe représentant un rendez-vous entre un patient et un spécialiste
 * @author [Votre nom]
 */
public class RendezVous {
    private int id;
    private Patient patient;
    private Specialiste specialiste;
    private  LocalDateTime dateHeure;
    private String statut; // "confirmé", "annulé", "terminé"

    /**
     * Constructeur complet
     * @param id Identifiant unique
     * @param patient Patient concerné
     * @param specialiste Spécialiste concerné
     * @param dateHeure Date et heure du rendez-vous
     * @param statut Statut actuel
     */
    public RendezVous(int id, Patient patient, Specialiste specialiste,
                      LocalDateTime dateHeure, String statut) {
        this.id = id;
        this.patient = patient;
        this.specialiste = specialiste;
        this.dateHeure = dateHeure;
        this.statut = statut;
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

    public Specialiste getSpecialiste() {
        return specialiste;
    }

    public void setSpecialiste(Specialiste specialiste) {
        this.specialiste = specialiste;
    }

    public  LocalDateTime getDateHeure() {
        return dateHeure;
    }

    public void setDateHeure( LocalDateTime dateHeure) {
        this.dateHeure = dateHeure;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    /**
     * Annule le rendez-vous
     */
    public void annuler() {
        this.statut = "annulé";
    }

    /**
     * Confirme le rendez-vous
     */
    public void confirmer() {
        this.statut = "confirmé";
    }

    /**
     * Marque le rendez-vous comme terminé
     */
    public void terminer() {
        this.statut = "terminé";
    }
}