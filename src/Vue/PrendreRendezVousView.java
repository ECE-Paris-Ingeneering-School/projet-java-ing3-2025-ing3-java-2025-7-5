package Vue;

import Modele.Patient;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener; // Import manquant

public class PrendreRendezVousView extends JFrame {
    private JButton btnRetour, btnConfirmer;

    public PrendreRendezVousView(Patient patient) {
        setTitle("Prendre un rendez-vous");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI(patient);
    }

    private void initUI(Patient patient) {
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnRetour = new JButton("← Retour au profil");
        headerPanel.add(btnRetour);

        // Contenu principal
        JPanel contentPanel = new JPanel();
        contentPanel.add(new JLabel("Interface de prise de rendez-vous", JLabel.CENTER));

        // Footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnConfirmer = new JButton("Confirmer le rendez-vous");
        footerPanel.add(btnConfirmer);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    // Méthodes pour les listeners
    public void setRetourListener(ActionListener listener) {
        btnRetour.addActionListener(listener);
    }

    public void setConfirmerListener(ActionListener listener) {
        btnConfirmer.addActionListener(listener);
    }
}