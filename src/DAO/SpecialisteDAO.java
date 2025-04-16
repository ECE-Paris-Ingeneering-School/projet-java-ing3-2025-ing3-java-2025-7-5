package DAO;

import Modele.Specialiste;
import DAO.DAOException;
import java.util.List;

public interface SpecialisteDAO {
    void create(Specialiste specialiste) throws DAOException;
    Specialiste findById(int id) throws DAOException;
    List<Specialiste> findAll() throws DAOException;
    void update(Specialiste specialiste) throws DAOException;
    void delete(int id) throws DAOException;
}
