package com.example.footballticketmanager.dao;

import com.example.footballticketmanager.database.DatabaseConnection;
import com.example.footballticketmanager.model.Supporter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SupporterDAO {

    public List<Supporter> getAllSupporters() {
        List<Supporter> supporters = new ArrayList<>();

        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM supporter";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                Supporter supporter = new Supporter(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("email"),
                        rs.getString("equipe_favorite")
                );
                supporters.add(supporter);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return supporters;
    }

    public void addSupporter(Supporter supporter) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO supporter (nom, email, equipe_favorite) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, supporter.getNom());
            ps.setString(2, supporter.getEmail());
            ps.setString(3, supporter.getEquipeFavorite());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean updateSupporter(Supporter supporter) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "UPDATE supporter SET nom = ?, email = ?, equipe_favorite = ? WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, supporter.getNom());
            ps.setString(2, supporter.getEmail());
            ps.setString(3, supporter.getEquipeFavorite());
            ps.setInt(4, supporter.getId());

            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public Supporter findByEmail(String email) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return null;
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM supporter WHERE email = ?");
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Supporter(rs.getInt("id"), rs.getString("nom"),
                    rs.getString("email"), rs.getString("equipe_favorite"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteSupporter(int id) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "DELETE FROM supporter WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}