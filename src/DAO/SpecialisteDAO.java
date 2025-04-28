package DAO;

import Modele.Specialiste;
import java.sql.*;

public class SpecialisteDAO {
    public static void inscrireSpecialiste(Specialiste specialiste) throws DAOException {
        try (Connection conn = DBConnection.getConnection()) {
            // Vérifier unicité email
            PreparedStatement check = conn.prepareStatement("SELECT id FROM UTILISATEUR WHERE email = ?");
            check.setString(1, specialiste.getEmail());
            ResultSet rs = check.executeQuery();
            if (rs.next()) throw new DAOException("Email déjà utilisé.");
            // Insérer dans UTILISATEUR
            PreparedStatement psUser = conn.prepareStatement("INSERT INTO UTILISATEUR(nom, prenom, email, mot_de_passe, type_utilisateur) VALUES (?, ?, ?, ?, 'specialiste')", PreparedStatement.RETURN_GENERATED_KEYS);
            psUser.setString(1, specialiste.getNom());
            psUser.setString(2, specialiste.getPrenom());
            psUser.setString(3, specialiste.getEmail());
            psUser.setString(4, specialiste.getMotDePasse());
            psUser.executeUpdate();
            ResultSet gen = psUser.getGeneratedKeys();
            if (gen.next()) {
                int id = gen.getInt(1);
                // Insérer dans SPECIALISTE
                PreparedStatement ps = conn.prepareStatement("INSERT INTO SPECIALISTE(id, nom, prenom, specialisation, telephone, email) VALUES (?, ?, ?, ?, ?, ?)");
                ps.setInt(1, id);
                ps.setString(2, specialiste.getNom());
                ps.setString(3, specialiste.getPrenom());
                ps.setString(4, specialiste.getSpecialisation());
                ps.setString(5, specialiste.getTelephone());
                ps.setString(6, specialiste.getEmail());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur SpecialisteDAO: " + e.getMessage(), e);
        }
    }

    public static Specialiste getSpecialisteByEmailAndPassword(String email, String motDePasse) throws DAOException {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT u.id, u.nom, u.prenom, u.email, u.mot_de_passe, s.specialisation, s.telephone " +
                "FROM UTILISATEUR u JOIN SPECIALISTE s ON u.id = s.id WHERE BINARY u.email = ?");
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String mdpBdd = rs.getString("mot_de_passe");
                if (mdpBdd != null && mdpBdd.equals(motDePasse)) {
                    return new Modele.Specialiste(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("mot_de_passe"),
                        rs.getString("specialisation"),
                        rs.getString("telephone")
                    );
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DAOException("Erreur SpecialisteDAO: " + e.getMessage(), e);
        }
    }

    public static java.util.List<Specialiste> getAllSpecialistes() throws DAOException {
        java.util.List<Specialiste> list = new java.util.ArrayList<>();
        try (java.sql.Connection conn = DBConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement("SELECT u.id, s.nom, s.prenom, u.email, u.mot_de_passe, s.specialisation, s.telephone FROM UTILISATEUR u JOIN SPECIALISTE s ON u.id = s.id");
             java.sql.ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Specialiste(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getString("mot_de_passe"),
                    rs.getString("specialisation"),
                    rs.getString("telephone")
                ));
            }
        } catch (java.sql.SQLException e) {
            throw new DAOException("Erreur getAllSpecialistes: " + e.getMessage(), e);
        }
        return list;
    }

    public static void updateSpecialiste(Specialiste specialiste) throws DAOException {
        try (java.sql.Connection conn = DBConnection.getConnection()) {
            java.sql.PreparedStatement ps1 = conn.prepareStatement("UPDATE UTILISATEUR SET nom=?, prenom=?, email=?, mot_de_passe=? WHERE id=?");
            ps1.setString(1, specialiste.getNom());
            ps1.setString(2, specialiste.getPrenom());
            ps1.setString(3, specialiste.getEmail());
            ps1.setString(4, specialiste.getMotDePasse());
            ps1.setInt(5, specialiste.getId());
            ps1.executeUpdate();
            java.sql.PreparedStatement ps2 = conn.prepareStatement("UPDATE SPECIALISTE SET specialisation=?, telephone=?, email=? WHERE id=?");
            ps2.setString(1, specialiste.getSpecialisation());
            ps2.setString(2, specialiste.getTelephone());
            ps2.setString(3, specialiste.getEmail());
            ps2.setInt(4, specialiste.getId());
            ps2.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new DAOException("Erreur updateSpecialiste: " + e.getMessage(), e);
        }
    }
}
