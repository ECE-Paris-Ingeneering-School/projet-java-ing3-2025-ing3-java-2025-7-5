// Controleur/DisponibilitesController.java
package Controleur;

import DAO.DAOException;
import DAO.RendezVousDAO;
import Modele.Patient;
import Modele.RendezVous;
import Modele.Specialiste;
import Vue.DisponibilitesView;

import javax.swing.*;
import java.time.LocalDateTime;

public class DisponibilitesController {
    private final Patient patient;
    private final Specialiste specialiste;
    private final DisponibilitesView view;
    private final RendezVousDAO rendezVousDAO;
    private final Runnable onReturn;

    public DisponibilitesController(Patient patient,
                                    Specialiste specialiste,
                                    DisponibilitesView view,
                                    RendezVousDAO rendezVousDAO,
                                    Runnable onReturn) {
        this.patient        = patient;
        this.specialiste    = specialiste;
        this.view           = view;
        this.rendezVousDAO  = rendezVousDAO;
        this.onReturn       = onReturn;

        // 1) Charger les créneaux déjà réservés pour ce spécialiste
        loadBookedSlots();

        // 2) Lier les boutons
        view.setRetourListener(e -> onReturn.run());
        view.setConfirmerListener(e -> confirmRendezVous());
    }

    private void loadBookedSlots() {
        try {
            // on récupère tous les RDV confirmés pour ce spécialiste
            var all = rendezVousDAO.findBySpecialiste(specialiste.getId());
            var booked = all.stream()
                    .filter(r -> r.getStatut().equalsIgnoreCase("confirmé"))
                    .map(RendezVous::getDateHeure)
                    .toList();

            view.setBookedSlots(booked);
        } catch (DAOException e) {
            JOptionPane.showMessageDialog(view,
                    "Erreur chargement des créneaux : " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void confirmRendezVous() {
        var date  = view.getSelectedDate();
        var time  = view.getSelectedTime();
        if (date == null || time == null) {
            JOptionPane.showMessageDialog(view,
                    "Veuillez sélectionner un créneau.",
                    "Attention", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDateTime dateTime = LocalDateTime.of(date, time);
        RendezVous rdv = new RendezVous(0, patient, specialiste, dateTime, "confirmé");

        try {
            // 1) on écrit en base
            rendezVousDAO.create(rdv);

            // 2) message de succès
            JOptionPane.showMessageDialog(view,
                    "Rendez‑vous confirmé pour le " + dateTime,
                    "Succès", JOptionPane.INFORMATION_MESSAGE);

            // 3) rappel du callback pour fermer cette vue ET retourner AU PROFIL
            onReturn.run();

        } catch (DAOException ex) {
            JOptionPane.showMessageDialog(view,
                    "Erreur lors de la réservation : " + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
