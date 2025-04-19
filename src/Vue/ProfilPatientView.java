// Vue/ProfilPatientView.java
package Vue;

import Modele.Patient;
import Modele.RendezVous;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ProfilPatientView extends JFrame {
    private final Patient patient;
    private JButton btnModifierProfil;
    private JButton quickActionPrendreRdv;
    private JButton quickActionHistorique;
    private JButton btnAnnulerRdv;
    private JTable appointmentsTable;
    private DefaultTableModel appointmentsTableModel;
    private JLabel nextAppointmentLabel;

    public ProfilPatientView(Patient patient) {
        this.patient = patient;
        setTitle("Profil Patient - DoctoLib");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Tableau de bord", createDashboardPanel());
        tabbedPane.addTab("Mes rendez-vous", createAppointmentsPanel());
        tabbedPane.addTab("Documents", createDocumentsPanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        JLabel photoLabel = new JLabel(new ImageIcon("placeholder_profile.png"));
        photoLabel.setPreferredSize(new Dimension(80, 80));
        photoLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        infoPanel.add(new JLabel("Bonjour " + patient.getPrenom() + " " + patient.getNom()));
        infoPanel.add(new JLabel("Né(e) le: " +
                patient.getDateNaissance().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        infoPanel.add(new JLabel("Email: " + patient.getEmail()));
        infoPanel.add(new JLabel("Tél: " + patient.getTelephone()));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnModifierProfil = new JButton("Modifier profil");
        buttonPanel.add(btnModifierProfil);

        JPanel westPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        westPanel.add(photoLabel);
        westPanel.add(infoPanel);

        headerPanel.add(westPanel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        return headerPanel;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel nextAppointmentPanel = new JPanel(new BorderLayout());
        nextAppointmentPanel.setBorder(BorderFactory.createTitledBorder("Prochain rendez-vous"));
        nextAppointmentLabel = new JLabel("Vous n'avez pas de rendez-vous prévu", JLabel.CENTER);
        nextAppointmentLabel.setFont(nextAppointmentLabel.getFont().deriveFont(Font.BOLD, 14f));
        nextAppointmentPanel.add(nextAppointmentLabel, BorderLayout.CENTER);

        JPanel quickActionsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        quickActionsPanel.setBorder(BorderFactory.createTitledBorder("Actions rapides"));

        quickActionPrendreRdv = createQuickActionButton("Prendre RDV", "calendar_icon.png");
        quickActionsPanel.add(quickActionPrendreRdv);
        quickActionsPanel.add(createQuickActionButton("Mes documents", "documents_icon.png"));
        quickActionHistorique = createQuickActionButton("Mon historique", "history_icon.png");
        quickActionsPanel.add(quickActionHistorique);
        quickActionsPanel.add(createQuickActionButton("Mes favoris", "favorites_icon.png"));

        panel.add(nextAppointmentPanel, BorderLayout.NORTH);
        panel.add(quickActionsPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAppointmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        String[] columnNames = {"Date", "Heure", "Spécialiste", "Statut"};
        appointmentsTableModel = new DefaultTableModel(columnNames, 0);
        appointmentsTable = new JTable(appointmentsTableModel);
        panel.add(new JScrollPane(appointmentsTable), BorderLayout.CENTER);

        btnAnnulerRdv = new JButton("Annuler Rendez-Vous");
        btnAnnulerRdv.setEnabled(false);
        panel.add(btnAnnulerRdv, BorderLayout.SOUTH);

        // Activer bouton annuler si sélection
        appointmentsTable.getSelectionModel().addListSelectionListener(e -> {
            boolean enabled = appointmentsTable.getSelectedRow() >= 0;
            btnAnnulerRdv.setEnabled(enabled);
        });

        return panel;
    }

    private JPanel createDocumentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Espace documents (à implémenter)", JLabel.CENTER), BorderLayout.CENTER);
        return panel;
    }

    private JButton createQuickActionButton(String text, String iconPath) {
        JButton button = new JButton(text, new ImageIcon(iconPath));
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        return button;
    }

    public void setRendezVousData(List<RendezVous> rendezVousList) {
        appointmentsTableModel.setRowCount(0);
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
        for (RendezVous rdv : rendezVousList) {
            String date = rdv.getDateHeure().toLocalDate().format(dateFmt);
            String heure = rdv.getDateHeure().toLocalTime().format(timeFmt);
            String spec = rdv.getSpecialiste().getPrenom() + " " + rdv.getSpecialiste().getNom();
            String statut = rdv.getStatut();
            appointmentsTableModel.addRow(new Object[]{date, heure, spec, statut});
        }
    }

    public int getSelectedAppointmentIndex() {
        return appointmentsTable.getSelectedRow();
    }

    public void setNextAppointment(RendezVous prochain) {
        if (prochain == null) {
            nextAppointmentLabel.setText("Vous n'avez pas de rendez-vous prévu");
        } else {
            String date = prochain.getDateHeure()
                    .toLocalDate()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String heure = prochain.getDateHeure()
                    .toLocalTime()
                    .format(DateTimeFormatter.ofPattern("HH:mm"));
            String spec = prochain.getSpecialiste().getPrenom() + " " + prochain.getSpecialiste().getNom();
            nextAppointmentLabel.setText(String.format("%s à %s avec Dr %s", date, heure, spec));
        }
    }

    // Listeners
    public void setModifierProfilListener(ActionListener l) {
        btnModifierProfil.addActionListener(l);
    }
    public void setQuickActionPrendreRdvListener(ActionListener l) {
        quickActionPrendreRdv.addActionListener(l);
    }
    public void setHistoriqueListener(ActionListener l) {
        quickActionHistorique.addActionListener(l);
    }
    public void setAnnulerRdvListener(ActionListener l) {
        btnAnnulerRdv.addActionListener(l);
    }
}
