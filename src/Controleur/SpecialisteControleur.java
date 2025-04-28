package Controleur;

import Modele.Specialiste;
import Modele.Patient;
import Modele.RendezVous;
import Modele.Historique;
import DAO.*;
import java.util.List;

public class SpecialisteControleur {
    private final Specialiste specialiste;

    public SpecialisteControleur(Specialiste specialiste) {
        this.specialiste = specialiste;
    }

    public Specialiste getSpecialiste() {
        return specialiste;
    }

    public RendezVous getProchainRendezVous() throws DAOException {
        return RendezVousDAO.getProchainRendezVousPourSpecialiste(specialiste.getId());
    }

    public List<RendezVous> getRendezVous() throws DAOException {
        return RendezVousDAO.getRendezVousPourSpecialiste(specialiste.getId());
    }

    public void terminerRendezVous(int idRdv) throws DAOException {
        RendezVousDAO.terminerRendezVous(idRdv);
    }

    public List<Patient> getPatientsSuivis() throws DAOException {
        return PatientDAO.getPatientsBySpecialiste(specialiste.getId());
    }

    public List<Historique> getHistoriquesByPatient(int patientId) throws DAOException {
        return HistoriqueDAO.getHistoriqueCompletByPatient(patientId);
    }


    public void ajouterCreneauDisponibilite(String dateHeure) throws DAO.DAOException {
        DAO.RendezVousDAO.ajouterCreneauDisponibilite(specialiste.getId(), dateHeure);
    }

    public java.util.List<RendezVous> getCreneauxDisponibles() throws DAO.DAOException {
        return DAO.RendezVousDAO.getCreneauxDisponiblesPourSpecialiste(specialiste.getId());
    }

    public void supprimerCreneau(int idRdv) throws DAO.DAOException {
        try (java.sql.Connection conn = DAO.DBConnection.getConnection()) {
            java.sql.PreparedStatement ps = conn.prepareStatement("DELETE FROM RENDEZ_VOUS WHERE id = ?");
            ps.setInt(1, idRdv);
            ps.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new DAO.DAOException("Erreur suppression créneau: " + e.getMessage(), e);
        }
    }

    public List<RendezVous> getAllCreneaux() throws DAO.DAOException {
        return DAO.RendezVousDAO.getAllCreneauxPourSpecialiste(specialiste.getId());
    }
}
