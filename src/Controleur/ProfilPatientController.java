package Controleur;

import DAO.PatientDAO;
import Modele.Patient;
import Vue.ProfilPatientView;
import Vue.ModifierProfilView;
import Vue.PrendreRendezVousView;

import javax.swing.*;
import java.awt.event.ActionListener; // Import manquant


public class ProfilPatientController {
    private final Patient patient;
    private final ProfilPatientView view;
    private final PatientDAO patientDAO;

    public ProfilPatientController(Patient patient, ProfilPatientView view, PatientDAO patientDAO) {
        this.patient = patient;
        this.view = view;
        this.patientDAO = patientDAO;

        initControllers();
        setupListeners();
    }

    private void initControllers() {
        // Initialisation des sous-contrôleurs
    }

    private void setupListeners() {
        // Utilisation de ActionListener directement
        view.getBtnPrendreRdv().addActionListener(e -> handlePrendreRdv());
        view.getBtnModifierProfil().addActionListener(e -> handleModifierProfil());
    }

    private void handlePrendreRdv() {
        view.setVisible(false);
        PrendreRendezVousView rdvView = new PrendreRendezVousView(patient);
        new PrendreRendezVousController(patient, rdvView, patientDAO);
        rdvView.setVisible(true);
    }

    private void handleModifierProfil() {
        ModifierProfilView editView = new ModifierProfilView(patient);
        new ModifierProfilController(patient, editView, patientDAO, this::refreshProfileView);
        editView.setVisible(true);
    }

    private void refreshProfileView() {
        try {
            Patient updatedPatient = patientDAO.findById(patient.getId());
            view.dispose();
            ProfilPatientView newProfilView = new ProfilPatientView(updatedPatient);
            new ProfilPatientController(updatedPatient, newProfilView, patientDAO);
            newProfilView.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view,
                    "Erreur lors du rafraîchissement: " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Classe interne modifiée pour PrendreRendezVousController
    private static class PrendreRendezVousController {
        public PrendreRendezVousController(Patient patient, PrendreRendezVousView view, PatientDAO patientDAO) {
            view.setRetourListener(e -> {
                view.dispose();
                ProfilPatientView profilView = new ProfilPatientView(patient);
                new ProfilPatientController(patient, profilView, patientDAO);
                profilView.setVisible(true);
            });
        }
    }

    // Classe interne modifiée pour ModifierProfilController
    private static class ModifierProfilController {
        public ModifierProfilController(Patient patient, ModifierProfilView view, PatientDAO patientDAO, Runnable onSuccess) {
            view.setValiderListener(e -> updatePatient(patient, view, patientDAO, onSuccess));
            view.setAnnulerListener(e -> view.dispose());
        }

        private void updatePatient(Patient patient, ModifierProfilView view, PatientDAO patientDAO, Runnable onSuccess) {
            try {
                patient.setNom(view.getNom());
                patient.setPrenom(view.getPrenom());
                patient.setEmail(view.getEmail());
                patient.setTelephone(view.getTelephone());
                patient.setAdresse(view.getAdresse());

                patientDAO.update(patient);
                onSuccess.run();
            } catch (Exception ex) {
                view.showError("Erreur de mise à jour: " + ex.getMessage());
            }
        }
    }
}