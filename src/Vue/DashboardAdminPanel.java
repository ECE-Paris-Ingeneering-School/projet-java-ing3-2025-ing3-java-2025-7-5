package Vue;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import DAO.*;
import Modele.*;

public class DashboardAdminPanel extends JPanel {
    private MainFrame frame;
    private JTabbedPane tabbedPane = new JTabbedPane();
    private JTable utilisateurTable, patientTable, specialisteTable, rdvTable, historiqueTable;
    private Controleur.AdminControleur controleur = new Controleur.AdminControleur();

    public DashboardAdminPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 250, 255)); // fond doux

        add(buildHeaderPanel(), BorderLayout.NORTH);

        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 14));
        tabbedPane.setBackground(new Color(220, 235, 250));
        tabbedPane.setForeground(new Color(30, 30, 30));
        tabbedPane.setBorder(BorderFactory.createLineBorder(new Color(180, 200, 240)));

        tabbedPane.addTab("UTILISATEUR", buildUtilisateurTab());
        tabbedPane.addTab("PATIENT", buildPatientTab());
        tabbedPane.addTab("SPÉCIALISTE", buildSpecialisteTab());
        tabbedPane.addTab("RENDEZ-VOUS", buildRdvTab());
        tabbedPane.addTab("HISTORIQUE", buildHistoriqueTab());
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel buildHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(230, 240, 255));
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel title = new JLabel("🛠️ Espace Administrateur - Gestion des Tables");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(new Color(25, 80, 160));
        header.add(title, BorderLayout.WEST);

        JButton btnDeconnexion = new JButton("Déconnexion");
        btnDeconnexion.setBackground(new Color(255, 99, 71));
        btnDeconnexion.setForeground(Color.WHITE);
        btnDeconnexion.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnDeconnexion.setFocusPainted(false);
        btnDeconnexion.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

        btnDeconnexion.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btnDeconnexion.setBackground(new Color(255, 69, 0));
            }

            public void mouseExited(MouseEvent e) {
                btnDeconnexion.setBackground(new Color(255, 99, 71));
            }
        });

        btnDeconnexion.addActionListener(e -> frame.showPanel("accueil"));
        header.add(btnDeconnexion, BorderLayout.EAST);

        return header;
    }

    private JTable createStyledTable(Vector<Vector<Object>> data, String[] columns, String tableName) {
        JTable table = new JTable(new DefaultTableModel(data, new Vector<>(List.of(columns))) {
            public boolean isCellEditable(int row, int col) { return col != 0; }
        });

        table.setRowHeight(28);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setGridColor(new Color(210, 220, 240));
        table.setShowGrid(true);
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(new Color(180, 205, 255));
        table.setSelectionForeground(Color.BLACK);
        table.setIntercellSpacing(new Dimension(6, 4));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setBackground(new Color(220, 235, 250));
        header.setForeground(new Color(25, 25, 25));
        header.setReorderingAllowed(false);

        table.putClientProperty("tableName", tableName);
        table.getModel().addTableModelListener(e -> handleEdit(table, columns));

        return table;
    }

    private JPanel buildUtilisateurTab() {
        Vector<Vector<Object>> data = new Vector<>();
        String[] columns = {"id", "nom", "prenom", "email", "mot_de_passe", "type_utilisateur"};
        try {
            for (Utilisateur u : controleur.getAllUtilisateurs()) {
                Vector<Object> row = new Vector<>();
                row.add(u.getId()); row.add(u.getNom()); row.add(u.getPrenom());
                row.add(u.getEmail()); row.add(u.getMotDePasse()); row.add(u.getTypeUtilisateur());
                data.add(row);
            }
        } catch (Exception ignored) {}
        utilisateurTable = createStyledTable(data, columns, "UTILISATEUR");
        return new JPanel(new BorderLayout()) {{
            add(new JScrollPane(utilisateurTable), BorderLayout.CENTER);
        }};
    }

    private JPanel buildPatientTab() {
        Vector<Vector<Object>> data = new Vector<>();
        String[] columns = {"id", "date_naissance", "adresse", "telephone"};
        try {
            for (Patient p : controleur.getAllPatients()) {
                Vector<Object> row = new Vector<>();
                row.add(p.getId()); row.add(p.getDateNaissance()); row.add(p.getAdresse()); row.add(p.getTelephone());
                data.add(row);
            }
        } catch (Exception ignored) {}
        patientTable = createStyledTable(data, columns, "PATIENT");
        return new JPanel(new BorderLayout()) {{
            add(new JScrollPane(patientTable), BorderLayout.CENTER);
        }};
    }

    private JPanel buildSpecialisteTab() {
        Vector<Vector<Object>> data = new Vector<>();
        String[] columns = {"id", "nom", "prenom", "specialisation", "telephone", "email"};
        try {
            for (Specialiste s : controleur.getAllSpecialistes()) {
                Vector<Object> row = new Vector<>();
                row.add(s.getId()); row.add(s.getNom()); row.add(s.getPrenom());
                row.add(s.getSpecialisation()); row.add(s.getTelephone()); row.add(s.getEmail());
                data.add(row);
            }
        } catch (Exception ignored) {}
        specialisteTable = createStyledTable(data, columns, "SPECIALISTE");
        return new JPanel(new BorderLayout()) {{
            add(new JScrollPane(specialisteTable), BorderLayout.CENTER);
        }};
    }

    private JPanel buildRdvTab() {
        Vector<Vector<Object>> data = new Vector<>();
        String[] columns = {"id", "patient_id", "specialiste_id", "date_heure", "statut"};
        try {
            for (RendezVous r : controleur.getAllRendezVous()) {
                Vector<Object> row = new Vector<>();
                row.add(r.getId()); row.add(null); row.add(null); row.add(r.getDateHeure()); row.add(r.getStatut());
                data.add(row);
            }
        } catch (Exception ignored) {}
        rdvTable = createStyledTable(data, columns, "RENDEZ_VOUS");
        return new JPanel(new BorderLayout()) {{
            add(new JScrollPane(rdvTable), BorderLayout.CENTER);
        }};
    }

    private JPanel buildHistoriqueTab() {
        Vector<Vector<Object>> data = new Vector<>();
        String[] columns = {"id", "patient_id", "rendez_vous_id", "notes"};
        try {
            for (Historique h : controleur.getAllHistoriques()) {
                Vector<Object> row = new Vector<>();
                row.add(h.getId()); row.add(h.getPatientId()); row.add(h.getRendezVousId()); row.add(h.getNotes());
                data.add(row);
            }
        } catch (Exception ignored) {}
        historiqueTable = createStyledTable(data, columns, "HISTORIQUE");
        return new JPanel(new BorderLayout()) {{
            add(new JScrollPane(historiqueTable), BorderLayout.CENTER);
        }};
    }

    private void handleEdit(JTable table, String[] columns) {
        int row = table.getSelectedRow();
        int col = table.getSelectedColumn();
        if (row < 0 || col < 0) return;
        Object newValue = table.getValueAt(row, col);
        Object oldValue = ((DefaultTableModel) table.getModel()).getValueAt(row, col);
        int id = (int) table.getValueAt(row, 0);
        String colName = columns[col];
        int res = JOptionPane.showConfirmDialog(this, "Valider la modification de " + colName + " ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.YES_OPTION) {
            try {
                String tableName = (String) table.getClientProperty("tableName");
                switch (tableName) {
                    case "UTILISATEUR":
                        for (Utilisateur u : controleur.getAllUtilisateurs()) {
                            if (u.getId() == id) {
                                controleur.updateUtilisateur(u, colName, newValue);
                                break;
                            }
                        }
                        break;
                    case "PATIENT":
                        break;
                    case "SPECIALISTE":
                        break;
                    case "RENDEZ_VOUS":
                        controleur.updateRendezVous(id, colName, newValue);
                        break;
                    case "HISTORIQUE":
                        controleur.updateHistorique(id, colName, newValue);
                        break;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la modification: " + ex.getMessage());
            }
        } else {
            ((DefaultTableModel) table.getModel()).setValueAt(oldValue, row, col);
        }
    }
}
