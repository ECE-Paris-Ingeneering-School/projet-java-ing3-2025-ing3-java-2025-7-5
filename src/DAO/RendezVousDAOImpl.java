package DAO;

import Modele.Patient;
import Modele.RendezVous;
import Modele.Specialiste;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RendezVousDAOImpl implements RendezVousDAO {

    private static final String CREATE_SQL =
            "INSERT INTO RENDEZ_VOUS (patient_id, specialiste_id, date_heure, statut) VALUES (?, ?, ?, ?)";

    // On joint désormais PATIENT → UTILISATEUR pour récupérer nom, prénom, email, mot_de_passe
    private static final String FIND_BY_ID_SQL =
            "SELECT " +
                    "  r.id           AS rdv_id, " +
                    "  r.patient_id, " +
                    "  r.specialiste_id, " +
                    "  r.date_heure, " +
                    "  r.statut, " +
                    "  p.date_naissance, " +
                    "  p.adresse, " +
                    "  p.telephone      AS pat_tel, " +
                    "  u.nom            AS pat_nom, " +
                    "  u.prenom         AS pat_prenom, " +
                    "  u.email          AS pat_email, " +
                    "  u.mot_de_passe   AS pat_mdp, " +
                    "  s.id             AS spec_id, " +
                    "  s.nom            AS spec_nom, " +
                    "  s.prenom         AS spec_prenom, " +
                    "  s.specialisation, " +
                    "  s.telephone      AS spec_tel, " +
                    "  s.email          AS spec_email " +
                    "FROM RENDEZ_VOUS r " +
                    "JOIN PATIENT p ON r.patient_id = p.id " +
                    "JOIN UTILISATEUR u ON p.id = u.id " +
                    "JOIN SPECIALISTE s ON r.specialiste_id = s.id " +
                    "WHERE r.id = ?";

    private static final String FIND_ALL_SQL =
            "SELECT " +
                    "  r.id           AS rdv_id, " +
                    "  r.patient_id, " +
                    "  r.specialiste_id, " +
                    "  r.date_heure, " +
                    "  r.statut, " +
                    "  p.date_naissance, " +
                    "  p.adresse, " +
                    "  p.telephone      AS pat_tel, " +
                    "  u.nom            AS pat_nom, " +
                    "  u.prenom         AS pat_prenom, " +
                    "  u.email          AS pat_email, " +
                    "  u.mot_de_passe   AS pat_mdp, " +
                    "  s.id             AS spec_id, " +
                    "  s.nom            AS spec_nom, " +
                    "  s.prenom         AS spec_prenom, " +
                    "  s.specialisation, " +
                    "  s.telephone      AS spec_tel, " +
                    "  s.email          AS spec_email " +
                    "FROM RENDEZ_VOUS r " +
                    "JOIN PATIENT p ON r.patient_id = p.id " +
                    "JOIN UTILISATEUR u ON p.id = u.id " +
                    "JOIN SPECIALISTE s ON r.specialiste_id = s.id";

    private static final String UPDATE_SQL =
            "UPDATE RENDEZ_VOUS SET patient_id=?, specialiste_id=?, date_heure=?, statut=? WHERE id=?";

    private static final String DELETE_SQL =
            "DELETE FROM RENDEZ_VOUS WHERE id=?";

    private static final String FIND_BY_PATIENT_SQL =
            FIND_ALL_SQL + " WHERE r.patient_id = ?";

    private static final String FIND_BY_SPECIALISTE_SQL =
            FIND_ALL_SQL + " WHERE r.specialiste_id = ?";

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
        List<RendezVous> list = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(FIND_ALL_SQL)) {

            while (rs.next()) {
                list.add(mapRendezVous(rs));
            }
            return list;
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
        List<RendezVous> list = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(FIND_BY_PATIENT_SQL)) {

            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRendezVous(rs));
                }
                return list;
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur recherche par patient : " + e.getMessage());
        }
    }

    @Override
    public List<RendezVous> findBySpecialiste(int specialisteId) throws DAOException {
        List<RendezVous> list = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(FIND_BY_SPECIALISTE_SQL)) {

            ps.setInt(1, specialisteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRendezVous(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DAOException("Erreur recherche par spécialiste : " + e.getMessage());
        }
    }

    /**
     * Construit un objet RendezVous complet à partir du ResultSet,
     * en incluant patient & spécialiste.
     */
    private RendezVous mapRendezVous(ResultSet rs) throws SQLException {
        // Données rendez-vous
        int rdvId      = rs.getInt("rdv_id");
        LocalDateTime dateHeure = rs.getTimestamp("date_heure").toLocalDateTime();
        String statut  = rs.getString("statut");

        // Données patient
        int patId      = rs.getInt("patient_id");
        String patNom  = rs.getString("pat_nom");
        String patPren = rs.getString("pat_prenom");
        String patEmail= rs.getString("pat_email");
        String patMdp  = rs.getString("pat_mdp");
        LocalDate patNaiss = rs.getDate("date_naissance").toLocalDate();
        String patAddr = rs.getString("adresse");
        String patTel  = rs.getString("pat_tel");

        Patient patient = new Patient(
                patId, patNom, patPren, patEmail, patMdp,
                patNaiss, patAddr, patTel
        );

        // Données spécialiste
        int specId     = rs.getInt("spec_id");
        String specNom = rs.getString("spec_nom");
        String specPre = rs.getString("spec_prenom");
        String specSpe = rs.getString("specialisation");
        String specTel2= rs.getString("spec_tel");
        String specMail= rs.getString("spec_email");

        Specialiste specialiste = new Specialiste(
                specId, specNom, specPre, specSpe, specTel2, specMail
        );

        return new RendezVous(rdvId, patient, specialiste, dateHeure, statut);
    }
}
