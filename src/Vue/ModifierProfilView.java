package Vue;

import Modele.Patient;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ModifierProfilView extends JDialog {
    private JTextField nomField, prenomField, emailField, telephoneField, adresseField;
    private JButton btnValider, btnAnnuler;

    public ModifierProfilView(Patient patient) {
        setTitle("Modifier mon profil");
        setSize(500, 400);
        setModal(true);
        setLocationRelativeTo(null);

        initUI(patient);
    }

    private void initUI(Patient patient) {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Formulaire
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));

        formPanel.add(new JLabel("Nom:"));
        nomField = new JTextField(patient.getNom());
        formPanel.add(nomField);

        formPanel.add(new JLabel("Prénom:"));
        prenomField = new JTextField(patient.getPrenom());
        formPanel.add(prenomField);

        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField(patient.getEmail());
        formPanel.add(emailField);

        formPanel.add(new JLabel("Téléphone:"));
        telephoneField = new JTextField(patient.getTelephone());
        formPanel.add(telephoneField);

        formPanel.add(new JLabel("Adresse:"));
        adresseField = new JTextField(patient.getAdresse());
        formPanel.add(adresseField);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnValider = new JButton("Valider");
        btnAnnuler = new JButton("Annuler");

        buttonPanel.add(btnValider);
        buttonPanel.add(btnAnnuler);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    // Getters
    public String getNom() { return nomField.getText(); }
    public String getPrenom() { return prenomField.getText(); }
    public String getEmail() { return emailField.getText(); }
    public String getTelephone() { return telephoneField.getText(); }
    public String getAdresse() { return adresseField.getText(); }

    // Listeners
    public void setValiderListener(ActionListener listener) {
        btnValider.addActionListener(listener);
    }

    public void setAnnulerListener(ActionListener listener) {
        btnAnnuler.addActionListener(listener);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }
}