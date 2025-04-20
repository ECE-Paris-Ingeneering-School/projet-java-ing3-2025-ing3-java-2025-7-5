package Controleur;
import Vue.ModifierProfilView;
import DAO.DAOException;
import DAO.PatientDAO;
import DAO.SpecialisteDAO;
import DAO.RendezVousDAO;
import DAO.RendezVousDAOImpl;
import Modele.Patient;
import Modele.RendezVous;
import Vue.HistoriqueView;
import Vue.PrendreRendezVousView;
import Vue.ProfilPatientView;

import javax.swing.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ProfilPatientController {
    private final Patient patient;
    private final ProfilPatientView view;
    private final PatientDAO patientDAO;
    private final SpecialisteDAO specialisteDAO;
    private final RendezVousDAO rendezVousDAO;

    public ProfilPatientController(Patient patient,
                                   ProfilPatientView view,
                                   PatientDAO patientDAO,
                                   SpecialisteDAO specialisteDAO) {
        this.patient        = patient;
        this.view           = view;
        this.patientDAO     = patientDAO;
        this.specialisteDAO = specialisteDAO;
        this.rendezVousDAO  = new RendezVousDAOImpl();

        view.setModifierProfilListener(e -> openModifierProfil());
        view.setQuickActionPrendreRdvListener(e -> openRendezVousView());
        view.setHistoriqueListener(e -> openHistoriqueView());
        view.setAnnulerRdvListener(e -> cancelSelectedAppointment());

        refreshAll();
    }

    private void refreshAll() {
        loadConfirmedAppointments();
        loadNextAppointment();
    }

    private void loadConfirmedAppointments() {
        try {
            List<RendezVous> all = rendezVousDAO.findByPatient(patient.getId());
            List<RendezVous> confirmed = all.stream()
                    .filter(r -> "confirmé".equalsIgnoreCase(r.getStatut()))
                    .collect(Collectors.toList());
            view.setRendezVousData(confirmed);
        } catch (DAOException e) {
            JOptionPane.showMessageDialog(view,
                    "Erreur chargement des rendez-vous : " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadNextAppointment() {
        try {
            List<RendezVous> all = rendezVousDAO.findByPatient(patient.getId());
            RendezVous next = all.stream()
                    .filter(r -> "confirmé".equalsIgnoreCase(r.getStatut())
                            && r.getDateHeure().isAfter(LocalDateTime.now()))
                    .min(Comparator.comparing(RendezVous::getDateHeure))
                    .orElse(null);
            view.setNextAppointment(next);
        } catch (DAOException e) {
            // silent if error
        }
    }

    private void openModifierProfil() {
        ModifierProfilView modifierView = new ModifierProfilView(patient);

        modifierView.setValiderListener(e -> {
            // Récupère les nouvelles valeurs
            String nom = modifierView.getNom().trim();
            String prenom = modifierView.getPrenom().trim();
            String email = modifierView.getEmail().trim();
            String telephone = modifierView.getTelephone().trim();
            String adresse = modifierView.getAdresse().trim();

            if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty()) {
                modifierView.showError("Nom, prénom et email sont obligatoires.");
                return;
            }

            // Met à jour le patient
            patient.setNom(nom);
            patient.setPrenom(prenom);
            patient.setEmail(email);
            patient.setTelephone(telephone);
            patient.setAdresse(adresse);

            try {
                patientDAO.update(patient);
                modifierView.dispose();
                refreshAll(); // recharge les données
            } catch (DAOException ex) {
                modifierView.showError("Erreur lors de la mise à jour : " + ex.getMessage());
            }
        });

        modifierView.setAnnulerListener(e -> modifierView.dispose());

        modifierView.setVisible(true);
    }

    private void openRendezVousView() {
        view.setVisible(false);
        PrendreRendezVousView rdvView = new PrendreRendezVousView(patient);
        new RendezVousController(
                patient,
                rdvView,
                specialisteDAO,
                rendezVousDAO,
                () -> {
                    rdvView.dispose();
                    view.setVisible(true);
                    refreshAll();
                }
        );
        rdvView.setVisible(true);
    }

    private void openHistoriqueView() {
        try {
            List<RendezVous> historique = rendezVousDAO.findByPatient(patient.getId())
                    .stream()
                    .filter(r -> {
                        String s = r.getStatut().toLowerCase();
                        return s.equals("terminé") || s.equals("annulé");
                    })
                    .collect(Collectors.toList());
            HistoriqueView hv = new HistoriqueView(historique);
            hv.setVisible(true);
        } catch (DAOException e) {
            JOptionPane.showMessageDialog(view,
                    "Erreur chargement historique : " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelSelectedAppointment() {
        try {
            // on ne prend que les confirmés pour l'annulation
            List<RendezVous> confirmed = rendezVousDAO.findByPatient(patient.getId())
                    .stream()
                    .filter(r -> "confirmé".equalsIgnoreCase(r.getStatut()))
                    .collect(Collectors.toList());

            int idx = view.getSelectedAppointmentIndex();
            if (idx < 0 || idx >= confirmed.size()) {
                JOptionPane.showMessageDialog(view,
                        "Sélectionnez un rendez-vous à annuler.", "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            RendezVous rdv = confirmed.get(idx);
            Duration diff = Duration.between(LocalDateTime.now(), rdv.getDateHeure());
            if (diff.toHours() < 48) {
                JOptionPane.showMessageDialog(view,
                        "Annulation impossible moins de 48 h avant le rendez-vous.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            rdv.setStatut("annulé");
            rendezVousDAO.update(rdv);

            JOptionPane.showMessageDialog(view,
                    "Rendez-vous annulé avec succès.", "Succès",
                    JOptionPane.INFORMATION_MESSAGE);

            refreshAll();  // le RDV n'apparait plus dans "Mes rendez-vous"
        } catch (DAOException e) {
            JOptionPane.showMessageDialog(view,
                    "Erreur lors de l'annulation : " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
