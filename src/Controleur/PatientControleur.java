package Controleur;

import Modele.Patient;
import Modele.RendezVous;
import Modele.Historique;
import DAO.*;
import java.util.List;

public class PatientControleur {
    private final Patient patient;

    public PatientControleur(Patient patient) {
        this.patient = patient;
    }

    public Patient getPatient() {
        return patient;
    }

    public RendezVous getProchainRendezVous() throws DAOException {
        return RendezVousDAO.getProchainRendezVous(patient.getId());
    }

    public List<RendezVous> getRendezVous() throws DAOException {
        return RendezVousDAO.getRendezVousPourPatient(patient.getId());
    }

    public void annulerRendezVous(int idRdv) throws DAOException {
        RendezVousDAO.annulerRendezVous(idRdv);
    }

    public void updateProfil(Patient updated) throws DAOException {
        PatientDAO.updatePatient(updated);
    }

    public Historique getHistoriqueByRendezVous(int rdvId) throws DAOException {
        return HistoriqueDAO.getHistoriqueByRendezVous(rdvId);
    }

}
