// Controleur/RendezVousController.java
package Controleur;

import DAO.SpecialisteDAO;
import DAO.RendezVousDAO;
import Modele.Patient;
import Modele.Specialiste;
import Vue.PrendreRendezVousView;
import Vue.DisponibilitesView;

import javax.swing.*;
import java.util.List;

public class RendezVousController {
    private final Patient patient;
    private final PrendreRendezVousView view;
    private final SpecialisteDAO specialisteDAO;
    private final RendezVousDAO rendezVousDAO;
    private final Runnable onReturn;

    public RendezVousController(Patient patient,
                                PrendreRendezVousView view,
                                SpecialisteDAO specialisteDAO,
                                RendezVousDAO rendezVousDAO,
                                Runnable onReturn) {
        this.patient         = patient;
        this.view            = view;
        this.specialisteDAO  = specialisteDAO;
        this.rendezVousDAO   = rendezVousDAO;
        this.onReturn        = onReturn;

        view.setRetourListener(e -> onReturn.run());
        view.setRechercherListener(e -> performSearch());
    }

    private void performSearch() {
        String text = view.getSearchText();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Veuillez entrer un terme de recherche");
            return;
        }

        try {
            List<Specialiste> results = switch (view.getSearchType()) {
                case "Nom du spécialiste" -> specialisteDAO.findByNom(text);
                default                    -> specialisteDAO.findBySpecialite(text);
            };
            // lancement de la vue disponibilités
            view.displayResults(results, this::openDisponibilites);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view,
                    "Erreur lors de la recherche: " + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            view.displayResults(List.of());
        }
    }

    private void openDisponibilites(Specialiste spec) {
        view.setVisible(false);
        DisponibilitesView dispoView = new DisponibilitesView(spec);
        new DisponibilitesController(
                patient,
                spec,
                dispoView,
                rendezVousDAO,
                () -> {
                    dispoView.dispose();
                    onReturn.run();   // retourne au PrendreRendezVousView
                }
        );
        dispoView.setVisible(true);
    }
}
