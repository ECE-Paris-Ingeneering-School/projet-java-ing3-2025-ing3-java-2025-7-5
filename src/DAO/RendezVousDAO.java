// DAO/RendezVousDAO.java
package DAO;

import Modele.RendezVous;
import java.util.List;

public interface RendezVousDAO {
    void create(RendezVous rendezVous) throws DAOException;
    RendezVous findById(int id) throws DAOException;
    List<RendezVous> findAll() throws DAOException;
    void update(RendezVous rendezVous) throws DAOException;
    void delete(int id) throws DAOException;
    List<RendezVous> findByPatient(int patientId) throws DAOException;

    // <— Ajout de cette méthode pour récupérer les RDV d’un spécialiste
    List<RendezVous> findBySpecialiste(int specialisteId) throws DAOException;
}
