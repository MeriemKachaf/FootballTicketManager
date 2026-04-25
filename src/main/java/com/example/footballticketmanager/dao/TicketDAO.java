package com.example.footballticketmanager.dao;

import com.example.footballticketmanager.database.DatabaseConnection;
import com.example.footballticketmanager.model.Ticket;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO {

    public List<Ticket> getAllTickets() {
        List<Ticket> tickets = new ArrayList<>();

        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM ticket";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                Ticket ticket = new Ticket(
                        rs.getInt("id"),
                        rs.getInt("match_id"),
                        rs.getDouble("prix"),
                        rs.getString("categorie")
                );
                tickets.add(ticket);
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
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM ticket WHERE match_id = ?");
            ps.setInt(1, matchId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tickets.add(new Ticket(rs.getInt("id"), rs.getInt("match_id"),
                    rs.getDouble("prix"), rs.getString("categorie")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tickets;
    }

    public List<String> getAllTicketsAvecMatch() {
        List<String> items = new ArrayList<>();
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return items;
            String sql =
                "SELECT t.id, t.categorie, t.prix, " +
                "m.equipe_domicile, m.equipe_exterieure, m.date_match " +
                "FROM ticket t " +
                "JOIN match_football m ON t.match_id = m.id";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                items.add(
                    rs.getInt("id") + " - " +
                    rs.getString("equipe_domicile") + " vs " + rs.getString("equipe_exterieure") +
                    " (" + rs.getDate("date_match") + ")" +
                    " - " + rs.getString("categorie") +
                    " - " + rs.getDouble("prix") + "€"
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }
}