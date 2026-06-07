package com.example.footballticketmanager.dao;

import com.example.footballticketmanager.database.DatabaseConnection;
import com.example.footballticketmanager.model.Reservation;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReservationDAO {

    public List<Reservation> getAllReservations() {
        List<Reservation> list = new ArrayList<>();
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return list;
            String sql =
                "SELECT r.id, r.utilisateur_id, r.ticket_id, r.quantite, r.date_reservation, " +
                "CONCAT(u.prenom, ' ', u.nom) AS utilisateur_nom, " +
                "CONCAT(m.equipe_domicile, ' vs ', m.equipe_exterieure, " +
                "       ' - ', t.categorie, ' - ', t.prix, '€') AS ticket_info, " +
                "COALESCE(p.statut, 'en_attente') AS statut_paiement " +
                "FROM reservation r " +
                "JOIN utilisateur u ON r.utilisateur_id = u.id " +
                "JOIN ticket t ON r.ticket_id = t.id " +
                "JOIN match_football m ON t.match_id = m.id " +
                "LEFT JOIN paiement p ON p.reservation_id = r.id";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                list.add(new Reservation(
                    rs.getInt("id"),
                    rs.getInt("utilisateur_id"),
                    rs.getInt("ticket_id"),
                    rs.getInt("quantite"),
                    rs.getDate("date_reservation"),
                    rs.getString("utilisateur_nom"),
                    rs.getString("ticket_info"),
                    rs.getString("statut_paiement")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Reservation> getReservationsByUtilisateurId(int utilisateurId) {
        List<Reservation> list = new ArrayList<>();
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return list;
            String sql =
                "SELECT r.id, r.utilisateur_id, r.ticket_id, r.quantite, r.date_reservation, " +
                "CONCAT(u.prenom, ' ', u.nom) AS utilisateur_nom, " +
                "CONCAT(m.equipe_domicile, ' vs ', m.equipe_exterieure, " +
                "       ' - ', t.categorie, ' - ', t.prix, '€') AS ticket_info, " +
                "COALESCE(p.statut, 'en_attente') AS statut_paiement " +
                "FROM reservation r " +
                "JOIN utilisateur u ON r.utilisateur_id = u.id " +
                "JOIN ticket t ON r.ticket_id = t.id " +
                "JOIN match_football m ON t.match_id = m.id " +
                "LEFT JOIN paiement p ON p.reservation_id = r.id " +
                "WHERE r.utilisateur_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, utilisateurId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Reservation(
                    rs.getInt("id"), rs.getInt("utilisateur_id"), rs.getInt("ticket_id"),
                    rs.getInt("quantite"), rs.getDate("date_reservation"),
                    rs.getString("utilisateur_nom"), rs.getString("ticket_info"),
                    rs.getString("statut_paiement")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Map<String, Integer> getBilletsParMatch() {
        Map<String, Integer> map = new LinkedHashMap<>();
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return map;
            ResultSet rs = conn.createStatement().executeQuery(
                "SELECT CONCAT(m.equipe_domicile, ' vs ', m.equipe_exterieure) AS nom_match, " +
                "COALESCE(SUM(r.quantite), 0) AS total " +
                "FROM match_football m " +
                "LEFT JOIN ticket t ON t.match_id = m.id " +
                "LEFT JOIN reservation r ON r.ticket_id = t.id " +
                "GROUP BY m.id, m.equipe_domicile, m.equipe_exterieure " +
                "ORDER BY total DESC"
            );
            while (rs.next()) map.put(rs.getString("nom_match"), rs.getInt("total"));
        } catch (Exception e) { e.printStackTrace(); }
        return map;
    }

    public boolean existeDeja(int utilisateurId, int ticketId) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return false;
            PreparedStatement ps = conn.prepareStatement(
                "SELECT id FROM reservation WHERE utilisateur_id = ? AND ticket_id = ?"
            );
            ps.setInt(1, utilisateurId);
            ps.setInt(2, ticketId);
            return ps.executeQuery().next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getTotalParMatch(int utilisateurId, int matchId) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return 0;
            PreparedStatement ps = conn.prepareStatement(
                "SELECT COALESCE(SUM(r.quantite), 0) AS total " +
                "FROM reservation r " +
                "JOIN ticket t ON r.ticket_id = t.id " +
                "WHERE r.utilisateur_id = ? AND t.match_id = ?"
            );
            ps.setInt(1, utilisateurId);
            ps.setInt(2, matchId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("total");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean ajouter(Reservation r) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return false;
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO reservation (utilisateur_id, ticket_id, quantite, date_reservation) VALUES (?, ?, ?, ?)"
            );
            ps.setInt(1, r.getUtilisateurId());
            ps.setInt(2, r.getTicketId());
            ps.setInt(3, r.getQuantite());
            ps.setDate(4, r.getDateReservation());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean supprimer(int id) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return false;
            PreparedStatement ps = conn.prepareStatement("DELETE FROM reservation WHERE id = ?");
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
