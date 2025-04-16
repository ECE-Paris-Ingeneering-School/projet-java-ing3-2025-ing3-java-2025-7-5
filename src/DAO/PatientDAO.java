package DAO;

import Modele.Patient;
import DAO.DAOException;
import java.util.List;

public interface PatientDAO {
    void create(Patient patient) throws DAOException;
    Patient findById(int id) throws DAOException;
    List<Patient> findAll() throws DAOException;
    void update(Patient patient) throws DAOException;
    void delete(int id) throws DAOException;
}

