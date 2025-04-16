import DAO.DatabaseConnection;
import DAO.PatientDAO;
import DAO.PatientDAOImpl;
import DAO.UtilisateurDAO;
import DAO.UtilisateurDAOImpl;
import Vue.AccueilView;
import Controleur.AuthentificationController;


import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Initialisation sécurisée dans l'EDT (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            try {
                // 1. Initialisation de la connexion à la BDD
                DatabaseConnection.getConnection();

                // 2. Initialisation des DAO
                UtilisateurDAO utilisateurDAO = new UtilisateurDAOImpl();
                PatientDAO patientDAO = new PatientDAOImpl();

                // 3. Création de la vue principale
                AccueilView accueilView = new AccueilView();
                accueilView.setTitle("DoctoLib - Gestion de Rendez-vous");

                // 4. Création et activation du contrôleur principal
                new AuthentificationController(utilisateurDAO, patientDAO, accueilView);

                // 5. Configuration de la fenêtre principale
                accueilView.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                accueilView.pack(); // Ajuste la taille automatiquement
                accueilView.setLocationRelativeTo(null); // Centre la fenêtre
                accueilView.setVisible(true);

                // Message de démarrage dans la console
                System.out.println("Application DoctoLib démarrée avec succès");

            } catch (Exception e) {
                // Gestion des erreurs de démarrage
                JOptionPane.showMessageDialog(
                        null,
                        "Erreur critique lors du démarrage:\n" + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE
                );
                e.printStackTrace();
                System.exit(1);
            }
        });
    }
}