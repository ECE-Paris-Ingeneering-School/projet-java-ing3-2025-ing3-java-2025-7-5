package Vue;

import javax.swing.*;
import java.awt.*;
import Modele.Patient;
import Modele.Specialiste;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public MainFrame() {
        setTitle("Doctolib - ECE");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 720);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(new AccueilPanel(this), "accueil");
        mainPanel.add(new InscriptionPanel(this), "inscription");
        mainPanel.add(new ConnexionPanel(this), "connexion");

        setContentPane(mainPanel);
        cardLayout.show(mainPanel, "accueil");
    }

    public void showPanel(String name) {
        cardLayout.show(mainPanel, name);
    }

    public void showDashboard(String typeUtilisateur) {
        // Cette méthode ne doit plus instancier DashboardSpecialistePanel sans Specialiste
        switch (typeUtilisateur) {
            case "patient":
                // Doit être appelé via showDashboardPatient(Patient patient)
                break;
            case "specialiste":
                // Doit être appelé via showDashboardSpecialiste(Specialiste specialiste)
                break;
            case "admin":
                mainPanel.add(new DashboardAdminPanel(this), "dashboard_admin");
                showPanel("dashboard_admin");
                break;
        }
    }

    public void showDashboardPatient(Patient patient) {
        mainPanel.add(new DashboardPatientPanel(this, patient), "dashboard_patient");
        showPanel("dashboard_patient");
    }

    public void showDashboardSpecialiste(Specialiste specialiste) {
        mainPanel.add(new DashboardSpecialistePanel(this, specialiste), "dashboard_specialiste");
        showPanel("dashboard_specialiste");
    }
}
