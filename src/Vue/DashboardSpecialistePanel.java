package Vue;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.util.Vector;
import java.util.List;
import DAO.*;
import Modele.*;

public class DashboardSpecialistePanel extends JPanel {
    private MainFrame frame;
    private Specialiste specialiste;
    private Controleur.SpecialisteControleur controleur;
    private JTabbedPane tabbedPane = new JTabbedPane();
    private JTable rdvTable;
    private JTable patientsTable;
    private JLabel prochainRdvLabel = new JLabel();

    public DashboardSpecialistePanel(MainFrame frame, Specialiste specialiste) {
        this.frame = frame;
        this.specialiste = specialiste;
        this.controleur = new Controleur.SpecialisteControleur(specialiste);
        setLayout(new BorderLayout());
        add(buildHeaderPanel(), BorderLayout.NORTH);
        tabbedPane.addTab("Tableau de bord", buildDashboardTab());
        tabbedPane.addTab("Mes rendez-vous", buildRdvTab());
        tabbedPane.addTab("Mes patients", buildPatientsTab());
        tabbedPane.addTab("Mes créneaux", buildCreneauxTab());
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel buildHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        JPanel infos = new JPanel(new GridLayout(3,2));
        infos.add(new JLabel(specialiste.getNom() + " " + specialiste.getPrenom()));
        infos.add(new JLabel(specialiste.getEmail()));
        infos.add(new JLabel("Spécialisation: " + specialiste.getSpecialisation()));
        infos.add(new JLabel("Téléphone: " + specialiste.getTelephone()));
        header.add(infos, BorderLayout.WEST);
        JButton btnDeconnexion = new JButton("Déconnexion");
        btnDeconnexion.addActionListener(e -> frame.showPanel("accueil"));
        header.add(btnDeconnexion, BorderLayout.EAST);
        return header;
    }

    private JPanel buildDashboardTab() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel prochainPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        prochainPanel.add(new JLabel("Prochain rendez-vous : "));
        prochainPanel.add(prochainRdvLabel);
        panel.add(prochainPanel, BorderLayout.NORTH);
        // Ajout du formulaire pour ajouter un créneau
        JPanel dispoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dispoPanel.add(new JLabel("Ajouter un créneau (AAAA-MM-JJ HH:MM): "));
        javax.swing.text.MaskFormatter mask = null;
        try {
            mask = new javax.swing.text.MaskFormatter("####-##-## ##:##");
            mask.setPlaceholderCharacter('_');
        } catch (java.text.ParseException e) { }
        JFormattedTextField dateField = (mask != null) ? new JFormattedTextField(mask) : new JFormattedTextField();
        dateField.setColumns(16);
        JButton btnAjouter = new JButton("Ajouter");
        JButton btnGrille = new JButton("Grille semaine");
        dispoPanel.add(dateField);
        dispoPanel.add(btnAjouter);
        dispoPanel.add(btnGrille);
        btnAjouter.addActionListener(e -> {
            String dateHeure = dateField.getText().trim();
            if (dateHeure.contains("_")) {
                JOptionPane.showMessageDialog(this, "Veuillez saisir une date/heure complète.");
                return;
            }
            try {
                controleur.ajouterCreneauDisponibilite(dateHeure);
                JOptionPane.showMessageDialog(this, "Créneau ajouté !");
                dateField.setText("");
                tabbedPane.setComponentAt(3, buildCreneauxTab()); // refresh creneaux
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
            }
        });
        btnGrille.addActionListener(e -> ouvrirGrilleCreneaux());
        panel.add(dispoPanel, BorderLayout.CENTER);
        chargerProchainRdv();
        return panel;
    }

    private void ouvrirGrilleCreneaux() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Sélectionner un créneau", true);
        dialog.setSize(800, 450);
        dialog.setLocationRelativeTo(this);
        JPanel panel = new JPanel(new BorderLayout());
        String[] jours = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"};
        String[] heures = new String[25];
        for (int i = 0; i < 25; i++) {
            int h = 8 + (i / 2);
            int m = (i % 2) * 30;
            heures[i] = String.format("%02d:%02d", h, m);
        }
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton prevWeek = new JButton("< Semaine précédente");
        JButton nextWeek = new JButton("Semaine suivante >");
        JLabel semaineLabel = new JLabel();
        navPanel.add(prevWeek);
        navPanel.add(semaineLabel);
        navPanel.add(nextWeek);
        panel.add(navPanel, BorderLayout.NORTH);
        final int[] weekOffset = {0};
        final Runnable[] updateGrille = new Runnable[1];
        updateGrille[0] = () -> {
            panel.removeAll();
            panel.add(navPanel, BorderLayout.NORTH);
            JPanel grille = new JPanel(new GridLayout(heures.length+1, jours.length+1));
            grille.add(new JLabel(""));
            for (String jour : jours) grille.add(new JLabel(jour, SwingConstants.CENTER));
            java.time.LocalDate today = java.time.LocalDate.now();
            java.time.DayOfWeek dow = today.getDayOfWeek();
            java.time.LocalDate lundi = today.minusDays((dow.getValue() + 6) % 7).plusWeeks(weekOffset[0]);
            semaineLabel.setText("Semaine du " + lundi.toString());
            java.util.List<RendezVous> creneauxExistants;
            try {
                creneauxExistants = controleur.getCreneauxDisponibles();
            } catch (Exception e) {
                creneauxExistants = new java.util.ArrayList<>();
            }
            java.util.Set<String> creneauxSet = new java.util.HashSet<>();
            for (RendezVous c : creneauxExistants) {
                creneauxSet.add(c.getDateHeure().substring(0, 16));
            }
            for (int i = 0; i < heures.length; i++) {
                grille.add(new JLabel(heures[i], SwingConstants.CENTER));
                for (int j = 0; j < jours.length; j++) {
                    java.time.LocalDate date = lundi.plusDays(j);
                    String dateStr = date.toString();
                    String dateHeure = dateStr + " " + heures[i];
                    JButton btn = new JButton("");
                    btn.setPreferredSize(new Dimension(60, 25));
                    if (creneauxSet.contains(dateHeure)) {
                        btn.setBackground(Color.LIGHT_GRAY);
                        btn.setText("Pris");
                        btn.setEnabled(false);
                    } else {
                        btn.setText("+");
                        btn.addActionListener(ev -> {
                            try {
                                controleur.ajouterCreneauDisponibilite(dateHeure);
                                JOptionPane.showMessageDialog(this, "Créneau ajouté : " + dateHeure);
                                updateGrille[0].run();
                                dialog.dispose();
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
                            }
                        });
                    }
                    grille.add(btn);
                }
            }
            panel.add(grille, BorderLayout.CENTER);
            panel.revalidate();
            panel.repaint();
        };
        prevWeek.addActionListener(e -> { weekOffset[0]--; updateGrille[0].run(); });
        nextWeek.addActionListener(e -> { weekOffset[0]++; updateGrille[0].run(); });
        updateGrille[0].run();
        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void chargerProchainRdv() {
        try {
            RendezVous prochain = controleur.getProchainRendezVous();
            if (prochain != null) {
                prochainRdvLabel.setText(prochain.getDateHeure() + " avec " + prochain.getSpecialisteNom());
            } else {
                prochainRdvLabel.setText("Aucun prochain rendez-vous");
            }
        } catch (Exception e) {
            prochainRdvLabel.setText("Erreur de chargement");
        }
    }

    private JPanel buildRdvTab() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"Patient", "Date", "Statut", "Note", "Action"};
        Vector<Vector<Object>> data = new Vector<>();
        List<RendezVous> rdvs = null;
        try {
            rdvs = controleur.getRendezVous();
            for (RendezVous rdv : rdvs) {
                Vector<Object> row = new Vector<>();
                row.add(rdv.getSpecialisteNom() + " " + rdv.getSpecialistePrenom()); // Nom du patient
                row.add(rdv.getDateHeure());
                String statut = rdv.getStatut();
                row.add(statut);
                // Charger la note existante
                String note = "";
                try {
                    Modele.Historique histo = DAO.HistoriqueDAO.getHistoriqueByRendezVous(rdv.getId());
                    if (histo != null) note = histo.getNotes();
                } catch (Exception e) { note = ""; }
                row.add(note);
                if ("confirmé".equalsIgnoreCase(statut)) {
                    row.add("Terminer");
                } else {
                    row.add("");
                }
                data.add(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des rendez-vous: " + e.getMessage());
        }
        DefaultTableModel model = new DefaultTableModel(data, new Vector<>(List.of(columns))) {
            @Override
            public boolean isCellEditable(int row, int col) {
                // Note (3) éditable, Action (4) éditable si "Terminer"
                if (col == 3) return true;
                if (col == 4) {
                    Object value = getValueAt(row, col);
                    return value != null && "Terminer".equals(value.toString());
                }
                return false;
            }
        };
        rdvTable = new JTable(model);
        rdvTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        rdvTable.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox(), this));
        // Listener pour sauvegarder la note en BDD
        List<RendezVous> finalRdvs = rdvs;
        model.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE && e.getColumn() == 3) {
                int row = e.getFirstRow();
                String nouvelleNote = (String) model.getValueAt(row, 3);
                int idRdv = finalRdvs.get(row).getId();
                try {
                    Modele.Historique histo = DAO.HistoriqueDAO.getHistoriqueByRendezVous(idRdv);
                    if (histo != null) {
                        DAO.HistoriqueDAO.updateHistorique(histo.getId(), "notes", nouvelleNote);
                    } else {
                        int patientId = getPatientIdByRdvId(idRdv);
                        DAO.HistoriqueDAO.ajouterNote(patientId, idRdv, nouvelleNote);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la sauvegarde de la note: " + ex.getMessage());
                }
            }
        });
        JScrollPane scroll = new JScrollPane(rdvTable);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }
    // Méthode utilitaire pour retrouver le patient_id à partir d'un id de RDV
    private int getPatientIdByRdvId(int rdvId) throws Exception {
        try (java.sql.Connection conn = DAO.DBConnection.getConnection()) {
            java.sql.PreparedStatement ps = conn.prepareStatement("SELECT patient_id FROM RENDEZ_VOUS WHERE id = ?");
            ps.setInt(1, rdvId);
            java.sql.ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("patient_id");
            }
        }
        throw new Exception("Impossible de retrouver le patient pour ce rendez-vous.");
    }

    private JPanel buildPatientsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        // Ajout de la colonne cachée 'ID' pour fiabilité
        String[] columns = {"Nom", "Prénom", "Téléphone", "Historique", "ID"};
        Vector<Vector<Object>> data = new Vector<>();
        try {
            List<Patient> patients = controleur.getPatientsSuivis();
            for (Patient p : patients) {
                Vector<Object> row = new Vector<>();
                row.add(p.getNom());
                row.add(p.getPrenom());
                row.add(p.getTelephone());
                row.add("Voir");
                row.add(p.getId()); // ID caché
                data.add(row);
            }
        } catch (Exception e) { }
        patientsTable = new JTable(new DefaultTableModel(data, new Vector<>(List.of(columns))) {
            public boolean isCellEditable(int row, int col) { return col == 3; }
        });
        // Masquer la colonne ID à l'affichage
        patientsTable.getColumnModel().getColumn(4).setMinWidth(0);
        patientsTable.getColumnModel().getColumn(4).setMaxWidth(0);
        patientsTable.getColumnModel().getColumn(4).setWidth(0);
        patientsTable.getColumn("Historique").setCellRenderer(new ButtonRenderer());
        patientsTable.getColumn("Historique").setCellEditor(new ButtonEditorPatient(new JCheckBox(), this));
        JScrollPane scroll = new JScrollPane(patientsTable);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildCreneauxTab() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"ID", "Date/Heure", "Statut", "Action"};
        Vector<Vector<Object>> data = new Vector<>();
        try {
            List<RendezVous> creneaux = controleur.getAllCreneaux(); // Nouvelle méthode pour tous les créneaux du spécialiste
            for (RendezVous c : creneaux) {
                Vector<Object> row = new Vector<>();
                row.add(c.getId());
                row.add(c.getDateHeure());
                row.add(c.getStatut());
                row.add("Supprimer");
                data.add(row);
            }
        } catch (Exception e) { }
        JTable table = new JTable(new DefaultTableModel(data, new Vector<>(List.of(columns))) {
            public boolean isCellEditable(int row, int col) { return col == 3; }
        });
        // Masquer la colonne ID à l'affichage
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);
        table.getColumn("Action").setCellRenderer(new ButtonRenderer());
        table.getColumn("Action").setCellEditor(new ButtonEditorCreneau(new JCheckBox(), this, table));
        JScrollPane scroll = new JScrollPane(table);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // Renderer et Editor pour bouton dans JTable
    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() { setOpaque(true); }
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private DashboardSpecialistePanel parent;
        private int row;
        public ButtonEditor(JCheckBox checkBox, DashboardSpecialistePanel parent) {
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
            if (isPushed && "Terminer".equals(label)) {
                try {
                    int modelRow = parent.rdvTable.convertRowIndexToModel(row);
                    List<RendezVous> rdvs = parent.controleur.getRendezVous();
                    if (modelRow < rdvs.size()) {
                        int idRdv = rdvs.get(modelRow).getId();
                        terminerRdv(idRdv);
                    } else {
                        JOptionPane.showMessageDialog(parent, "Impossible d'identifier le rendez-vous à terminer.");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(parent, "Erreur lors de la terminaison du rendez-vous.");
                }
            }
            isPushed = false;
            return label;
        }
    }
    private void terminerRdv(int idRdv) {
        try {
            controleur.terminerRendezVous(idRdv);
            JOptionPane.showMessageDialog(this, "Rendez-vous terminé et ajouté à l'historique.");
            tabbedPane.setComponentAt(1, buildRdvTab()); // Rafraîchir l'onglet des rendez-vous
            chargerProchainRdv();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de la terminaison du rendez-vous: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // Pour le débogage
        }
    }
    class ButtonEditorPatient extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private DashboardSpecialistePanel parent;
        private int row;

        public ButtonEditorPatient(JCheckBox checkBox, DashboardSpecialistePanel parent) {
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
            if (isPushed && "Voir".equals(label)) {
                try {
                    DefaultTableModel model = (DefaultTableModel) parent.patientsTable.getModel();
                    // Récupérer l'ID du patient de manière sécurisée
                    if (model.getColumnCount() > 4) { // Vérifier si la colonne ID existe
                        int idPatient = Integer.parseInt(model.getValueAt(row, 4).toString());
                        afficherHistorique(idPatient);
                    } else {
                        // Fallback: utiliser les informations visibles pour identifier le patient
                        String nom = model.getValueAt(row, 0).toString();
                        String prenom = model.getValueAt(row, 1).toString();
                        JOptionPane.showMessageDialog(parent,
                                "Impossible de récupérer l'historique pour " + prenom + " " + nom +
                                        ".\nL'identifiant du patient est manquant.");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(parent, "Erreur lors de l'accès à l'historique: " + e.getMessage());
                }
            }
            isPushed = false;
            return label;
        }
    }
    private void afficherHistorique(int idPatient) {
        // Création d'une fenêtre personnalisée plutôt qu'une simple JOptionPane
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this),
                "Historique complet du patient", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Création du modèle de tableau
        String[] columns = {"Date/Heure", "Statut", "Notes"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Rendre toutes les cellules non éditables
            }
        };

        try {
            List<Historique> histos = controleur.getHistoriquesByPatient(idPatient);
            if (histos.isEmpty()) {
                model.addRow(new Object[]{"Aucun historique disponible", "", ""});
            } else {
                for (Historique h : histos) {
                    model.addRow(new Object[]{
                            h.getDateHeure(),  // Date et heure formatées
                            h.getStatut(),     // Statut du RDV
                            h.getNotes()       // Notes du spécialiste
                    });
                }
            }
        } catch (Exception e) {
            model.addRow(new Object[]{"Erreur de chargement", e.getMessage(), ""});
        }

        // Création du tableau avec style
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Centrer le contenu des colonnes Date et Statut
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        // Ajout du tableau à un JScrollPane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        panel.add(scrollPane, BorderLayout.CENTER);
        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    class ButtonEditorCreneau extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private DashboardSpecialistePanel parent;
        private JTable table;
        private int row;
        public ButtonEditorCreneau(JCheckBox checkBox, DashboardSpecialistePanel parent, JTable table) {
            super(checkBox);
            this.parent = parent;
            this.table = table;
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
            if (isPushed && "Supprimer".equals(label)) {
                Object idObj = table.getModel().getValueAt(row, 0); // colonne 0 = id
                int idRdv;
                if (idObj instanceof Integer) {
                    idRdv = (Integer) idObj;
                } else if (idObj instanceof String) {
                    try {
                        idRdv = Integer.parseInt((String) idObj);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(parent, "Erreur : id du créneau invalide.");
                        isPushed = false;
                        return label;
                    }
                } else {
                    JOptionPane.showMessageDialog(parent, "Erreur : id du créneau inconnu.");
                    isPushed = false;
                    return label;
                }
                try {
                    controleur.supprimerCreneau(idRdv);
                    JOptionPane.showMessageDialog(parent, "Créneau supprimé.");
                    parent.tabbedPane.setComponentAt(3, parent.buildCreneauxTab());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(parent, "Erreur lors de la suppression.");
                }
            }
            isPushed = false;
            return label;
        }
    }
}
