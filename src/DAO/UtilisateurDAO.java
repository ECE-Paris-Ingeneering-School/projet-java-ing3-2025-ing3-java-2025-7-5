package DAO;

import Modele.Utilisateur;
import DAO.DAOException;
import java.util.List;

public interface UtilisateurDAO {
    void create(Utilisateur utilisateur) throws DAOException;
    Utilisateur findByEmail(String email) throws DAOException;
    Utilisateur findById(int id) throws DAOException;
    List<Utilisateur> findAll() throws DAOException;
    void update(Utilisateur utilisateur) throws DAOException;
    void delete(int id) throws DAOException;
}