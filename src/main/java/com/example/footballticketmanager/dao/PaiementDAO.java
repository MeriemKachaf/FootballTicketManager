package com.example.footballticketmanager.dao;

import com.example.footballticketmanager.database.DatabaseConnection;
import com.example.footballticketmanager.model.Paiement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaiementDAO {

    public List<Paiement> getAllPaiements() {
        List<Paiement> list = new ArrayList<>();
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return list;
            String sql =
                "SELECT p.id, p.reservation_id, p.montant, p.date_paiement, p.mode_paiement, p.statut, " +
                "s.nom AS supporter_nom, " +
                "CONCAT(m.equipe_domicile, ' vs ', m.equipe_exterieure, ' - ', t.categorie) AS ticket_info " +
                "FROM paiement p " +
                "JOIN reservation r ON p.reservation_id = r.id " +
                "JOIN supporter s ON r.supporter_id = s.id " +
                "JOIN ticket t ON r.ticket_id = t.id " +
                "JOIN match_football m ON t.match_id = m.id " +
                "ORDER BY p.id DESC";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                list.add(new Paiement(
                    rs.getInt("id"), rs.getInt("reservation_id"),
                    rs.getDouble("montant"), rs.getDate("date_paiement"),
                    rs.getString("mode_paiement"), rs.getString("statut"),
                    rs.getString("supporter_nom"), rs.getString("ticket_info")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean ajouter(Paiement p) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return false;
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO paiement (reservation_id, montant, date_paiement, mode_paiement, statut) VALUES (?, ?, ?, ?, ?)"
            );
            ps.setInt(1, p.getReservationId());
            ps.setDouble(2, p.getMontant());
            ps.setDate(3, p.getDatePaiement());
            ps.setString(4, p.getModePaiement());
            ps.setString(5, p.getStatut());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public double getTotalRevenu() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return 0;
            ResultSet rs = conn.createStatement().executeQuery(
                "SELECT COALESCE(SUM(montant), 0) AS total FROM paiement WHERE statut = 'paye'"
            );
            if (rs.next()) return rs.getDouble("total");
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    public int countEnAttente() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return 0;
            ResultSet rs = conn.createStatement().executeQuery(
                "SELECT COUNT(*) AS nb FROM paiement WHERE statut = 'en_attente'"
            );
            if (rs.next()) return rs.getInt("nb");
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    public boolean updateStatut(int id, String statut) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return false;
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE paiement SET statut = ? WHERE id = ?"
            );
            ps.setString(1, statut);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
