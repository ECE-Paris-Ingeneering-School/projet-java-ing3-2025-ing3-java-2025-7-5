// Vue/HistoriqueView.java
package Vue;

import Modele.RendezVous;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HistoriqueView extends JFrame {
    private JTable historyTable;
    private DefaultTableModel historyTableModel;

    public HistoriqueView(List<RendezVous> historiqueList) {
        setTitle("Historique des rendez-vous");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI(historiqueList);
    }

    private void initUI(List<RendezVous> historiqueList) {
        String[] cols = {"Date", "Heure", "Spécialiste", "Statut"};
        historyTableModel = new DefaultTableModel(cols, 0);
        historyTable = new JTable(historyTableModel);
        getContentPane().add(new JScrollPane(historyTable), BorderLayout.CENTER);
        populate(historiqueList);
    }

    private void populate(List<RendezVous> list) {
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
        for (RendezVous r : list) {
            String date = r.getDateHeure().toLocalDate().format(dateFmt);
            String heure = r.getDateHeure().toLocalTime().format(timeFmt);
            String spec = r.getSpecialiste().getPrenom() + " " + r.getSpecialiste().getNom();
            String statut = r.getStatut();
            historyTableModel.addRow(new Object[]{date, heure, spec, statut});
        }
    }
}
