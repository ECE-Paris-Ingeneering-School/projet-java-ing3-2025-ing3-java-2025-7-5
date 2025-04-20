package Vue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class AccueilView extends JFrame {
    private JButton btnConnexion;
    private JButton btnInscription;

    public AccueilView() {
        // Configuration de la fenêtre principale
        setTitle("DoctoLib - Accueil");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);

        // Utilisation d'un JPanel avec BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Logo en haut (NORTH)
        JLabel logoLabel = new JLabel(new ImageIcon("logo.png"), SwingConstants.CENTER);
        mainPanel.add(logoLabel, BorderLayout.NORTH);

        // Panel central avec GridBagLayout pour plus de contrôle
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Bouton Connexion
        btnConnexion = new JButton("Se connecter");
        btnConnexion.setPreferredSize(new Dimension(200, 50));
        btnConnexion.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(btnConnexion, gbc);

        // Bouton Inscription
        btnInscription = new JButton("S'inscrire");
        btnInscription.setPreferredSize(new Dimension(200, 50));
        btnInscription.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridy = 1;
        centerPanel.add(btnInscription, gbc);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Ajout du panel principal à la fenêtre
        add(mainPanel);

        // Look and Feel moderne
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Méthodes pour les listeners (inchangées)
    public void setConnexionListener(ActionListener listener) {
        btnConnexion.addActionListener(listener);
    }

    public void setInscriptionListener(ActionListener listener) {
        btnInscription.addActionListener(listener);
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}