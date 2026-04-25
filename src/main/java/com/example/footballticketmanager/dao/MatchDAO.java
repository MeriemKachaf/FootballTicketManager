package com.example.footballticketmanager.dao;

import com.example.footballticketmanager.database.DatabaseConnection;
import com.example.footballticketmanager.model.MatchFootball;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MatchDAO {

    public List<MatchFootball> getAllMatches() {
        List<MatchFootball> matches = new ArrayList<>();
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return matches;
            String sql =
                "SELECT m.id, m.equipe_domicile, m.equipe_exterieure, m.stade_id, s.nom AS stade_nom, m.date_match " +
                "FROM match_football m JOIN stade s ON m.stade_id = s.id";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                matches.add(new MatchFootball(
                    rs.getInt("id"),
                    rs.getString("equipe_domicile"),
                    rs.getString("equipe_exterieure"),
                    rs.getInt("stade_id"),
                    rs.getString("stade_nom"),
                    rs.getDate("date_match")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return matches;
    }

    public void addMatch(MatchFootball match) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return;
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO match_football (equipe_domicile, equipe_exterieure, stade_id, date_match) VALUES (?, ?, ?, ?)"
            );
            ps.setString(1, match.getEquipeDomicile());
            ps.setString(2, match.getEquipeExterieure());
            ps.setInt(3, match.getStadeId());
            ps.setDate(4, match.getDateMatch());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean updateMatch(MatchFootball match) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return false;
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE match_football SET equipe_domicile=?, equipe_exterieure=?, stade_id=?, date_match=? WHERE id=?"
            );
            ps.setString(1, match.getEquipeDomicile());
            ps.setString(2, match.getEquipeExterieure());
            ps.setInt(3, match.getStadeId());
            ps.setDate(4, match.getDateMatch());
            ps.setInt(5, match.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void deleteMatch(int id) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return;
            PreparedStatement ps = conn.prepareStatement("DELETE FROM match_football WHERE id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
