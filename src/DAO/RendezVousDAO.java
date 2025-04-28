package DAO;

import Modele.Historique;
import Modele.RendezVous;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RendezVousDAO {

    public static RendezVous getProchainRendezVous(int patientId) throws DAOException {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT r.id, r.date_heure, r.statut, s.nom, s.prenom, s.specialisation " +
                            "FROM RENDEZ_VOUS r " +
                            "JOIN SPECIALISTE s ON r.specialiste_id = s.id " +
                            "WHERE r.patient_id = ? AND r.date_heure <= NOW() AND r.statut = 'confirmé' " +
                            "ORDER BY r.date_heure ASC LIMIT 1"
            );
            return getRendezVous(patientId, ps);
        } catch (SQLException e) {
            throw new DAOException("Erreur getPlusAncienRendezVous: " + e.getMessage(), e);
        }
    }

    private static RendezVous getRendezVous(int patientId, PreparedStatement ps) throws SQLException {
        ps.setInt(1, patientId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return new RendezVous(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("specialisation"),
                    rs.getString("date_heure"),
                    rs.getString("statut")
            );
        }
        return null;
    }


    public static List<RendezVous> getRendezVousPourPatient(int patientId) throws DAOException {
        List<RendezVous> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT r.id, r.date_heure, r.statut, s.nom, s.prenom, s.specialisation FROM RENDEZ_VOUS r " +
                "JOIN SPECIALISTE s ON r.specialiste_id = s.id WHERE r.patient_id = ? AND r.statut='confirmé' ORDER BY r.date_heure DESC");
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new RendezVous(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("specialisation"),
                    rs.getString("date_heure"),
                    rs.getString("statut")
                ));
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur getRendezVousPourPatient: " + e.getMessage(), e);
        }
        return list;
    }
    public static List<RendezVous> getRendezVousPourHistorique(int patientId) throws DAOException {
        List<RendezVous> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT r.id, r.date_heure, r.statut, s.nom, s.prenom, s.specialisation FROM RENDEZ_VOUS r " +
                            "JOIN SPECIALISTE s ON r.specialiste_id = s.id WHERE r.patient_id = ? AND r.statut='terminé' ORDER BY r.date_heure DESC");
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new RendezVous(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("specialisation"),
                        rs.getString("date_heure"),
                        rs.getString("statut")
                ));
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur getRendezVousPourPatient: " + e.getMessage(), e);
        }
        return list;
    }

    public static void annulerRendezVous(int rdvId) throws DAOException {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE RENDEZ_VOUS SET statut = 'annulé' WHERE id = ?");
            ps.setInt(1, rdvId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erreur annulerRendezVous: " + e.getMessage(), e);
        }
    }

    public static List<RendezVous> getAllRendezVous() throws DAOException {
        List<RendezVous> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT r.id, r.date_heure, r.statut, s.nom, s.prenom, s.specialisation FROM RENDEZ_VOUS r JOIN SPECIALISTE s ON r.specialiste_id = s.id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new RendezVous(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("specialisation"),
                    rs.getString("date_heure"),
                    rs.getString("statut")
                ));
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur getAllRendezVous: " + e.getMessage(), e);
        }
        return list;
    }

    public static void updateRendezVous(int id, String colName, Object value) throws DAOException {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE RENDEZ_VOUS SET " + colName + " = ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setObject(1, value);
                ps.setInt(2, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur updateRendezVous: " + e.getMessage(), e);
        }
    }

    public static RendezVous getProchainRendezVousPourSpecialiste(int specialisteId) throws DAOException {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT r.id,p.nom,p.prenom, r.date_heure, r.statut, s.specialisation FROM RENDEZ_VOUS r " +
                "JOIN PATIENT p ON r.patient_id = p.id " +
                "JOIN SPECIALISTE s ON r.specialiste_id = s.id " +
                "WHERE r.specialiste_id = ? AND r.date_heure > NOW() AND r.statut = 'confirmé' ORDER BY r.date_heure ASC LIMIT 1");
            return getRendezVous(specialisteId, ps);
        } catch (SQLException e) {
            throw new DAOException("Erreur getProchainRendezVousPourSpecialiste: " + e.getMessage(), e);
        }
    }

    public static List<RendezVous> getRendezVousPourSpecialiste(int specialisteId) throws DAOException {
        List<RendezVous> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT r.id, r.date_heure, r.statut, u.nom, u.prenom, s.specialisation " +
                "FROM RENDEZ_VOUS r " +
                "JOIN PATIENT p ON r.patient_id = p.id " +
                "JOIN UTILISATEUR u ON p.id = u.id " +
                "JOIN SPECIALISTE s ON r.specialiste_id = s.id " +
                "WHERE r.specialiste_id = ? ORDER BY r.date_heure DESC"
            );
            ps.setInt(1, specialisteId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new RendezVous(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("specialisation"),
                    rs.getString("date_heure"),
                    rs.getString("statut")
                ));
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur getRendezVousPourSpecialiste: " + e.getMessage(), e);
        }
        return list;
    }

    public static void terminerRendezVous(int rdvId) throws DAOException {
        try (Connection conn = DBConnection.getConnection()) {
            // Mettre à jour le statut du rendez-vous
            PreparedStatement ps = conn.prepareStatement("UPDATE RENDEZ_VOUS SET statut = 'terminé' WHERE id = ?");
            ps.setInt(1, rdvId);
            ps.executeUpdate();

            // Récupérer toutes les informations nécessaires pour l'historique
            PreparedStatement psInfo = conn.prepareStatement(
                    "SELECT r.patient_id, r.date_heure, s.nom as nom_specialiste, s.prenom as prenom_specialiste, s.specialisation " +
                            "FROM RENDEZ_VOUS r " +
                            "JOIN SPECIALISTE s ON r.specialiste_id = s.id " +
                            "WHERE r.id = ?");
            psInfo.setInt(1, rdvId);
            ResultSet rsInfo = psInfo.executeQuery();

            if (rsInfo.next()) {
                int patientId = rsInfo.getInt("patient_id");
                String dateHeure = rsInfo.getString("date_heure");
                String nomSpecialiste = rsInfo.getString("nom_specialiste");
                String prenomSpecialiste = rsInfo.getString("prenom_specialiste");
                String specialisation = rsInfo.getString("specialisation");

                // Vérifier si une entrée existe déjà dans l'historique
                PreparedStatement check = conn.prepareStatement("SELECT COUNT(*) FROM HISTORIQUE WHERE rendez_vous_id = ?");
                check.setInt(1, rdvId);
                ResultSet rs = check.executeQuery();
                boolean exists = false;
                if (rs.next()) exists = rs.getInt(1) > 0;

                if (!exists) {
                    // Insérer dans l'historique avec toutes les informations
                    PreparedStatement insert = conn.prepareStatement(
                            "INSERT INTO HISTORIQUE (patient_id, rendez_vous_id, notes, date_heure, nom_specialiste, prenom_specialiste, specialisation) " +
                                    "VALUES (?, ?, ?, ?, ?, ?, ?)");
                    insert.setInt(1, patientId);
                    insert.setInt(2, rdvId);
                    insert.setString(3, ""); // note vide initialement
                    insert.setString(4, dateHeure);
                    insert.setString(5, nomSpecialiste);
                    insert.setString(6, prenomSpecialiste);
                    insert.setString(7, specialisation);
                    insert.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur terminerRendezVous: " + e.getMessage(), e);
        }
    }

    public static void ajouterCreneauDisponibilite(int specialisteId, String dateHeure) throws DAOException {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO RENDEZ_VOUS (patient_id, specialiste_id, date_heure, statut) VALUES (?, ?, ?, 'libre')");
            ps.setNull(1, java.sql.Types.INTEGER); // patient_id = NULL
            ps.setInt(2, specialisteId);
            ps.setString(3, dateHeure);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erreur ajouterCreneauDisponibilite: " + e.getMessage(), e);
        }
    }

    public static List<RendezVous> getCreneauxDisponiblesPourSpecialiste(int specialisteId) throws DAOException {
        List<RendezVous> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT r.id, r.date_heure, r.statut, '' as nom, '' as prenom, s.specialisation FROM RENDEZ_VOUS r " +
                "JOIN SPECIALISTE s ON r.specialiste_id = s.id " +
                "WHERE r.specialiste_id = ? AND r.patient_id IS NULL ORDER BY r.date_heure DESC");
            ps.setInt(1, specialisteId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new RendezVous(
                    rs.getInt("id"),
                    rs.getString("nom"), // nom vide
                    rs.getString("prenom"), // prenom vide
                    rs.getString("specialisation"),
                    rs.getString("date_heure"),
                    rs.getString("statut")
                ));
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur getCreneauxDisponiblesPourSpecialiste: " + e.getMessage(), e);
        }
        return list;
    }

    public static void reserverCreneau(int rdvId, int patientId) throws DAOException {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE RENDEZ_VOUS SET patient_id = ?, statut = 'confirmé' WHERE id = ? AND statut = 'libre' AND patient_id IS NULL");
            ps.setInt(1, patientId);
            ps.setInt(2, rdvId);
            int updated = ps.executeUpdate();
            if (updated == 0) throw new DAOException("Ce créneau n'est plus disponible.");
        } catch (SQLException e) {
            throw new DAOException("Erreur reserverCreneau: " + e.getMessage(), e);
        }
    }

    public static void remettreCreneauLibre(int rdvId) throws DAOException {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE RENDEZ_VOUS SET patient_id = NULL, statut = 'libre' WHERE id = ?");
            ps.setInt(1, rdvId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erreur remettreCreneauLibre: " + e.getMessage(), e);
        }
    }

    public static List<RendezVous> getAllCreneauxPourSpecialiste(int specialisteId) throws DAOException {
        List<RendezVous> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT r.id, r.date_heure, r.statut, '' as nom, '' as prenom, s.specialisation FROM RENDEZ_VOUS r " +
                "JOIN SPECIALISTE s ON r.specialiste_id = s.id " +
                "WHERE r.specialiste_id = ? ORDER BY r.date_heure DESC");
            ps.setInt(1, specialisteId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new RendezVous(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("specialisation"),
                    rs.getString("date_heure"),
                    rs.getString("statut")
                ));
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur getAllCreneauxPourSpecialiste: " + e.getMessage(), e);
        }
        return list;
    }


}
