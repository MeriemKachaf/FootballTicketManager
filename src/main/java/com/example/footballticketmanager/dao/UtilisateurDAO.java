package com.example.footballticketmanager.dao;

import com.example.footballticketmanager.database.DatabaseConnection;
import com.example.footballticketmanager.model.Utilisateur;
import com.example.footballticketmanager.util.PasswordUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDAO {

    public Utilisateur authentifier(String email, String motDePasse) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return null;
            PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM utilisateur WHERE email = ?"
            );
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String hashStocke = rs.getString("mot_de_passe");
                if (!PasswordUtils.verifier(motDePasse, hashStocke)) return null;
                return new Utilisateur(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    hashStocke,
                    rs.getString("role"),
                    rs.getString("equipe_favorite")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean emailExiste(String email) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return false;
            PreparedStatement ps = conn.prepareStatement(
                "SELECT id FROM utilisateur WHERE email = ?"
            );
            ps.setString(1, email);
            return ps.executeQuery().next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean ajouter(Utilisateur u) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return false;
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, role, equipe_favorite) VALUES (?, ?, ?, ?, ?, ?)"
            );
            ps.setString(1, u.getNom());
            ps.setString(2, u.getPrenom());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getMotDePasse());
            ps.setString(5, u.getRole());
            ps.setString(6, u.getEquipeFavorite());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Utilisateur> getAllUsers() {
        List<Utilisateur> list = new ArrayList<>();
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return list;
            ResultSet rs = conn.createStatement().executeQuery(
                "SELECT * FROM utilisateur WHERE role = 'user' ORDER BY nom"
            );
            while (rs.next()) {
                list.add(new Utilisateur(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getString("mot_de_passe"),
                    rs.getString("role"),
                    rs.getString("equipe_favorite")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countUsers() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return 0;
            ResultSet rs = conn.createStatement().executeQuery(
                "SELECT COUNT(*) AS nb FROM utilisateur WHERE role = 'user'"
            );
            if (rs.next()) return rs.getInt("nb");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean updateProfil(int id, String nom, String prenom, String equipe) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return false;
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE utilisateur SET nom = ?, prenom = ?, equipe_favorite = ? WHERE id = ?"
            );
            ps.setString(1, nom);
            ps.setString(2, prenom);
            ps.setString(3, equipe);
            ps.setInt(4, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateUtilisateur(int id, String nom, String email, String equipe) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return false;
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE utilisateur SET nom = ?, email = ?, equipe_favorite = ? WHERE id = ?"
            );
            ps.setString(1, nom);
            ps.setString(2, email);
            ps.setString(3, equipe);
            ps.setInt(4, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateMotDePasse(int id, String nouveauHash) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return false;
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE utilisateur SET mot_de_passe = ? WHERE id = ?"
            );
            ps.setString(1, nouveauHash);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteUtilisateur(int id) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return false;
            PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM utilisateur WHERE id = ?"
            );
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
