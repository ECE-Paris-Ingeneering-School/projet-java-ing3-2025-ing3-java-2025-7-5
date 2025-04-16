package DAO;

import DAO.RendezVousDAO;
import Modele.RendezVous;
import Modele.Patient;
import Modele.Specialiste;
import DAO.DAOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RendezVousDAOImpl implements RendezVousDAO {

    private static final String CREATE_SQL = "INSERT INTO RENDEZ_VOUS (patient_id, specialiste_id, date_heure, statut) VALUES (?, ?, ?, ?)";
    private static final String FIND_BY_ID_SQL = "SELECT r.*, p.*, s.* FROM RENDEZ_VOUS r " +
            "JOIN PATIENT p ON r.patient_id = p.id " +
            "JOIN SPECIALISTE s ON r.specialiste_id = s.id " +
            "WHERE r.id = ?";
    private static final String FIND_ALL_SQL = "SELECT r.*, p.*, s.* FROM RENDEZ_VOUS r " +
            "JOIN PATIENT p ON r.patient_id = p.id " +
            "JOIN SPECIALISTE s ON r.specialiste_id = s.id";
    private static final String UPDATE_SQL = "UPDATE RENDEZ_VOUS SET patient_id=?, specialiste_id=?, date_heure=?, statut=? WHERE id=?";
    private static final String DELETE_SQL = "DELETE FROM RENDEZ_VOUS WHERE id=?";
    private static final String FIND_BY_PATIENT_SQL = "SELECT r.*, p.*, s.* FROM RENDEZ_VOUS r " +
            "JOIN PATIENT p ON r.patient_id = p.id " +
            "JOIN SPECIALISTE s ON r.specialiste_id = s.id " +
            "WHERE r.patient_id = ?";

    @Override
    public void create(RendezVous rendezVous) throws DAOException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(CREATE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, rendezVous.getPatient().getId());
            ps.setInt(2, rendezVous.getSpecialiste().getId());
            ps.setTimestamp(3, Timestamp.valueOf(rendezVous.getDateHeure()));
            ps.setString(4, rendezVous.getStatut());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    rendezVous.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur création rendez-vous : " + e.getMessage());
        }
    }

    @Override
    public RendezVous findById(int id) throws DAOException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(FIND_BY_ID_SQL)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRendezVous(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur recherche rendez-vous : " + e.getMessage());
        }
    }

    @Override
    public List<RendezVous> findAll() throws DAOException {
        List<RendezVous> rendezVousList = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(FIND_ALL_SQL)) {

            while (rs.next()) {
                rendezVousList.add(mapRendezVous(rs));
            }
            return rendezVousList;
        } catch (SQLException e) {
            throw new DAOException("Erreur recherche tous rendez-vous : " + e.getMessage());
        }
    }

    @Override
    public void update(RendezVous rendezVous) throws DAOException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(UPDATE_SQL)) {

            ps.setInt(1, rendezVous.getPatient().getId());
            ps.setInt(2, rendezVous.getSpecialiste().getId());
            ps.setTimestamp(3, Timestamp.valueOf(rendezVous.getDateHeure()));
            ps.setString(4, rendezVous.getStatut());
            ps.setInt(5, rendezVous.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erreur mise à jour rendez-vous : " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) throws DAOException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(DELETE_SQL)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erreur suppression rendez-vous : " + e.getMessage());
        }
    }

    @Override
    public List<RendezVous> findByPatient(int patientId) throws DAOException {
        List<RendezVous> rendezVousList = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(FIND_BY_PATIENT_SQL)) {

            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rendezVousList.add(mapRendezVous(rs));
                }
                return rendezVousList;
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur recherche par patient : " + e.getMessage());
        }
    }

    private RendezVous mapRendezVous(ResultSet rs) throws SQLException {
        Patient patient = new Patient(
                rs.getInt("p.id"),
                rs.getString("p.nom"),
                rs.getString("p.prenom"),
                rs.getString("p.email"),
                rs.getString("p.mot_de_passe"),
                rs.getDate("p.date_naissance").toLocalDate(),
                rs.getString("p.adresse"),
                rs.getString("p.telephone")
        );

        Specialiste specialiste = new Specialiste(
                rs.getInt("s.id"),
                rs.getString("s.nom"),
                rs.getString("s.prenom"),
                rs.getString("s.specialisation"),
                rs.getString("s.telephone"),
                rs.getString("s.email")
        );

        return new RendezVous(
                rs.getInt("r.id"),
                patient,
                specialiste,
                rs.getTimestamp("r.date_heure").toLocalDateTime(),
                rs.getString("r.statut")
        );
    }
}