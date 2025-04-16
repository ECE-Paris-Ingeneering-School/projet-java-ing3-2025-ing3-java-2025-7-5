package Vue;

import Modele.Patient;
import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class ProfilPatientView extends JFrame {
    private Patient patient;
    private JButton btnPrendreRdv, btnMesRdvs, btnModifierProfil;

    public ProfilPatientView(Patient patient) {
        this.patient = patient;
        setTitle("Profil Patient - DoctoLib");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        // Panel principal avec BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header avec infos patient
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // Centre avec onglets (style Doctolib)
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Tableau de bord", createDashboardPanel());
        tabbedPane.addTab("Mes rendez-vous", createAppointmentsPanel());
        tabbedPane.addTab("Documents", createDocumentsPanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        // Photo de profil (placeholder)
        JLabel photoLabel = new JLabel(new ImageIcon("placeholder_profile.png"));
        photoLabel.setPreferredSize(new Dimension(80, 80));
        photoLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        // Infos patient
        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        infoPanel.add(new JLabel("Bonjour " + patient.getPrenom() + " " + patient.getNom(), JLabel.LEFT));
        infoPanel.add(new JLabel("Né(e) le: " +
                patient.getDateNaissance().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        infoPanel.add(new JLabel("Email: " + patient.getEmail()));
        infoPanel.add(new JLabel("Tél: " + patient.getTelephone()));

        // Boutons d'action
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPrendreRdv = new JButton("Prendre RDV");
        btnMesRdvs = new JButton("Mes RDV"); // Pourrait être un bouton
        btnModifierProfil = new JButton("Modifier profil");

        buttonPanel.add(btnPrendreRdv);
        buttonPanel.add(btnMesRdvs);
        buttonPanel.add(btnModifierProfil);

        // Assemblage
        JPanel westPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        westPanel.add(photoLabel);
        westPanel.add(infoPanel);

        headerPanel.add(westPanel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Prochain RDV
        JPanel nextAppointmentPanel = new JPanel(new BorderLayout());
        nextAppointmentPanel.setBorder(BorderFactory.createTitledBorder("Prochain rendez-vous"));
        nextAppointmentPanel.add(new JLabel("Vous n'avez pas de rendez-vous prévu", JLabel.CENTER));

        // Actions rapides
        JPanel quickActionsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        quickActionsPanel.setBorder(BorderFactory.createTitledBorder("Actions rapides"));
        quickActionsPanel.add(createQuickActionButton("Prendre RDV", "calendar_icon.png"));
        quickActionsPanel.add(createQuickActionButton("Mes documents", "documents_icon.png"));
        quickActionsPanel.add(createQuickActionButton("Mon historique", "history_icon.png"));
        quickActionsPanel.add(createQuickActionButton("Mes favoris", "favorites_icon.png"));

        panel.add(nextAppointmentPanel, BorderLayout.NORTH);
        panel.add(quickActionsPanel, BorderLayout.CENTER);

        return panel;
    }

    private JButton createQuickActionButton(String text, String iconPath) {
        JButton button = new JButton(text, new ImageIcon(iconPath));
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        return button;
    }

    private JPanel createAppointmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Tableau des RDV
        String[] columnNames = {"Date", "Heure", "Spécialiste", "Statut"};
        Object[][] data = {}; // Remplacer par les vrais données

        JTable appointmentsTable = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(appointmentsTable);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createDocumentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Espace documents (à implémenter)", JLabel.CENTER));
        return panel;
    }

    // Getters pour les boutons
    public JButton getBtnPrendreRdv() { return btnPrendreRdv; }
    public JButton getBtnModifierProfil() { return btnModifierProfil; }
}