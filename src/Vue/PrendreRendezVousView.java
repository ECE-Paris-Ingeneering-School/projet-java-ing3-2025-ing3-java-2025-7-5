package Vue;

import Modele.Patient;           // ← Ajout de cet import
import Modele.Specialiste;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.function.Consumer;

public class PrendreRendezVousView extends JFrame {
    private final JTextField searchField;
    private final JComboBox<String> searchTypeCombo;
    private final JButton btnRechercher;
    private final JButton btnRetour;
    private final JPanel resultsPanel;

    public PrendreRendezVousView(Patient patient) {
        setTitle("Recherche de spécialistes");
        setSize(1000, 700);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout(10,10));
        main.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        // --- Recherche ---
        JPanel top = new JPanel(new BorderLayout(10,10));
        searchTypeCombo = new JComboBox<>(new String[]{"Nom du spécialiste", "Spécialité"});

        // Barre de recherche allongée
        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(600, 30));

        btnRechercher = new JButton("Rechercher");

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        controls.add(searchTypeCombo);
        controls.add(searchField);
        controls.add(btnRechercher);
        top.add(controls, BorderLayout.CENTER);

        btnRetour = new JButton("← Retour au profil");
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        backPanel.add(btnRetour);
        top.add(backPanel, BorderLayout.SOUTH);

        main.add(top, BorderLayout.NORTH);

        // --- Résultats ---
        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        JScrollPane scroll = new JScrollPane(resultsPanel);
        main.add(scroll, BorderLayout.CENTER);

        setContentPane(main);
    }

    public void setRechercherListener(ActionListener l) {
        btnRechercher.addActionListener(l);
    }

    public void setRetourListener(ActionListener l) {
        btnRetour.addActionListener(l);
    }

    public String getSearchText() {
        return searchField.getText().trim();
    }

    public String getSearchType() {
        return (String) searchTypeCombo.getSelectedItem();
    }

    public void displayResults(List<Specialiste> specialists) {
        displayResults(specialists, null);
    }

    public void displayResults(List<Specialiste> specialists,
                               Consumer<Specialiste> prendreRdvCallback) {
        resultsPanel.removeAll();

        if (specialists.isEmpty()) {
            resultsPanel.add(new JLabel("Aucun résultat trouvé", SwingConstants.CENTER));
        } else {
            for (Specialiste spec : specialists) {
                JPanel card = new JPanel(new BorderLayout(10,10));
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                        BorderFactory.createEmptyBorder(10,10,10,10)
                ));

                JLabel name = new JLabel(spec.getNom() + " " + spec.getPrenom());
                name.setFont(name.getFont().deriveFont(Font.BOLD, 16f));
                JLabel sp = new JLabel(spec.getSpecialisation());
                sp.setForeground(new Color(0,120,215));
                JPanel header = new JPanel(new BorderLayout());
                header.add(name, BorderLayout.WEST);
                header.add(sp, BorderLayout.EAST);

                JPanel details = new JPanel(new GridLayout(0,1,5,5));
                details.add(new JLabel("📞 " + spec.getTelephone()));
                details.add(new JLabel("✉️ " + spec.getEmail()));

                JButton btnPrendre = new JButton("Prendre rendez-vous");
                btnPrendre.addActionListener(e -> {
                    if (prendreRdvCallback != null) {
                        prendreRdvCallback.accept(spec);
                    }
                });

                card.add(header, BorderLayout.NORTH);
                card.add(details, BorderLayout.CENTER);
                card.add(btnPrendre, BorderLayout.SOUTH);

                resultsPanel.add(card);
                resultsPanel.add(Box.createRigidArea(new Dimension(0,10)));
            }
        }

        resultsPanel.revalidate();
        resultsPanel.repaint();
    }
}
