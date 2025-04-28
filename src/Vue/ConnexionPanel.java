package Vue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import DAO.DAOException;
import DAO.PatientDAO;
import DAO.SpecialisteDAO;
import Modele.Patient;
import Modele.Specialiste;
import Modele.Utilisateur;

public class ConnexionPanel extends JPanel {
    private JTextField emailField = new JTextField(20);
    private JPasswordField mdpField = new JPasswordField(20);
    private JLabel errorLabel = new JLabel();

    public ConnexionPanel(MainFrame frame) {
        // Set beautiful background color
        setBackground(new Color(240, 248, 255)); // Light Blue-ish

        // Set layout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Bienvenue !");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        titleLabel.setForeground(new Color(30, 144, 255)); // Dodger Blue
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(titleLabel, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 1;
        gbc.gridy++;

        // Email Label
        add(createLabel("Email:"), gbc);
        gbc.gridx++;
        stylizeField(emailField);
        add(emailField, gbc);

        // Password Label
        gbc.gridx = 0;
        gbc.gridy++;
        add(createLabel("Mot de passe:"), gbc);
        gbc.gridx++;
        stylizeField(mdpField);
        add(mdpField, gbc);

        // Error Label
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        add(errorLabel, gbc);

        // Valider Button
        gbc.gridy++;
        JButton valider = createStyledButton("Se connecter", new Color(50, 205, 50)); // Lime Green
        add(valider, gbc);

        // Retour Button
        gbc.gridy++;
        JButton retour = createStyledButton("Retour", new Color(220, 20, 60)); // Crimson
        add(retour, gbc);

        // Actions
        retour.addActionListener(e -> frame.showPanel("accueil"));

        valider.addActionListener((ActionEvent e) -> {
            errorLabel.setText("");
            String email = emailField.getText().trim();
            String mdp = new String(mdpField.getPassword());
            if (email.isEmpty() || mdp.isEmpty()) {
                errorLabel.setText("Veuillez remplir tous les champs.");
                return;
            }
            try {
                Patient patient = PatientDAO.getPatientByEmailAndPassword(email, mdp);
                if (patient != null) {
                    frame.showDashboardPatient(patient);
                    return;
                }
                Specialiste specialiste = SpecialisteDAO.getSpecialisteByEmailAndPassword(email, mdp);
                if (specialiste != null) {
                    frame.showDashboardSpecialiste(specialiste);
                    return;
                }
                Utilisateur admin = getAdminByEmailAndPassword(email, mdp);
                if (admin != null) {
                    frame.showDashboard("admin");
                    return;
                }
                errorLabel.setText("Identifiants invalides.");
            } catch (DAOException ex) {
                errorLabel.setText("Erreur: " + ex.getMessage());
            }
        });
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 18));
        label.setForeground(new Color(70, 70, 70));
        return label;
    }

    private void stylizeField(JTextField field) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createLineBorder(new Color(100, 149, 237), 2)); // Cornflower Blue
        field.setBackground(Color.white);
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.white);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }

    private Utilisateur getAdminByEmailAndPassword(String email, String motDePasse) throws DAOException {
        try (java.sql.Connection conn = DAO.DBConnection.getConnection()) {
            java.sql.PreparedStatement ps = conn.prepareStatement(
                    "SELECT id, nom, prenom, email, mot_de_passe, type_utilisateur FROM UTILISATEUR WHERE BINARY email = ? AND type_utilisateur = 'admin'");
            ps.setString(1, email);
            java.sql.ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String mdpBdd = rs.getString("mot_de_passe");
                if (mdpBdd != null && mdpBdd.equals(motDePasse)) {
                    return new Utilisateur(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("email"),
                            rs.getString("mot_de_passe"),
                            rs.getString("type_utilisateur")
                    );
                }
            }
            return null;
        } catch (java.sql.SQLException e) {
            throw new DAOException("Erreur Admin Connexion: " + e.getMessage(), e);
        }
    }
}
