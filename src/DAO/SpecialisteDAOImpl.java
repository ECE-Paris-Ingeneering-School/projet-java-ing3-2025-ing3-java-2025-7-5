package DAO;

import Modele.Specialiste;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpecialisteDAOImpl implements SpecialisteDAO {
    private static final String CREATE_SQL =
            "INSERT INTO SPECIALISTE (nom, prenom, specialisation, telephone, email) VALUES (?, ?, ?, ?, ?)";
    private static final String FIND_BY_ID_SQL =
            "SELECT * FROM SPECIALISTE WHERE id = ?";
    private static final String FIND_ALL_SQL =
            "SELECT * FROM SPECIALISTE";
    private static final String UPDATE_SQL =
            "UPDATE SPECIALISTE SET nom=?, prenom=?, specialisation=?, telephone=?, email=? WHERE id=?";
    private static final String DELETE_SQL =
            "DELETE FROM SPECIALISTE WHERE id=?";
    private static final String FIND_BY_NOM_SQL =
            "SELECT * FROM SPECIALISTE WHERE nom LIKE ? OR prenom LIKE ?";
    private static final String FIND_BY_SPECIALITE_SQL =
            "SELECT * FROM SPECIALISTE WHERE specialisation LIKE ?";

    @Override
    public void create(Specialiste specialiste) throws DAOException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(CREATE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, specialiste.getNom());
            ps.setString(2, specialiste.getPrenom());
            ps.setString(3, specialiste.getSpecialisation());
            ps.setString(4, specialiste.getTelephone());
            ps.setString(5, specialiste.getEmail());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    specialiste.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur création spécialiste : " + e.getMessage(), e);
        }
    }

    @Override
    public Specialiste findById(int id) throws DAOException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(FIND_BY_ID_SQL)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapSpecialiste(rs) : null;
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur recherche spécialiste : " + e.getMessage(), e);
        }
    }

    @Override
    public List<Specialiste> findAll() throws DAOException {
        List<Specialiste> list = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(FIND_ALL_SQL)) {
            while (rs.next()) {
                list.add(mapSpecialiste(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DAOException("Erreur récupération de tous les spécialistes : " + e.getMessage(), e);
        }
    }

    @Override
    public List<Specialiste> findByNom(String nom) throws DAOException {
        List<Specialiste> list = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(FIND_BY_NOM_SQL)) {
            ps.setString(1, "%" + nom + "%");
            ps.setString(2, "%" + nom + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapSpecialiste(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DAOException("Erreur recherche par nom : " + e.getMessage(), e);
        }
    }

    @Override
    public List<Specialiste> findBySpecialite(String specialite) throws DAOException {
        List<Specialiste> list = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(FIND_BY_SPECIALITE_SQL)) {
            ps.setString(1, "%" + specialite + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapSpecialiste(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DAOException("Erreur recherche par spécialité : " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Specialiste specialiste) throws DAOException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(UPDATE_SQL)) {
            ps.setString(1, specialiste.getNom());
            ps.setString(2, specialiste.getPrenom());
            ps.setString(3, specialiste.getSpecialisation());
            ps.setString(4, specialiste.getTelephone());
            ps.setString(5, specialiste.getEmail());
            ps.setInt(6, specialiste.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erreur mise à jour spécialiste : " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int id) throws DAOException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(DELETE_SQL)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erreur suppression spécialiste : " + e.getMessage(), e);
        }
    }

    private Specialiste mapSpecialiste(ResultSet rs) throws SQLException {
        return new Specialiste(
                rs.getInt("id"),
                rs.getString("nom"),
                rs.getString("prenom"),
                rs.getString("specialisation"),
                rs.getString("telephone"),
                rs.getString("email")
        );
    }
}
