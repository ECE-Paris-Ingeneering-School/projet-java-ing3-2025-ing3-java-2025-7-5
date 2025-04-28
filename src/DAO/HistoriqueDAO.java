package DAO;

import Modele.Historique;
import Modele.RendezVous;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HistoriqueDAO {
    public static void ajouterNote(int patientId, int rendezVousId, String notes) throws DAOException {
        try (Connection conn = DBConnection.getConnection()) {
            // Vérifier si une ligne existe déjà pour ce rendez-vous
            PreparedStatement check = conn.prepareStatement("SELECT id FROM HISTORIQUE WHERE rendez_vous_id = ?");
            check.setInt(1, rendezVousId);
            ResultSet rs = check.executeQuery();
            if (rs.next()) {
                // Si existe, faire un UPDATE
                int histoId = rs.getInt("id");
                PreparedStatement update = conn.prepareStatement("UPDATE HISTORIQUE SET notes = ? WHERE id = ?");
                update.setString(1, notes);
                update.setInt(2, histoId);
                update.executeUpdate();
            } else {
                // Sinon, faire un INSERT
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO HISTORIQUE(patient_id, rendez_vous_id, notes) VALUES (?, ?, ?)");
                ps.setInt(1, patientId);
                ps.setInt(2, rendezVousId);
                ps.setString(3, notes);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur HistoriqueDAO.ajouterNote: " + e.getMessage(), e);
        }
    }

    public static List<Historique> getHistoriquesByPatient(int patientId) throws DAOException {
        List<Historique> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT h.id, h.patient_id, h.rendez_vous_id, h.notes, " +
                            "DATE_FORMAT(r.date_heure, '%d/%m/%Y %H:%i') AS date_heure_formatee, " +
                            "r.statut, s.nom as specialiste_nom, s.prenom as specialiste_prenom, s.specialisation " +
                            "FROM HISTORIQUE h " +
                            "JOIN RENDEZ_VOUS r ON h.rendez_vous_id = r.id " +
                            "JOIN SPECIALISTE s ON r.specialiste_id = s.id " +
                            "WHERE h.patient_id = ? ORDER BY r.date_heure DESC");
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Historique histo = new Historique(
                        rs.getInt("id"),
                        rs.getInt("patient_id"),
                        rs.getInt("rendez_vous_id"),
                        rs.getString("notes")
                );
                histo.setDateHeure(rs.getString("date_heure_formatee"));
                histo.setStatut(rs.getString("statut"));
                histo.setSpecialisteNom(rs.getString("specialiste_nom"));
                histo.setSpecialistePrenom(rs.getString("specialiste_prenom"));
                histo.setSpecialisation(rs.getString("specialisation"));
                list.add(histo);
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la récupération de l'historique", e);
        }
        return list;
    }

    public static Historique getHistoriqueByRendezVous(int rendezVousId) throws DAOException {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT id, patient_id, rendez_vous_id, notes FROM HISTORIQUE WHERE rendez_vous_id = ?");
            ps.setInt(1, rendezVousId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Historique(
                        rs.getInt("id"),
                        rs.getInt("patient_id"),
                        rs.getInt("rendez_vous_id"),
                        rs.getString("notes")
                );
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur HistoriqueDAO.getHistoriqueByRendezVous: " + e.getMessage(), e);
        }
        return null;
    }

    public static List<Historique> getAllHistoriques() throws DAOException {
        List<Historique> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id, patient_id, rendez_vous_id, notes FROM HISTORIQUE");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Historique(
                        rs.getInt("id"),
                        rs.getInt("patient_id"),
                        rs.getInt("rendez_vous_id"),
                        rs.getString("notes")
                ));
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur getAllHistoriques: " + e.getMessage(), e);
        }
        return list;
    }

    public static void updateHistorique(int id, String colName, Object value) throws DAOException {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE HISTORIQUE SET " + colName + " = ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setObject(1, value);
                ps.setInt(2, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur updateHistorique: " + e.getMessage(), e);
        }
    }

    public static List<Historique> getHistoriqueCompletByPatient(int patientId) throws DAOException {
        List<Historique> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT h.id, h.patient_id, h.rendez_vous_id, h.notes, " +
                            "r.date_heure, s.nom as specialiste_nom, s.prenom as specialiste_prenom, s.specialisation " +
                            "FROM HISTORIQUE h " +
                            "JOIN RENDEZ_VOUS r ON h.rendez_vous_id = r.id " +
                            "JOIN SPECIALISTE s ON r.specialiste_id = s.id " +
                            "WHERE h.patient_id = ? AND r.statut='terminé'" );
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Historique histo = new Historique(
                        rs.getInt("id"),
                        rs.getInt("patient_id"),
                        rs.getInt("rendez_vous_id"),
                        rs.getString("notes")
                );
                // Ajout des infos supplémentaires
                histo.setDateHeure(rs.getString("date_heure"));
                histo.setSpecialisteNom(rs.getString("specialiste_nom"));
                histo.setSpecialistePrenom(rs.getString("specialiste_prenom"));
                histo.setSpecialisation(rs.getString("specialisation"));
                list.add(histo);
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la récupération de l'historique", e);
        }
        return list;
    }
}
