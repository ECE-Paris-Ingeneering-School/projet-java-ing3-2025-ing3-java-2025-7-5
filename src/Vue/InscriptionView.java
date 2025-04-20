package Vue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class InscriptionView extends JDialog {
    // Champs de formulaire
    private JTextField nomField;
    private JTextField prenomField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmationPasswordField;
    private JTextField dateNaissanceField;
    private JTextField adresseField;
    private JTextField telephoneField;

    // Boutons
    private JButton btnInscription;
    private JButton btnAnnuler;

    public InscriptionView() {
        configureFenetre();
        initUI();
    }

    private void configureFenetre() {
        setTitle("Inscription Patient");
        setModal(true);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout(10, 10));
    }

    private void initUI() {
        // Panel principal avec bordure
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Titre
        JLabel titreLabel = new JLabel("Création de compte patient", SwingConstants.CENTER);
        titreLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titreLabel, BorderLayout.NORTH);

        // Panel de formulaire avec GridBagLayout pour un alignement précis
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Informations personnelles"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Ligne 0: Nom
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(createLabel("Nom*:"), gbc);

        gbc.gridx = 1;
        nomField = new JTextField(20);
        formPanel.add(nomField, gbc);

        // Ligne 1: Prénom
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(createLabel("Prénom*:"), gbc);

        gbc.gridx = 1;
        prenomField = new JTextField(20);
        formPanel.add(prenomField, gbc);

        // Ligne 2: Email
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(createLabel("Email*:"), gbc);

        gbc.gridx = 1;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);

        // Ligne 3: Mot de passe
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(createLabel("Mot de passe*:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);

        // Ligne 4: Confirmation mot de passe
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(createLabel("Confirmation*:"), gbc);

        gbc.gridx = 1;
        confirmationPasswordField = new JPasswordField(20);

        // Gestion de la touche Entrée
        confirmationPasswordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnInscription.doClick();
                }
            }
        });
        formPanel.add(confirmationPasswordField, gbc);

        // Ligne 5: Date de naissance
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(createLabel("Date naissance* (AAAA-MM-JJ):"), gbc);

        gbc.gridx = 1;
        dateNaissanceField = new JTextField(20);
        formPanel.add(dateNaissanceField, gbc);

        // Ligne 6: Adresse
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(createLabel("Adresse:"), gbc);

        gbc.gridx = 1;
        adresseField = new JTextField(20);
        formPanel.add(adresseField, gbc);

        // Ligne 7: Téléphone
        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(createLabel("Téléphone:"), gbc);

        gbc.gridx = 1;
        telephoneField = new JTextField(20);
        formPanel.add(telephoneField, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        btnAnnuler = new JButton("Annuler");
        btnAnnuler.addActionListener(e -> dispose());
        buttonPanel.add(btnAnnuler);

        btnInscription = new JButton("S'inscrire");
        btnInscription.setPreferredSize(new Dimension(120, 30));
        buttonPanel.add(btnInscription);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Look and Feel moderne
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        return label;
    }

    // Getters pour les valeurs des champs
    public String getNom() { return nomField.getText().trim(); }
    public String getPrenom() { return prenomField.getText().trim(); }
    public String getEmail() { return emailField.getText().trim(); }
    public String getPassword() { return new String(passwordField.getPassword()); }
    public String getConfirmationPassword() { return new String(confirmationPasswordField.getPassword()); }
    public String getDateNaissance() { return dateNaissanceField.getText().trim(); }
    public String getAdresse() { return adresseField.getText().trim(); }
    public String getTelephone() { return telephoneField.getText().trim(); }

    // Méthodes pour les listeners
    public void setInscriptionListener(ActionListener listener) {
        btnInscription.addActionListener(listener);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    public void clearFields() {
        nomField.setText("");
        prenomField.setText("");
        emailField.setText("");
        passwordField.setText("");
        confirmationPasswordField.setText("");
        dateNaissanceField.setText("");
        adresseField.setText("");
        telephoneField.setText("");
    }
}