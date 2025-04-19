// DAO/SpecialisteDAO.java
package DAO;

import Modele.Specialiste;
import java.util.List;

public interface SpecialisteDAO {
    void create(Specialiste specialiste) throws DAOException;
    Specialiste findById(int id) throws DAOException;
    List<Specialiste> findAll() throws DAOException;
    void update(Specialiste specialiste) throws DAOException;
    void delete(int id) throws DAOException;

    // ← Ajouté pour la recherche par nom et par spécialité
    List<Specialiste> findByNom(String nom) throws DAOException;
    List<Specialiste> findBySpecialite(String specialite) throws DAOException;
}
