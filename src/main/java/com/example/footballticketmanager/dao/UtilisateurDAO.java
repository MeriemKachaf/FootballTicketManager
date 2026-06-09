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

    /** Insère l'utilisateur et retourne son nouvel ID, ou -1 en cas d'échec. */
    public int ajouter(Utilisateur u) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return -1;
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, role, equipe_favorite) VALUES (?, ?, ?, ?, ?, ?)"
            );
            ps.setString(1, u.getNom());
            ps.setString(2, u.getPrenom());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getMotDePasse());
            ps.setString(5, u.getRole());
            ps.setString(6, u.getEquipeFavorite());
            if (ps.executeUpdate() == 0) return -1;
            PreparedStatement sel = conn.prepareStatement("SELECT id FROM utilisateur WHERE email = ?");
            sel.setString(1, u.getEmail());
            ResultSet rs = sel.executeQuery();
            return rs.next() ? rs.getInt(1) : -1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /** Retourne les hashes des 3 derniers mots de passe de l'utilisateur. */
    public List<String> getHistoriqueMotsDePasse(int utilisateurId) {
        List<String> hashes = new ArrayList<>();
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return hashes;
            PreparedStatement ps = conn.prepareStatement(
                "SELECT mot_de_passe FROM historique_mot_de_passe " +
                "WHERE utilisateur_id = ? ORDER BY date_modification DESC LIMIT 3"
            );
            ps.setInt(1, utilisateurId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) hashes.add(rs.getString("mot_de_passe"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hashes;
    }

    /** Vérifie si le mot de passe en clair correspond à l'un des 3 derniers hashes. */
    public boolean motDePasseDejauUtilise(int utilisateurId, String motDePasse) {
        return getHistoriqueMotsDePasse(utilisateurId).stream()
            .anyMatch(hash -> PasswordUtils.verifier(motDePasse, hash));
    }

    /** Sauvegarde un hash dans l'historique et conserve uniquement les 3 dernières entrées. */
    public void sauvegarderHistorique(int utilisateurId, String hash) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return;
            PreparedStatement insert = conn.prepareStatement(
                "INSERT INTO historique_mot_de_passe (utilisateur_id, mot_de_passe) VALUES (?, ?)"
            );
            insert.setInt(1, utilisateurId);
            insert.setString(2, hash);
            insert.executeUpdate();

            // Supprimer les entrées au-delà des 3 plus récentes
            PreparedStatement delete = conn.prepareStatement(
                "DELETE FROM historique_mot_de_passe WHERE utilisateur_id = ? " +
                "AND id NOT IN (" +
                "  SELECT id FROM (SELECT id FROM historique_mot_de_passe " +
                "  WHERE utilisateur_id = ? ORDER BY date_modification DESC LIMIT 3) AS recents)"
            );
            delete.setInt(1, utilisateurId);
            delete.setInt(2, utilisateurId);
            delete.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    /**
     * Met à jour le mot de passe et sauvegarde l'ancien dans l'historique.
     * L'appelant doit vérifier motDePasseDejauUtilise() avant d'appeler cette méthode.
     */
    public boolean updateMotDePasse(int id, String nouveauHash) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return false;
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE utilisateur SET mot_de_passe = ? WHERE id = ?"
            );
            ps.setString(1, nouveauHash);
            ps.setInt(2, id);
            boolean ok = ps.executeUpdate() > 0;
            if (ok) sauvegarderHistorique(id, nouveauHash);
            return ok;
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
