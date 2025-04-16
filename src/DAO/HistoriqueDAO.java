package DAO;

import Modele.Historique;
import DAO.DAOException;
import java.util.List;

public interface HistoriqueDAO {
    void create(Historique historique) throws DAOException;
    Historique findById(int id) throws DAOException;
    List<Historique> findAll() throws DAOException;
    void update(Historique historique) throws DAOException;
    void delete(int id) throws DAOException;
    List<Historique> findByPatient(int patientId) throws DAOException;
}
