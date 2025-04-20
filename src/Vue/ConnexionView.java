package Vue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ConnexionView extends JDialog {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton btnConnexion;

    public ConnexionView() {
        // Configuration de la boîte de dialogue
        setTitle("Connexion");
        setModal(true);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel principal avec BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel de formulaire avec GridBagLayout
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Champ Email
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);

        // Champ Mot de passe
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Mot de passe:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        passwordField = new JPasswordField(20);

        // Ajout d'un KeyListener pour la touche Enter
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnConnexion.doClick();
                }
            }
        });
        formPanel.add(passwordField, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnConnexion = new JButton("Se connecter");
        btnConnexion.setPreferredSize(new Dimension(120, 30));
        buttonPanel.add(btnConnexion);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    // Getters et méthodes pour les listeners (inchangés)
    public String getEmail() { return emailField.getText(); }
    public String getPassword() { return new String(passwordField.getPassword()); }
    public void setConnexionListener(ActionListener listener) { btnConnexion.addActionListener(listener); }
    public void showError(String message) { JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE); }
}