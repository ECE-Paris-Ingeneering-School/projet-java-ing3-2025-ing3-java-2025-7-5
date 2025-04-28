package Vue;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import java.util.List;

import Modele.*;
import DAO.*;
import Controleur.*;

public class DashboardPatientPanel extends JPanel {
    private final MainFrame frame;
    private Patient patient;
    private final PatientControleur controleur;
    private JTabbedPane tabbedPane;
    private JTable rdvTable;
    private JLabel prochainRdvLabel;
    private Color primaryColor = new Color(30, 144, 255); // DodgerBlue
    private Color secondaryColor = new Color(240, 248, 255); // AliceBlue

    private static final Color ERROR_COLOR = new Color(220, 20, 60);
    private static final Color SUCCESS_COLOR = new Color(50, 205, 50);

    public DashboardPatientPanel(MainFrame frame, Patient patient) {
        this.frame = frame;
        this.patient = patient;
        this.controleur = new PatientControleur(patient);

        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 10));
        setBackground(secondaryColor);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header
        add(createHeaderPanel(), BorderLayout.NORTH);

        // TabbedPane
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Tableau de bord", buildDashboardTab());
        tabbedPane.addTab("Rendez-vous", buildRdvTab());
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout(15, 15));
        header.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 2, 0, primaryColor),
                new EmptyBorder(10, 15, 10, 15)
        ));
        header.setBackground(Color.WHITE);

        // Info patient
        JPanel infoPanel = new JPanel(new GridLayout(3, 2, 10, 5));
        infoPanel.setOpaque(false);

        addInfoField(infoPanel, "Nom", patient.getNom());
        addInfoField(infoPanel, "Prénom", patient.getPrenom());
        addInfoField(infoPanel, "Email", patient.getEmail());
        addInfoField(infoPanel, "Téléphone", patient.getTelephone());
        addInfoField(infoPanel, "Date naissance", formatDate(patient.getDateNaissance()));
        addInfoField(infoPanel, "Adresse", patient.getAdresse());

        header.add(infoPanel, BorderLayout.CENTER);

        // Boutons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 5, 10));
        buttonPanel.setOpaque(false);

        JButton btnDeconnexion = createButton("Déconnexion", ERROR_COLOR, e -> frame.showPanel("accueil"));
        JButton btnModifier = createButton("Modifier profil", primaryColor, e -> showModifierProfilDialog());

        buttonPanel.add(btnDeconnexion);
        buttonPanel.add(btnModifier);
        header.add(buttonPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel buildDashboardTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setOpaque(false);

        // Prochain RDV
        JPanel rdvCard = createCardPanel("Prochain rendez-vous");
        prochainRdvLabel = new JLabel("Chargement...");
        prochainRdvLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        rdvCard.add(prochainRdvLabel, BorderLayout.CENTER);
        panel.add(rdvCard, BorderLayout.NORTH);

        // Actions
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        actionPanel.setOpaque(false);

        JButton btnPrendreRdv = createButton("Prendre RDV", SUCCESS_COLOR, e -> showRechercheRdvDialog());
        JButton btnHistorique = createButton("Historique", primaryColor, e -> showHistoriqueDialog());

        actionPanel.add(btnPrendreRdv);
        actionPanel.add(btnHistorique);
        panel.add(actionPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildRdvTab() {
        JPanel panel = new JPanel(new BorderLayout());
        // Table des rendez-vous
        String[] columns = {"Spécialiste", "Spécialité", "Date", "Statut", "Note", "Action"};
        Vector<Vector<Object>> data = chargerRendezVous();
        rdvTable = new JTable(new javax.swing.table.DefaultTableModel(data, new Vector<>(java.util.List.of(columns))) {
            public boolean isCellEditable(int row, int col) { return col == 5; }
        });
        rdvTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        rdvTable.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox(), this));
        JScrollPane scroll = new JScrollPane(rdvTable);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void loadData() {
        // Chargement asynchrone
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                loadProchainRdv();
                loadRendezVous();
                return null;
            }
        }.execute();
    }

    private void loadProchainRdv() {
        try {
            RendezVous prochain = controleur.getProchainRendezVous();
            SwingUtilities.invokeLater(() -> {
                if (prochain != null) {
                    String text = String.format("Le %s avec Dr. %s %s (%s)",
                            prochain.getDateHeure(),
                            prochain.getSpecialisteNom(),
                            prochain.getSpecialistePrenom(),
                            prochain.getSpecialisation());
                    prochainRdvLabel.setText(text);
                } else {
                    prochainRdvLabel.setText("Aucun rendez-vous à venir");
                }
            });
        } catch (DAOException e) {
            SwingUtilities.invokeLater(() ->
                    prochainRdvLabel.setText("Erreur de chargement"));
        }
    }

    private void loadRendezVous() {
        try {
            List<RendezVous> rdvs = controleur.getRendezVous();
            DefaultTableModel model = (DefaultTableModel) rdvTable.getModel();
            model.setRowCount(0); // Clear existing data

            for (RendezVous rdv : rdvs) {
                String note = "";
                try {
                    Historique histo = controleur.getHistoriqueByRendezVous(rdv.getId());
                    note = histo != null ? histo.getNotes() : "";
                } catch (DAOException e) {
                    note = "Erreur";
                }

                model.addRow(new Object[]{

                        rdv.getSpecialisteNom() + " " + rdv.getSpecialistePrenom(),
                        rdv.getSpecialisation(),
                        rdv.getDateHeure(),
                        rdv.getStatut(),
                        note,
                        "confirmé".equals(rdv.getStatut()) ? "Annuler" : " "
                });
            }
        } catch (DAOException e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors du chargement des rendez-vous: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Méthodes utilitaires
    private JPanel createCardPanel(String title) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));
        card.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(secondaryColor);
        card.add(titleLabel, BorderLayout.NORTH);

        return card;
    }

    private JButton createButton(String text, Color bgColor, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.addActionListener(listener);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
                button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void addInfoField(JPanel panel, String label, String value) {
        JLabel lbl = new JLabel(label + ":");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(new Color(100, 100, 100));
        panel.add(lbl);

        JLabel val = new JLabel(value != null ? value : "");
        val.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(val);
    }


    private String formatDate(String dateIso) {
        if (dateIso == null || dateIso.length() < 10) return "";
        try {
            String[] parts = dateIso.split("-");
            return parts[2] + "/" + parts[1] + "/" + parts[0];
        } catch (Exception e) {
            return dateIso;
        }
    }

    // Classes internes pour les boutons dans la table
    private static class ButtonRenderer extends JButton implements TableCellRenderer {

        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(UIManager.getColor("Button.background"));
            }

            setText((value == null) ? "" : value.toString());
            return this;
        }
    }
    private void chargerProchainRdv() {
        try {
            RendezVous prochain = controleur.getProchainRendezVous();
            if (prochain != null) {
                String spec = prochain.getSpecialisteNom() + " " + prochain.getSpecialistePrenom();
                String date = prochain.getDateHeure();
                String specialite = prochain.getSpecialisation();
                prochainRdvLabel.setText("Prochain rendez-vous : " + date + " avec " + spec + " (" + specialite + ")");
            } else {
                prochainRdvLabel.setText("Aucun prochain rendez-vous");
            }
        } catch (DAO.DAOException e) {
            prochainRdvLabel.setText("Erreur de chargement");
        }
    }
    private Vector<Vector<Object>> chargerRendezVous() {
        Vector<Vector<Object>> data = new Vector<>();
        try {
            List<RendezVous> rdvs = controleur.getRendezVous();
            for (RendezVous rdv : rdvs) {
                Vector<Object> row = new Vector<>();
                row.add(rdv.getSpecialisteNom() + " " + rdv.getSpecialistePrenom());
                row.add(rdv.getSpecialisation());
                row.add(rdv.getDateHeure());
                String statut = rdv.getStatut();
                row.add(statut);
                // Ajout de la note du spécialiste
                String note = "";
                try {
                    Modele.Historique histo = controleur.getHistoriqueByRendezVous(rdv.getId());
                    if (histo != null) note = histo.getNotes();
                } catch (Exception e) { note = ""; }
                row.add(note);
                if ("confirmé".equals(statut)) {
                    row.add("Annuler");
                } else {
                    row.add("");
                }
                row.add(rdv.getId()); // id caché pour action
                data.add(row);
            }
        } catch (DAO.DAOException e) {
            // rien
        }
        return data;
    }
    private void annulerRdv(int idRdv) {
        try {
            controleur.annulerRendezVous(idRdv);
            DAO.RendezVousDAO.remettreCreneauLibre(idRdv); // Remet le créneau en libre
            JOptionPane.showMessageDialog(this, "Rendez-vous annulé et créneau remis en libre.");
            tabbedPane.setComponentAt(1, buildRdvTab());
            loadProchainRdv();
        } catch (DAO.DAOException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'annulation : " + e.getMessage());
        }
    }
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private DashboardPatientPanel parent;
        private int row;
        public ButtonEditor(JCheckBox checkBox, DashboardPatientPanel parent) {
            super(checkBox);
            this.parent = parent;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.row = row;
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }
        public Object getCellEditorValue() {
            if (isPushed && "Annuler".equals(label)) {
                try {
                    int modelRow = parent.rdvTable.convertRowIndexToModel(row);
                    List<RendezVous> rdvs = parent.controleur.getRendezVous();
                    if (modelRow < rdvs.size()) {
                        int idRdv = rdvs.get(modelRow).getId();
                        annulerRdv(idRdv);
                    } else {
                        JOptionPane.showMessageDialog(parent, "Impossible d'identifier le rendez-vous à annuler.");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(parent, "Erreur lors de l'annulation du rendez-vous.");
                }
            }
            isPushed = false;
            return label;
        }
    }

    // Méthodes pour les dialogues (à implémenter selon tes besoins)
    private void showModifierProfilDialog() {
        // Création de la boîte de dialogue
        JDialog dialog = new JDialog((JFrame)SwingUtilities.getWindowAncestor(this), "Modifier le profil", true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(secondaryColor);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(secondaryColor);

        // Panel de formulaire
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Configuration des champs
        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);
        Dimension fieldSize = new Dimension(250, 30);

        // Champ Nom
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblNom = new JLabel("Nom:");
        lblNom.setFont(labelFont);
        formPanel.add(lblNom, gbc);

        gbc.gridx = 1;
        JTextField nomField = new JTextField(patient.getNom());
        styleTextField(nomField, fieldSize, fieldFont);
        formPanel.add(nomField, gbc);

        // Champ Prénom
        gbc.gridx = 0; gbc.gridy++;
        JLabel lblPrenom = new JLabel("Prénom:");
        lblPrenom.setFont(labelFont);
        formPanel.add(lblPrenom, gbc);

        gbc.gridx = 1;
        JTextField prenomField = new JTextField(patient.getPrenom());
        styleTextField(prenomField, fieldSize, fieldFont);
        formPanel.add(prenomField, gbc);

        // Champ Date de naissance
        gbc.gridx = 0; gbc.gridy++;
        JLabel lblDateNaiss = new JLabel("Date de naissance:");
        lblDateNaiss.setFont(labelFont);
        formPanel.add(lblDateNaiss, gbc);

        gbc.gridx = 1;
        JFormattedTextField dateField;
        try {
            MaskFormatter dateFormatter = new MaskFormatter("##/##/####");
            dateFormatter.setPlaceholderCharacter('_');
            dateField = new JFormattedTextField(dateFormatter);
            dateField.setText(formatDate(patient.getDateNaissance()));
        } catch (ParseException e) {
            dateField = new JFormattedTextField();
            dateField.setText(formatDate(patient.getDateNaissance()));
        }
        styleTextField(dateField, fieldSize, fieldFont);
        formPanel.add(dateField, gbc);

        // Champ Email
        gbc.gridx = 0; gbc.gridy++;
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(labelFont);
        formPanel.add(lblEmail, gbc);

        gbc.gridx = 1;
        JTextField emailField = new JTextField(patient.getEmail());
        styleTextField(emailField, fieldSize, fieldFont);
        formPanel.add(emailField, gbc);

        // Champ Téléphone
        gbc.gridx = 0; gbc.gridy++;
        JLabel lblTel = new JLabel("Téléphone:");
        lblTel.setFont(labelFont);
        formPanel.add(lblTel, gbc);

        gbc.gridx = 1;
        JTextField telField = new JTextField(patient.getTelephone());
        styleTextField(telField, fieldSize, fieldFont);
        formPanel.add(telField, gbc);

        // Champ Adresse
        gbc.gridx = 0; gbc.gridy++;
        JLabel lblAdresse = new JLabel("Adresse:");
        lblAdresse.setFont(labelFont);
        formPanel.add(lblAdresse, gbc);

        gbc.gridx = 1;
        JTextField adresseField = new JTextField(patient.getAdresse());
        styleTextField(adresseField, fieldSize, fieldFont);
        formPanel.add(adresseField, gbc);

        // Champ Mot de passe
        gbc.gridx = 0; gbc.gridy++;
        JLabel lblMdp = new JLabel("Nouveau mot de passe:");
        lblMdp.setFont(labelFont);
        formPanel.add(lblMdp, gbc);

        gbc.gridx = 1;
        JPasswordField mdpField = new JPasswordField();
        styleTextField(mdpField, fieldSize, fieldFont);
        formPanel.add(mdpField, gbc);

        // Champ Confirmation mot de passe
        gbc.gridx = 0; gbc.gridy++;
        JLabel lblMdpConfirm = new JLabel("Confirmer mot de passe:");
        lblMdpConfirm.setFont(labelFont);
        formPanel.add(lblMdpConfirm, gbc);

        gbc.gridx = 1;
        JPasswordField mdpConfirmField = new JPasswordField();
        styleTextField(mdpConfirmField, fieldSize, fieldFont);
        formPanel.add(mdpConfirmField, gbc);

        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton btnAnnuler = createDialogButton("Annuler", ERROR_COLOR, e -> dialog.dispose());
        JFormattedTextField finalDateField = dateField;
        JFormattedTextField finalDateField1 = dateField;
        JButton btnValider = createDialogButton("Enregistrer", SUCCESS_COLOR, e -> {
            // Validation des données
            String mdp = new String(mdpField.getPassword());
            String mdpConfirm = new String(mdpConfirmField.getPassword());

            if (!mdp.isEmpty() && !mdp.equals(mdpConfirm)) {
                JOptionPane.showMessageDialog(dialog,
                        "Les mots de passe ne correspondent pas",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Conversion de la date
            String dateNaissance = "";
            try {
                String dateText = finalDateField1.getText().trim();
                if (dateText.length() == 10) { // JJ/MM/AAAA
                    String[] parts = dateText.split("/");
                    if (parts.length == 3) {
                        dateNaissance = parts[2] + "-" + parts[1] + "-" + parts[0]; // AAAA-MM-JJ
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Format de date invalide (JJ/MM/AAAA requis)",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Mise à jour du patient
            Patient updated = new Patient(
                    patient.getId(),
                    nomField.getText().trim(),
                    prenomField.getText().trim(),
                    emailField.getText().trim(),
                    mdp.isEmpty() ? patient.getMotDePasse() : mdp,
                    dateNaissance, // Utilisation de la date convertie
                    adresseField.getText().trim(),
                    telField.getText().trim()

            );

            try {
                controleur.updateProfil(updated);
                JOptionPane.showMessageDialog(dialog,
                        "Profil mis à jour avec succès",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();

                // Rafraîchir l'affichage
                this.patient = updated;
                refreshDashboard();
            } catch (DAOException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Erreur lors de la mise à jour: " + ex.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });


        buttonPanel.add(btnAnnuler);
        buttonPanel.add(btnValider);

        // Assemblage des composants
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setContentPane(mainPanel);
        dialog.setVisible(true);
    }
    private void refreshDashboard() {
        // 1. Supprimer les composants existants
        removeAll();

        // 2. Recréer le header avec les nouvelles données
        add(createHeaderPanel(), BorderLayout.NORTH);

        // 3. Recréer le contenu (conserve l'onglet actif)
        int selectedTab = tabbedPane.getSelectedIndex();
        tabbedPane.removeAll();
        tabbedPane.addTab("Tableau de bord", buildDashboardTab());
        tabbedPane.addTab("Rendez-vous", buildRdvTab());
        tabbedPane.setSelectedIndex(selectedTab);

        add(tabbedPane, BorderLayout.CENTER);

        // 4. Forcer le rafraîchissement
        revalidate();
        repaint();

        // 5. Recharger les données
        loadData();
    }
    // Méthodes utilitaires pour le style
    private void styleTextField(JComponent field, Dimension size, Font font) {
        field.setPreferredSize(size);
        field.setFont(font);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
    }

    private JButton createDialogButton(String text, Color bgColor, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.addActionListener(listener);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    // Conversion de date JJ/MM/AAAA vers AAAA-MM-JJ
    private String convertDate(String date) {
        if (date == null || date.length() != 10) return "";
        String[] parts = date.split("/");
        if (parts.length == 3) {
            return parts[2] + "-" + parts[1] + "-" + parts[0];
        }
        return date;
    }
    private JButton createHoverButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
                button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
                button.setCursor(Cursor.getDefaultCursor());
            }
        });

        return button;
    }

    private void showRechercheRdvDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Prendre un rendez-vous", true);
        dialog.setSize(650, 450);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Partie recherche
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Recherche du spécialiste"));

        JLabel lblSpec = new JLabel("Spécialité : ");
        lblSpec.setFont(new Font("Arial", Font.PLAIN, 14));
        JTextField specialiteField = new JTextField(12);
        specialiteField.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel lblNom = new JLabel("Nom : ");
        lblNom.setFont(new Font("Arial", Font.PLAIN, 14));
        JTextField nomField = new JTextField(12);
        nomField.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton btnSearch = new JButton("Rechercher");
        btnSearch.setFont(new Font("Arial", Font.BOLD, 14));
        btnSearch.setBackground(new Color(70, 130, 180));
        btnSearch.setForeground(Color.WHITE);

        searchPanel.add(lblSpec);
        searchPanel.add(specialiteField);
        searchPanel.add(lblNom);
        searchPanel.add(nomField);
        searchPanel.add(btnSearch);

        panel.add(searchPanel, BorderLayout.NORTH);

        // Liste spécialistes
        DefaultListModel<Modele.Specialiste> model = new DefaultListModel<>();
        JList<Specialiste> list = getSpecialisteJList(model);
        list.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(BorderFactory.createTitledBorder("Spécialistes trouvés"));
        panel.add(scroll, BorderLayout.CENTER);

        // Partie créneaux
        JPanel creneauxPanel = new JPanel(new BorderLayout(10, 10));
        creneauxPanel.setBorder(BorderFactory.createTitledBorder("Créneaux disponibles"));
        panel.add(creneauxPanel, BorderLayout.SOUTH);

        btnSearch.addActionListener(e -> {
            model.clear();
            try {
                String spec = specialiteField.getText().trim();
                String nom = nomField.getText().trim();
                List<Modele.Specialiste> specs = DAO.SpecialisteDAO.getAllSpecialistes();
                for (Modele.Specialiste s : specs) {
                    boolean ok = true;
                    if (!spec.isEmpty() && !s.getSpecialisation().toLowerCase().contains(spec.toLowerCase())) ok = false;
                    if (!nom.isEmpty() && !s.getNom().toLowerCase().contains(nom.toLowerCase())) ok = false;
                    if (ok) model.addElement(s);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Erreur recherche : " + ex.getMessage());
            }
        });

        list.addListSelectionListener(e -> {
            creneauxPanel.removeAll();
            Modele.Specialiste selected = list.getSelectedValue();
            if (selected != null) {
                try {
                    List<Modele.RendezVous> creneaux = DAO.RendezVousDAO.getCreneauxDisponiblesPourSpecialiste(selected.getId());
                    DefaultListModel<Modele.RendezVous> creneauModel = new DefaultListModel<>();
                    for (Modele.RendezVous c : creneaux) {
                        creneauModel.addElement(c);
                    }
                    JList<Modele.RendezVous> creneauList = new JList<>(creneauModel);
                    creneauList.setFont(new Font("Arial", Font.PLAIN, 13));
                    creneauList.setCellRenderer(new DefaultListCellRenderer() {
                        public Component getListCellRendererComponent(JList<?> l, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                            Component c = super.getListCellRendererComponent(l, value, index, isSelected, cellHasFocus);
                            if (value instanceof Modele.RendezVous) {
                                Modele.RendezVous rdv = (Modele.RendezVous) value;
                                setText(rdv.getDateHeure() + " - " + rdv.getSpecialisation());
                            }
                            return c;
                        }
                    });

                    JScrollPane creneauScroll = new JScrollPane(creneauList);
                    JButton btnReserver = new JButton("Réserver ce créneau");
                    btnReserver.setFont(new Font("Arial", Font.BOLD, 14));
                    btnReserver.setBackground(new Color(34, 139, 34));
                    btnReserver.setForeground(Color.WHITE);

                    btnReserver.addActionListener(ev -> {
                        Modele.RendezVous rdv = creneauList.getSelectedValue();
                        if (rdv == null) {
                            JOptionPane.showMessageDialog(dialog, "Sélectionnez un créneau.");
                            return;
                        }
                        try {
                            DAO.RendezVousDAO.reserverCreneau(rdv.getId(), patient.getId());
                            JOptionPane.showMessageDialog(dialog, "Créneau réservé !");
                            dialog.dispose();
                            tabbedPane.setComponentAt(1, buildRdvTab());
                            loadProchainRdv();
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(dialog, "Erreur réservation : " + ex.getMessage());
                        }
                    });

                    JPanel p = new JPanel(new BorderLayout(10, 10));
                    p.add(creneauScroll, BorderLayout.CENTER);
                    p.add(btnReserver, BorderLayout.SOUTH);
                    creneauxPanel.add(p, BorderLayout.CENTER);

                } catch (Exception ex) {
                    creneauxPanel.add(new JLabel("Erreur chargement créneaux."), BorderLayout.CENTER);
                }
            }
            creneauxPanel.revalidate();
            creneauxPanel.repaint();
        });

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }


    private static JList<Specialiste> getSpecialisteJList(DefaultListModel<Specialiste> model) {
        JList<Specialiste> list = new JList<>(model);
        list.setCellRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> l, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(l, value, index, isSelected, cellHasFocus);
                if (value instanceof Specialiste) {
                    Specialiste s = (Specialiste) value;
                    setText(s.getNom() + " " + s.getPrenom() + " - " + s.getSpecialisation());
                }
                return c;
            }
        });
        return list;
    }

    private void showHistoriqueDialog() {
        // Configuration de la fenêtre
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Historique Médical", true);
        dialog.setSize(650, 450);
        dialog.setLocationRelativeTo(this);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(240, 248, 255)); // Fond bleu clair

        // Titre
        JLabel titleLabel = new JLabel("Historique des Consultations");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(30, 144, 255)); // Bleu
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Modèle de données
        DefaultListModel<Historique> model = new DefaultListModel<>();
        JList<Historique> list = new JList<>(model);
        list.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        list.setBackground(Color.WHITE);
        list.setSelectionBackground(new Color(200, 230, 255)); // Bleu clair pour la sélection
        list.setFixedCellHeight(60); // Hauteur fixe pour chaque élément

        // Renderer personnalisé
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value instanceof Historique) {
                    Historique h = (Historique) value;
                    String text = String.format("%s %s - %s\n%s | Notes: %s",
                            h.getSpecialistePrenom(),
                            h.getSpecialisteNom(),
                            h.getSpecialisation(),
                            h.getDateHeure(),
                            h.getNotes().isEmpty() ? "Aucune note" : h.getNotes());
                    setText(text);
                }
                return this;
            }
        });

        // Chargement des données
        try {
            List<Historique> histos = HistoriqueDAO.getHistoriqueCompletByPatient(patient.getId());
            for (Historique h : histos) {
                model.addElement(h);
            }
        } catch (DAOException e) {
            model.addElement(new Historique(0, 0, 0, "Erreur de chargement: " + e.getMessage()));
            JOptionPane.showMessageDialog(dialog,
                    "Erreur lors du chargement de l'historique",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }

        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bouton Fermer
        JButton closeButton = new JButton("Fermer");
        closeButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        closeButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setContentPane(mainPanel);
        dialog.setVisible(true);
    }
}