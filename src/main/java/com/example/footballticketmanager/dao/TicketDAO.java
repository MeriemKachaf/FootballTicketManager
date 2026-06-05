package com.example.footballticketmanager.dao;

import com.example.footballticketmanager.database.DatabaseConnection;
import com.example.footballticketmanager.model.Ticket;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO {

    public List<Ticket> getAllTickets() {
        List<Ticket> tickets = new ArrayList<>();
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return tickets;
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM ticket");
            while (rs.next()) {
                tickets.add(new Ticket(
                    rs.getInt("id"), rs.getInt("match_id"),
                    rs.getDouble("prix"), rs.getString("categorie"),
                    rs.getString("zone"), rs.getInt("quantite")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tickets;
    }

    public List<Ticket> getTicketsByMatchId(int matchId) {
        List<Ticket> tickets = new ArrayList<>();
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return tickets;
            PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM ticket WHERE match_id = ?"
            );
            ps.setInt(1, matchId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tickets.add(new Ticket(
                    rs.getInt("id"), rs.getInt("match_id"),
                    rs.getDouble("prix"), rs.getString("categorie"),
                    rs.getString("zone"), rs.getInt("quantite")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tickets;
    }

    public int getDisponible(int ticketId) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return 0;
            PreparedStatement ps = conn.prepareStatement(
                "SELECT t.quantite - COUNT(r.id) AS disponible " +
                "FROM ticket t LEFT JOIN reservation r ON r.ticket_id = t.id " +
                "WHERE t.id = ? GROUP BY t.quantite"
            );
            ps.setInt(1, ticketId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Math.max(0, rs.getInt("disponible"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean addTicket(Ticket t) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return false;
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO ticket (match_id, prix, categorie, zone, quantite) VALUES (?, ?, ?, ?, ?)"
            );
            ps.setInt(1, t.getMatchId());
            ps.setDouble(2, t.getPrix());
            ps.setString(3, t.getCategorie());
            ps.setString(4, t.getZone() != null ? t.getZone() : "");
            ps.setInt(5, t.getQuantite());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteTicket(int id) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return false;
            PreparedStatement ps = conn.prepareStatement("DELETE FROM ticket WHERE id = ?");
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> getAllTicketsAvecMatch() {
        List<String> items = new ArrayList<>();
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return items;
            String sql =
                "SELECT t.id, t.categorie, t.zone, t.prix, " +
                "m.equipe_domicile, m.equipe_exterieure, m.date_match " +
                "FROM ticket t JOIN match_football m ON t.match_id = m.id";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                String zone = rs.getString("zone");
                items.add(
                    rs.getInt("id") + " - " +
                    rs.getString("equipe_domicile") + " vs " + rs.getString("equipe_exterieure") +
                    " (" + rs.getDate("date_match") + ")" +
                    " | " + rs.getString("categorie") +
                    (zone != null && !zone.isEmpty() ? " — " + zone : "") +
                    " — " + rs.getDouble("prix") + "€"
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }
}
