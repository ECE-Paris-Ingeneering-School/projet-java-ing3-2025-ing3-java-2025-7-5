package DAO;

import DAO.UtilisateurDAO;
import Modele.Utilisateur;
import DAO.DAOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDAOImpl implements UtilisateurDAO {

    // Requêtes SQL
    private static final String CREATE_SQL = "INSERT INTO UTILISATEUR (nom, prenom, email, mot_de_passe, type_utilisateur) VALUES (?, ?, ?, ?, ?)";
    private static final String FIND_BY_EMAIL_SQL = "SELECT * FROM UTILISATEUR WHERE email = ?";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM UTILISATEUR WHERE id = ?";
    private static final String FIND_ALL_SQL = "SELECT * FROM UTILISATEUR";
    private static final String UPDATE_SQL = "UPDATE UTILISATEUR SET nom=?, prenom=?, email=?, mot_de_passe=?, type_utilisateur=? WHERE id=?";
    private static final String DELETE_SQL = "DELETE FROM UTILISATEUR WHERE id=?";

    @Override
    public void create(Utilisateur utilisateur) throws DAOException {
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            try (PreparedStatement ps = connection.prepareStatement(CREATE_SQL, Statement.RETURN_GENERATED_KEYS)) {
                setCommonParameters(ps, utilisateur);
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        utilisateur.setId(rs.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur création utilisateur: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
    }

    @Override
    public Utilisateur findByEmail(String email) throws DAOException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(FIND_BY_EMAIL_SQL)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapUtilisateur(rs) : null;
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur recherche par email : " + e.getMessage());
        }
    }

    @Override
    public Utilisateur findById(int id) throws DAOException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(FIND_BY_ID_SQL)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapUtilisateur(rs) : null;
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur recherche par ID : " + e.getMessage());
        }
    }

    @Override
    public List<Utilisateur> findAll() throws DAOException {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(FIND_ALL_SQL)) {

            while (rs.next()) {
                utilisateurs.add(mapUtilisateur(rs));
            }
            return utilisateurs;
        } catch (SQLException e) {
            throw new DAOException("Erreur recherche tous utilisateurs : " + e.getMessage());
        }
    }

    @Override
    public void update(Utilisateur utilisateur) throws DAOException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(UPDATE_SQL)) {

            setCommonParameters(ps, utilisateur);
            ps.setInt(6, utilisateur.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("Erreur mise à jour utilisateur : " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) throws DAOException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(DELETE_SQL)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("Erreur suppression utilisateur : " + e.getMessage());
        }
    }

    private void setCommonParameters(PreparedStatement ps, Utilisateur u) throws SQLException {
        ps.setString(1, u.getNom());
        ps.setString(2, u.getPrenom());
        ps.setString(3, u.getEmail());
        ps.setString(4, u.getMotDePasse());
        ps.setString(5, u.getTypeUtilisateur());
    }

    private Utilisateur mapUtilisateur(ResultSet rs) throws SQLException {
        return new Utilisateur(
                rs.getInt("id"),
                rs.getString("nom"),
                rs.getString("prenom"),
                rs.getString("email"),
                rs.getString("mot_de_passe"),
                rs.getString("type_utilisateur")
        );
    }
}
