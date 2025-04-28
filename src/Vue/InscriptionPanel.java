package Vue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import javax.swing.text.MaskFormatter;
import DAO.*;
import Modele.*;

public class InscriptionPanel extends JPanel {
    private JTextField nomField = new JTextField(15);
    private JTextField prenomField = new JTextField(15);
    private JTextField emailField = new JTextField(15);
    private JPasswordField mdpField = new JPasswordField(15);
    private JPasswordField mdpConfirmField = new JPasswordField(15);
    private JComboBox<String> typeBox = new JComboBox<>(new String[]{"patient", "specialiste"});
    private JFormattedTextField dateNaissanceField;
    private JTextField adresseField = new JTextField(20);
    private JTextField telPatientField = new JTextField(15);
    private JTextField specialisationField = new JTextField(15);
    private JTextField telSpecField = new JTextField(15);
    private JLabel errorLabel = new JLabel();

    public InscriptionPanel(MainFrame frame) {
        setOpaque(false); // Important pour dessiner avec alpha
        setBackground(new Color(240, 248, 255));

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Inscription : ");
        titleLabel.setFont(new Font("Poppins", Font.BOLD, 26));
        titleLabel.setForeground(new Color(30, 144, 255));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(titleLabel, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 1;
        gbc.gridy++;

        addFields(gbc);

        JPanel patientPanel = buildPatientPanel();
        JPanel specialistePanel = buildSpecialistePanel();
        addPatientAndSpecPanels(gbc, patientPanel, specialistePanel);

        typeBox.addActionListener(e -> {
            boolean isPatient = "patient".equals(typeBox.getSelectedItem());
            patientPanel.setVisible(isPatient);
            specialistePanel.setVisible(!isPatient);
        });

        addErrorLabelAndButtons(gbc, frame);
    }

    private void addFields(GridBagConstraints gbc) {
        JLabel[] labels = {
                new JLabel("Nom:"), new JLabel("Prénom:"), new JLabel("Email:"),
                new JLabel("Mot de passe:"), new JLabel("Confirmer mot de passe:"),
                new JLabel("Type d'utilisateur:")
        };
        JComponent[] fields = {
                nomField, prenomField, emailField, mdpField, mdpConfirmField, typeBox
        };

        for (int i = 0; i < labels.length; i++) {
            labels[i].setFont(new Font("SansSerif", Font.PLAIN, 18));
            labels[i].setForeground(new Color(70, 70, 70));
            add(labels[i], gbc);
            gbc.gridx++;
            fields[i].setFont(new Font("SansSerif", Font.PLAIN, 16));
            fields[i].setBorder(BorderFactory.createLineBorder(new Color(100, 149, 237), 2));
            fields[i].setBackground(Color.white);
            add(fields[i], gbc);
            gbc.gridx = 0;
            gbc.gridy++;
        }
    }

    private JPanel buildPatientPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        setBackground(new Color(240, 248, 255));
        panel.setBorder(BorderFactory.createTitledBorder("Informations Patient"));

        try {
            MaskFormatter dateFormatter = new MaskFormatter("##/##/####");
            dateFormatter.setPlaceholderCharacter('_');
            dateNaissanceField = new JFormattedTextField(dateFormatter);
            dateNaissanceField.setColumns(10);
        } catch (ParseException e) {
            dateNaissanceField = new JFormattedTextField();
        }

        panel.add(new JLabel("Date de naissance (JJ/MM/AAAA):"));
        panel.add(dateNaissanceField);
        panel.add(new JLabel("Adresse:"));
        panel.add(adresseField);
        panel.add(new JLabel("Téléphone:"));
        panel.add(telPatientField);
        return panel;
    }

    private JPanel buildSpecialistePanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        setBackground(new Color(240, 248, 255));
        panel.setBorder(BorderFactory.createTitledBorder("Informations Spécialiste"));

        panel.add(new JLabel("Spécialisation:"));
        panel.add(specialisationField);
        panel.add(new JLabel("Téléphone:"));
        panel.add(telSpecField);
        panel.setVisible(false);
        return panel;
    }

    private void addPatientAndSpecPanels(GridBagConstraints gbc, JPanel patientPanel, JPanel specialistePanel) {
        gbc.gridwidth = 2;
        add(patientPanel, gbc);
        gbc.gridy++;
        add(specialistePanel, gbc);
    }

    private void addErrorLabelAndButtons(GridBagConstraints gbc, MainFrame frame) {
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy++;
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        add(errorLabel, gbc);

        gbc.gridy++;
        JButton valider = createStyledButton("Valider", new Color(50, 205, 50));
        add(valider, gbc);

        gbc.gridy++;
        JButton retour = createStyledButton("Retour", new Color(220, 20, 60));
        add(retour, gbc);

        retour.addActionListener(e -> frame.showPanel("accueil"));
        valider.addActionListener((ActionEvent e) -> handleValidation(frame));
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.white);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        Color originalColor = color;
        Color hoverColor = color.brighter();



        return button;
    }

    private void handleValidation(MainFrame frame) {
        errorLabel.setText("");

        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        String mdp = new String(mdpField.getPassword());
        String mdpConfirm = new String(mdpConfirmField.getPassword());
        String type = (String) typeBox.getSelectedItem();

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || mdp.isEmpty() || mdpConfirm.isEmpty()) {
            errorLabel.setText("Tous les champs sont obligatoires.");
            return;
        }
        if (!mdp.equals(mdpConfirm)) {
            errorLabel.setText("Les mots de passe ne correspondent pas.");
            return;
        }

        try {
            if ("patient".equals(type)) {
                inscrirePatient(nom, prenom, email, mdp, frame);
            } else {
                inscrireSpecialiste(nom, prenom, email, mdp, frame);
            }
        } catch (DAOException ex) {
            errorLabel.setText("Erreur: " + ex.getMessage());
        }
    }

    private void inscrirePatient(String nom, String prenom, String email, String mdp, MainFrame frame) throws DAOException {
        String dateN = dateNaissanceField.getText().trim();
        String[] parts = dateN.split("/");
        if (parts.length != 3) {
            errorLabel.setText("Date invalide.");
            return;
        }

        String dateIso = parts[2] + "-" + parts[1] + "-" + parts[0];
        String adresse = adresseField.getText().trim();
        String tel = telPatientField.getText().trim();

        if (dateIso.isEmpty() || adresse.isEmpty() || tel.isEmpty()) {
            errorLabel.setText("Tous les champs patient sont obligatoires.");
            return;
        }

        Patient patient = new Patient(0, nom, prenom, email, mdp, dateIso, adresse, tel);
        PatientDAO.inscrirePatient(patient);

        JOptionPane.showMessageDialog(this, "Inscription patient réussie !");
        frame.showPanel("connexion");
    }

    private void inscrireSpecialiste(String nom, String prenom, String email, String mdp, MainFrame frame) throws DAOException {
        String specialisation = specialisationField.getText().trim();
        String tel = telSpecField.getText().trim();

        if (specialisation.isEmpty() || tel.isEmpty()) {
            errorLabel.setText("Tous les champs spécialiste sont obligatoires.");
            return;
        }

        Specialiste specialiste = new Specialiste(0, nom, prenom, email, mdp, specialisation, tel);
        SpecialisteDAO.inscrireSpecialiste(specialiste);

        JOptionPane.showMessageDialog(this, "Inscription spécialiste réussie !");
        frame.showPanel("connexion");
    }
}
