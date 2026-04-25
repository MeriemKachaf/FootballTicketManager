package com.example.footballticketmanager.dao;

import com.example.footballticketmanager.database.DatabaseConnection;
import com.example.footballticketmanager.model.Stade;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StadeDAO {

    public List<Stade> getAllStades() {
        List<Stade> list = new ArrayList<>();
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return list;
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM stade");
            while (rs.next()) {
                list.add(new Stade(rs.getInt("id"), rs.getString("nom"),
                    rs.getString("ville"), rs.getInt("capacite")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean ajouter(Stade s) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return false;
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO stade (nom, ville, capacite) VALUES (?, ?, ?)"
            );
            ps.setString(1, s.getNom());
            ps.setString(2, s.getVille());
            ps.setInt(3, s.getCapacite());
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
            PreparedStatement ps = conn.prepareStatement("DELETE FROM stade WHERE id = ?");
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
