package Controleur;

import Modele.Utilisateur;
import Modele.Patient;
import Modele.Specialiste;
import Modele.RendezVous;
import Modele.Historique;
import DAO.*;
import java.util.List;

public class AdminControleur {
    public List<Utilisateur> getAllUtilisateurs() throws DAOException {
        return PatientDAO.getAllUtilisateurs();
    }
    public void updateUtilisateur(Utilisateur utilisateur, String colName, Object value) throws DAOException {
        PatientDAO.updateUtilisateur(utilisateur, colName, value);
    }
    public List<Patient> getAllPatients() throws DAOException {
        return PatientDAO.getAllPatients();
    }
    public List<Specialiste> getAllSpecialistes() throws DAOException {
        return SpecialisteDAO.getAllSpecialistes();
    }
    public List<RendezVous> getAllRendezVous() throws DAOException {
        return RendezVousDAO.getAllRendezVous();
    }
    public void updateRendezVous(int id, String colName, Object value) throws DAOException {
        RendezVousDAO.updateRendezVous(id, colName, value);
    }
    public List<Historique> getAllHistoriques() throws DAOException {
        return HistoriqueDAO.getAllHistoriques();
    }
    public void updateHistorique(int id, String colName, Object value) throws DAOException {
        HistoriqueDAO.updateHistorique(id, colName, value);
    }
}
