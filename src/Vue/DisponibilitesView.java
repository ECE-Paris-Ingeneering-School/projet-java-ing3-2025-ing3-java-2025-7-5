// Vue/DisponibilitesView.java
package Vue;

import Modele.Specialiste;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DisponibilitesView extends JFrame {
    private final Specialiste specialiste;
    private JButton btnConfirmer, btnRetour;
    private JPanel calendarPanel;
    private LocalDate selectedDate;
    private LocalTime selectedTime;

    // Ensemble des créneaux déjà pris
    private Set<LocalDateTime> bookedSlots = new HashSet<>();

    public DisponibilitesView(Specialiste specialiste) {
        this.specialiste = specialiste;
        setTitle("Disponibilités - Dr " + specialiste.getNom());
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        JPanel main = new JPanel(new BorderLayout(10,10));
        main.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.add(new JLabel(
                "Disponibilités du Dr " +
                        specialiste.getNom() +
                        " (" + specialiste.getSpecialisation() + ")"
        ), BorderLayout.WEST);
        btnRetour = new JButton("Retour");
        header.add(btnRetour, BorderLayout.EAST);
        main.add(header, BorderLayout.NORTH);

        // Calendar
        calendarPanel = new JPanel(new GridLayout(0,7,5,5));
        main.add(new JScrollPane(calendarPanel), BorderLayout.CENTER);

        // Footer
        btnConfirmer = new JButton("Confirmer le rendez-vous");
        btnConfirmer.setEnabled(false);
        JPanel footer = new JPanel();
        footer.add(btnConfirmer);
        main.add(footer, BorderLayout.SOUTH);

        setContentPane(main);

        // On attend que bookedSlots soit rempli avant d'afficher
        updateCalendar(LocalDate.now());
    }

    /**
     * Injecte la liste de créneaux déjà pris puis rafraîchit l'affichage.
     */
    public void setBookedSlots(List<LocalDateTime> slots) {
        bookedSlots = new HashSet<>(slots);
        updateCalendar(LocalDate.now());
    }

    private void updateCalendar(LocalDate start) {
        calendarPanel.removeAll();
        String[] jours = {"Lundi","Mardi","Mercredi","Jeudi","Vendredi","Samedi","Dimanche"};
        for (String j : jours) {
            calendarPanel.add(new JLabel(j, JLabel.CENTER));
        }

        LocalDate date = start;
        for (int i=0; i<35; i++) {
            if (date.getDayOfWeek().getValue() <= 5) {
                addDaySlots(date);
            }
            date = date.plusDays(1);
        }

        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    private void addDaySlots(LocalDate date) {
        JPanel day = new JPanel(new GridLayout(0,1));
        day.setBorder(BorderFactory.createTitledBorder(date.toString()));

        LocalTime[] slots = {
                LocalTime.of(9,0), LocalTime.of(10,30),
                LocalTime.of(14,0),LocalTime.of(15,30)
        };

        for (LocalTime slot : slots) {
            LocalDateTime dt = LocalDateTime.of(date, slot);
            JButton btn = new JButton(slot.toString());

            if (bookedSlots.contains(dt)) {
                // déjà pris → rouge et désactivé
                btn.setEnabled(false);
                btn.setBackground(Color.RED);
            } else {
                btn.setEnabled(true);
                btn.setBackground(Color.GREEN);
                btn.addActionListener(e -> {
                    selectedDate = date;
                    selectedTime = slot;
                    btnConfirmer.setEnabled(true);
                    resetSlotButtons();
                    btn.setBackground(Color.YELLOW);
                });
            }
            day.add(btn);
        }

        calendarPanel.add(day);
    }

    private void resetSlotButtons() {
        for (Component c1 : calendarPanel.getComponents()) {
            if (!(c1 instanceof JPanel)) continue;
            for (Component c2 : ((JPanel)c1).getComponents()) {
                if (c2 instanceof JButton b && b.isEnabled()) {
                    b.setBackground(Color.GREEN);
                }
            }
        }
    }

    // Getters pour le contrôleur
    public LocalDate getSelectedDate()  { return selectedDate; }
    public LocalTime getSelectedTime()  { return selectedTime; }

    // Listeners exposés
    public void setConfirmerListener(ActionListener l) { btnConfirmer.addActionListener(l); }
    public void setRetourListener(ActionListener l)    { btnRetour.addActionListener(l); }
}
