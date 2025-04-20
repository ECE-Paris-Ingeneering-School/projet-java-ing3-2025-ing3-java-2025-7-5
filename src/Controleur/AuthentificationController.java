package Controleur;

import DAO.PatientDAO;
import DAO.SpecialisteDAO;
import DAO.UtilisateurDAO;
import Modele.Patient;
import Modele.Utilisateur;
import Vue.AccueilView;
import Vue.ConnexionView;
import Vue.InscriptionView;
import Vue.ProfilPatientView;

import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class AuthentificationController {
    private final UtilisateurDAO utilisateurDAO;
    private final PatientDAO patientDAO;
    private final SpecialisteDAO specialisteDAO;
    private final AccueilView accueilView;
    private ConnexionView connexionView;
    private InscriptionView inscriptionView;

    public AuthentificationController(UtilisateurDAO utilisateurDAO,
                                      PatientDAO patientDAO,
                                      SpecialisteDAO specialisteDAO,
                                      AccueilView accueilView) {
        this.utilisateurDAO = utilisateurDAO;
        this.patientDAO     = patientDAO;
        this.specialisteDAO = specialisteDAO;
        this.accueilView    = accueilView;

        initListeners();
    }

    private void initListeners() {
        accueilView.setConnexionListener(e -> showConnexionView());
        accueilView.setInscriptionListener(e -> showInscriptionView());
    }

    private void showConnexionView() {
        connexionView = new ConnexionView();
        connexionView.setConnexionListener(e -> handleConnexion());
        connexionView.setVisible(true);
    }

    private void showInscriptionView() {
        inscriptionView = new InscriptionView();
        inscriptionView.setInscriptionListener(e -> handleInscription());
        inscriptionView.setVisible(true);
    }

    private void handleConnexion() {
        String email    = connexionView.getEmail().trim();
        String password = connexionView.getPassword();

        if (email.isEmpty() || password.isEmpty()) {
            connexionView.showError("Veuillez remplir tous les champs");
            return;
        }

        try {
            Utilisateur utilisateur = utilisateurDAO.findByEmail(email);
            if (utilisateur != null && utilisateur.verifierMotDePasse(password)) {
                Patient patient = patientDAO.findById(utilisateur.getId());
                openProfilPatient(patient);
                connexionView.dispose();
                accueilView.setVisible(false);
            } else {
                connexionView.showError("Email ou mot de passe incorrect");
            }
        } catch (Exception e) {
            connexionView.showError("Erreur de connexion: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleInscription() {
        try {
            if (!validateInscriptionFields()) {
                return;
            }

            if (utilisateurDAO.findByEmail(inscriptionView.getEmail()) != null) {
                inscriptionView.showError("Cet email est déjà utilisé");
                return;
            }

            Patient patient = createPatientFromForm();
            patientDAO.create(patient);

            openProfilPatient(patient);
            inscriptionView.dispose();
            accueilView.setVisible(false);

        } catch (DateTimeParseException e) {
            inscriptionView.showError("Format de date invalide (AAAA-MM-JJ requis)");
        } catch (Exception e) {
            inscriptionView.showError("Erreur d'inscription: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateInscriptionFields() {
        if (inscriptionView.getNom().isEmpty() ||
                inscriptionView.getPrenom().isEmpty() ||
                inscriptionView.getEmail().isEmpty() ||
                inscriptionView.getPassword().isEmpty() ||
                inscriptionView.getDateNaissance().isEmpty()) {

            inscriptionView.showError("Tous les champs obligatoires doivent être remplis");
            return false;
        }

        if (!inscriptionView.getPassword().equals(inscriptionView.getConfirmationPassword())) {
            inscriptionView.showError("Les mots de passe ne correspondent pas");
            return false;
        }

        return true;
    }

    private Patient createPatientFromForm() throws DateTimeParseException {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return new Patient(
                0,
                inscriptionView.getNom(),
                inscriptionView.getPrenom(),
                inscriptionView.getEmail(),
                inscriptionView.getPassword(),
                LocalDate.parse(inscriptionView.getDateNaissance(), fmt),
                inscriptionView.getAdresse(),
                inscriptionView.getTelephone()
        );
    }

    private void openProfilPatient(Patient patient) {
        ProfilPatientView profilView = new ProfilPatientView(patient);
        // Maintenant on passe bien les 4 paramètres requis
        new ProfilPatientController(
                patient,
                profilView,
                patientDAO,
                specialisteDAO
        );
        profilView.setVisible(true);
    }
}
