package com.example.footballticketmanager.dao;

import com.example.footballticketmanager.database.DatabaseConnection;
import com.example.footballticketmanager.model.Reservation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    public List<Reservation> getAllReservations() {
        List<Reservation> list = new ArrayList<>();
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return list;
            String sql =
                "SELECT r.id, r.supporter_id, r.ticket_id, r.date_reservation, " +
                "s.nom AS supporter_nom, " +
                "CONCAT(m.equipe_domicile, ' vs ', m.equipe_exterieure, " +
                "       ' - ', t.categorie, ' - ', t.prix, '€') AS ticket_info " +
                "FROM reservation r " +
                "JOIN supporter s ON r.supporter_id = s.id " +
                "JOIN ticket t ON r.ticket_id = t.id " +
                "JOIN match_football m ON t.match_id = m.id";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                list.add(new Reservation(
                    rs.getInt("id"),
                    rs.getInt("supporter_id"),
                    rs.getInt("ticket_id"),
                    rs.getDate("date_reservation"),
                    rs.getString("supporter_nom"),
                    rs.getString("ticket_info")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Reservation> getReservationsBySupporterId(int supporterId) {
        List<Reservation> list = new ArrayList<>();
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return list;
            String sql =
                "SELECT r.id, r.supporter_id, r.ticket_id, r.date_reservation, " +
                "s.nom AS supporter_nom, " +
                "CONCAT(m.equipe_domicile, ' vs ', m.equipe_exterieure, " +
                "       ' - ', t.categorie, ' - ', t.prix, '€') AS ticket_info " +
                "FROM reservation r " +
                "JOIN supporter s ON r.supporter_id = s.id " +
                "JOIN ticket t ON r.ticket_id = t.id " +
                "JOIN match_football m ON t.match_id = m.id " +
                "WHERE r.supporter_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, supporterId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Reservation(
                    rs.getInt("id"), rs.getInt("supporter_id"), rs.getInt("ticket_id"),
                    rs.getDate("date_reservation"), rs.getString("supporter_nom"), rs.getString("ticket_info")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean ajouter(Reservation r) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return false;
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO reservation (supporter_id, ticket_id, date_reservation) VALUES (?, ?, ?)"
            );
            ps.setInt(1, r.getSupporterId());
            ps.setInt(2, r.getTicketId());
            ps.setDate(3, r.getDateReservation());
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
