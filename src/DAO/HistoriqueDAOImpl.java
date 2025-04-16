package DAO;

import DAO.HistoriqueDAO;
import Modele.Historique;
import Modele.Patient;
import Modele.RendezVous;
import DAO.DAOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HistoriqueDAOImpl implements HistoriqueDAO {

    private static final String CREATE_SQL = "INSERT INTO HISTORIQUE (patient_id, rendez_vous_id, notes) VALUES (?, ?, ?)";
    private static final String FIND_BY_ID_SQL = "SELECT h.*, p.*, r.* FROM HISTORIQUE h " +
            "JOIN PATIENT p ON h.patient_id = p.id " +
            "JOIN RENDEZ_VOUS r ON h.rendez_vous_id = r.id " +
            "WHERE h.id = ?";
    private static final String FIND_ALL_SQL = "SELECT h.*, p.*, r.* FROM HISTORIQUE h " +
            "JOIN PATIENT p ON h.patient_id = p.id " +
            "JOIN RENDEZ_VOUS r ON h.rendez_vous_id = r.id";
    private static final String UPDATE_SQL = "UPDATE HISTORIQUE SET patient_id=?, rendez_vous_id=?, notes=? WHERE id=?";
    private static final String DELETE_SQL = "DELETE FROM HISTORIQUE WHERE id=?";
    private static final String FIND_BY_PATIENT_SQL = "SELECT h.*, p.*, r.* FROM HISTORIQUE h " +
            "JOIN PATIENT p ON h.patient_id = p.id " +
            "JOIN RENDEZ_VOUS r ON h.rendez_vous_id = r.id " +
            "WHERE h.patient_id = ?";

    @Override
    public void create(Historique historique) throws DAOException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(CREATE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, historique.getPatient().getId());
            ps.setInt(2, historique.getRendezVous().getId());
            ps.setString(3, historique.getNotes());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    historique.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur création historique : " + e.getMessage());
        }
    }

    @Override
    public Historique findById(int id) throws DAOException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(FIND_BY_ID_SQL)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapHistorique(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur recherche historique : " + e.getMessage());
        }
    }

    @Override
    public List<Historique> findAll() throws DAOException {
        List<Historique> historiques = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(FIND_ALL_SQL)) {

            while (rs.next()) {
                historiques.add(mapHistorique(rs));
            }
            return historiques;
        } catch (SQLException e) {
            throw new DAOException("Erreur recherche tous historiques : " + e.getMessage());
        }
    }

    @Override
    public void update(Historique historique) throws DAOException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(UPDATE_SQL)) {

            ps.setInt(1, historique.getPatient().getId());
            ps.setInt(2, historique.getRendezVous().getId());
            ps.setString(3, historique.getNotes());
            ps.setInt(4, historique.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erreur mise à jour historique : " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) throws DAOException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(DELETE_SQL)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erreur suppression historique : " + e.getMessage());
        }
    }

    @Override
    public List<Historique> findByPatient(int patientId) throws DAOException {
        List<Historique> historiques = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(FIND_BY_PATIENT_SQL)) {

            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    historiques.add(mapHistorique(rs));
                }
                return historiques;
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur recherche par patient : " + e.getMessage());
        }
    }

    private Historique mapHistorique(ResultSet rs) throws SQLException {
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

        RendezVous rendezVous = new RendezVous(
                rs.getInt("r.id"),
                patient,
                null, // Spécialiste non chargé pour simplifier
                rs.getTimestamp("r.date_heure").toLocalDateTime(),
                rs.getString("r.statut")
        );

        return new Historique(
                rs.getInt("h.id"),
                patient,
                rendezVous,
                rs.getString("h.notes")
        );
    }
}
