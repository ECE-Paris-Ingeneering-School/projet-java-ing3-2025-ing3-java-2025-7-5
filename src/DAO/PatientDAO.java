package DAO;

import Modele.Patient;
import java.sql.*;

public class PatientDAO {
    public static Patient getPatientByEmailAndPassword(String email, String motDePasse) throws DAOException {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT u.id, u.nom, u.prenom, u.email, u.mot_de_passe, p.date_naissance, p.adresse, p.telephone " +
                "FROM UTILISATEUR u JOIN PATIENT p ON u.id = p.id WHERE BINARY u.email = ?");
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String mdpBdd = rs.getString("mot_de_passe");
                if (mdpBdd != null && mdpBdd.equals(motDePasse)) {
                    return new Patient(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("mot_de_passe"),
                        rs.getString("date_naissance"),
                        rs.getString("adresse"),
                        rs.getString("telephone")
                    );
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DAOException("Erreur PatientDAO: " + e.getMessage(), e);
        }
    }

    public static Patient getPatientById(int id) throws DAOException {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT u.id, u.nom, u.prenom, u.email, u.mot_de_passe, p.date_naissance, p.adresse, p.telephone " +
                "FROM UTILISATEUR u JOIN PATIENT p ON u.id = p.id WHERE u.id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Patient(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getString("mot_de_passe"),
                    rs.getString("date_naissance"),
                    rs.getString("adresse"),
                    rs.getString("telephone")
                );
            }
            return null;
        } catch (SQLException e) {
            throw new DAOException("Erreur PatientDAO: " + e.getMessage(), e);
        }
    }

    public static void updatePatient(Patient patient) throws DAOException {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps1;
            if (patient.getMotDePasse() != null && !patient.getMotDePasse().isEmpty()) {
                ps1 = conn.prepareStatement("UPDATE UTILISATEUR SET nom=?, prenom=?, email=?, mot_de_passe=? WHERE id=?");
                ps1.setString(1, patient.getNom());
                ps1.setString(2, patient.getPrenom());
                ps1.setString(3, patient.getEmail());
                ps1.setString(4, patient.getMotDePasse());
                ps1.setInt(5, patient.getId());
            } else {
                ps1 = conn.prepareStatement("UPDATE UTILISATEUR SET nom=?, prenom=?, email=? WHERE id=?");
                ps1.setString(1, patient.getNom());
                ps1.setString(2, patient.getPrenom());
                ps1.setString(3, patient.getEmail());
                ps1.setInt(4, patient.getId());
            }
            ps1.executeUpdate();
            PreparedStatement ps2 = conn.prepareStatement("UPDATE PATIENT SET date_naissance=?, adresse=?, telephone=? WHERE id=?");
            ps2.setString(1, patient.getDateNaissance());
            ps2.setString(2, patient.getAdresse());
            ps2.setString(3, patient.getTelephone());
            ps2.setInt(4, patient.getId());
            ps2.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erreur updatePatient: " + e.getMessage(), e);
        }
    }

    public static void inscrirePatient(Patient patient) throws DAOException {
        try (Connection conn = DBConnection.getConnection()) {
            // Vérifier unicité email
            PreparedStatement check = conn.prepareStatement("SELECT id FROM UTILISATEUR WHERE email = ?");
            check.setString(1, patient.getEmail());
            ResultSet rs = check.executeQuery();
            if (rs.next()) throw new DAOException("Email déjà utilisé.");
            // Insérer dans UTILISATEUR
            PreparedStatement psUser = conn.prepareStatement("INSERT INTO UTILISATEUR(nom, prenom, email, mot_de_passe, type_utilisateur) VALUES (?, ?, ?, ?, 'patient')", PreparedStatement.RETURN_GENERATED_KEYS);
            psUser.setString(1, patient.getNom());
            psUser.setString(2, patient.getPrenom());
            psUser.setString(3, patient.getEmail());
            psUser.setString(4, patient.getMotDePasse());
            psUser.executeUpdate();
            ResultSet gen = psUser.getGeneratedKeys();
            if (gen.next()) {
                int id = gen.getInt(1);
                // Insérer dans PATIENT
                PreparedStatement ps = conn.prepareStatement("INSERT INTO PATIENT(id, date_naissance, adresse, telephone) VALUES (?, ?, ?, ?)");
                ps.setInt(1, id);
                ps.setString(2, patient.getDateNaissance());
                ps.setString(3, patient.getAdresse());
                ps.setString(4, patient.getTelephone());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur inscrirePatient: " + e.getMessage(), e);
        }
    }

    public static java.util.List<Patient> getAllPatients() throws DAOException {
        java.util.List<Patient> list = new java.util.ArrayList<>();
        try (java.sql.Connection conn = DBConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement("SELECT u.id, u.nom, u.prenom, u.email, u.mot_de_passe, p.date_naissance, p.adresse, p.telephone FROM UTILISATEUR u JOIN PATIENT p ON u.id = p.id");
             java.sql.ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Patient(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getString("mot_de_passe"),
                    rs.getString("date_naissance"),
                    rs.getString("adresse"),
                    rs.getString("telephone")
                ));
            }
        } catch (java.sql.SQLException e) {
            throw new DAOException("Erreur getAllPatients: " + e.getMessage(), e);
        }
        return list;
    }

    public static java.util.List<Patient> getPatientsBySpecialiste(int specialisteId) throws DAOException {
        java.util.List<Patient> list = new java.util.ArrayList<>();
        try (java.sql.Connection conn = DBConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(
                "SELECT DISTINCT p.id, u.nom, u.prenom, u.email, u.mot_de_passe, p.date_naissance, p.adresse, p.telephone " +
                "FROM PATIENT p " +
                "JOIN UTILISATEUR u ON p.id = u.id " +
                "JOIN RENDEZ_VOUS r ON p.id = r.patient_id " +
                "WHERE r.specialiste_id = ?")
        ) {
            ps.setInt(1, specialisteId);
            java.sql.ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Patient(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getString("mot_de_passe"),
                    rs.getString("date_naissance"),
                    rs.getString("adresse"),
                    rs.getString("telephone")
                ));
            }
        } catch (java.sql.SQLException e) {
            throw new DAOException("Erreur getPatientsBySpecialiste: " + e.getMessage(), e);
        }
        return list;
    }

    public static java.util.List<Modele.Utilisateur> getAllUtilisateurs() throws DAOException {
        java.util.List<Modele.Utilisateur> list = new java.util.ArrayList<>();
        try (java.sql.Connection conn = DBConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement("SELECT id, nom, prenom, email, mot_de_passe, type_utilisateur FROM UTILISATEUR");
             java.sql.ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Modele.Utilisateur(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getString("mot_de_passe"),
                    rs.getString("type_utilisateur")
                ));
            }
        } catch (java.sql.SQLException e) {
            throw new DAOException("Erreur getAllUtilisateurs: " + e.getMessage(), e);
        }
        return list;
    }

    public static void updateUtilisateur(Modele.Utilisateur utilisateur, String colName, Object value) throws DAOException {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE UTILISATEUR SET " + colName + " = ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setObject(1, value);
                ps.setInt(2, utilisateur.getId());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur updateUtilisateur: " + e.getMessage(), e);
        }
    }
}
