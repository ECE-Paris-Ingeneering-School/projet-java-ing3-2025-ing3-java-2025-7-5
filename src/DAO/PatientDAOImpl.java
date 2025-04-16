package DAO;

import DAO.PatientDAO;
import Modele.Patient;
import DAO.DAOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAOImpl implements PatientDAO {

    private static final String CREATE_SQL = "INSERT INTO PATIENT (id, date_naissance, adresse, telephone) VALUES (?, ?, ?, ?)";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM PATIENT p JOIN UTILISATEUR u ON p.id = u.id WHERE p.id = ?";
    private static final String FIND_ALL_SQL = "SELECT * FROM PATIENT p JOIN UTILISATEUR u ON p.id = u.id";
    private static final String UPDATE_SQL = "UPDATE PATIENT SET date_naissance=?, adresse=?, telephone=? WHERE id=?";
    private static final String DELETE_SQL = "DELETE FROM PATIENT WHERE id=?";

    private final UtilisateurDAOImpl utilisateurDAO = new UtilisateurDAOImpl();

    @Override
    public void create(Patient patient) throws DAOException {
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);

            // 1. Création dans UTILISATEUR
            utilisateurDAO.create(patient);

            // 2. Création dans PATIENT
            try (PreparedStatement ps = connection.prepareStatement(CREATE_SQL)) {
                ps.setInt(1, patient.getId());
                ps.setDate(2, Date.valueOf(patient.getDateNaissance()));
                ps.setString(3, patient.getAdresse());
                ps.setString(4, patient.getTelephone());
                ps.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            DatabaseConnection.rollback(connection);
            throw new DAOException("Erreur création patient: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
    }

    @Override
    public Patient findById(int id) throws DAOException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(FIND_BY_ID_SQL)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapPatient(rs) : null;
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur recherche patient : " + e.getMessage());
        }
    }

    @Override
    public List<Patient> findAll() throws DAOException {
        List<Patient> patients = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(FIND_ALL_SQL)) {

            while (rs.next()) {
                patients.add(mapPatient(rs));
            }
            return patients;
        } catch (SQLException e) {
            throw new DAOException("Erreur recherche tous patients : " + e.getMessage());
        }
    }

    @Override
    public void update(Patient patient) throws DAOException {
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);

            // Mise à jour UTILISATEUR
            utilisateurDAO.update(patient);

            // Mise à jour PATIENT
            try (PreparedStatement ps = connection.prepareStatement(UPDATE_SQL)) {
                ps.setDate(1, Date.valueOf(patient.getDateNaissance().toString()));
                ps.setString(2, patient.getAdresse());
                ps.setString(3, patient.getTelephone());
                ps.setInt(4, patient.getId());
                ps.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            DatabaseConnection.rollback(connection);
            throw new DAOException("Erreur mise à jour patient : " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
    }

    @Override
    public void delete(int id) throws DAOException {
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);

            // Suppression PATIENT
            try (PreparedStatement ps = connection.prepareStatement(DELETE_SQL)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }

            // Suppression UTILISATEUR
            utilisateurDAO.delete(id);

            connection.commit();
        } catch (SQLException e) {
            DatabaseConnection.rollback(connection);
            throw new DAOException("Erreur suppression patient : " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
    }

    private Patient mapPatient(ResultSet rs) throws SQLException {
        Patient patient = new Patient(
                rs.getInt("id"),
                rs.getString("nom"),
                rs.getString("prenom"),
                rs.getString("email"),
                rs.getString("mot_de_passe"),
                rs.getDate("date_naissance").toLocalDate(),
                rs.getString("adresse"),
                rs.getString("telephone")
        );
        return patient;
    }
}
