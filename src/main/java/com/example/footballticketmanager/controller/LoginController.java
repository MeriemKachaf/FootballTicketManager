package com.example.footballticketmanager.controller;

import com.example.footballticketmanager.HelloApplication;
import com.example.footballticketmanager.dao.UtilisateurDAO;
import com.example.footballticketmanager.model.Utilisateur;
import com.example.footballticketmanager.session.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.HashMap;
import java.util.Map;

public class LoginController {

    private static final int MAX_TENTATIVES = 3;
    private static final long DUREE_BLOCAGE_MS = 2 * 60 * 1000; // 2 minutes

    private static final Map<String, Integer> tentatives = new HashMap<>();
    private static final Map<String, Long> blocages     = new HashMap<>();

    @FXML private TextField emailField;
    @FXML private PasswordField motDePasseField;
    @FXML private Label messageLabel;

    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    @FXML
    public void seConnecter() {
        String email = emailField.getText().trim().toLowerCase();
        String motDePasse = motDePasseField.getText();

        if (email.isEmpty() || motDePasse.isEmpty()) {
            afficherMessage("Veuillez remplir tous les champs.", false);
            return;
        }

        // Vérification blocage
        if (blocages.containsKey(email)) {
            long restant = (blocages.get(email) + DUREE_BLOCAGE_MS) - System.currentTimeMillis();
            if (restant > 0) {
                long secondes = restant / 1000;
                afficherMessage("Compte bloqué. Réessayez dans " + secondes + " secondes.", false);
                return;
            } else {
                blocages.remove(email);
                tentatives.remove(email);
            }
        }

        Utilisateur utilisateur = utilisateurDAO.authentifier(email, motDePasse);

        if (utilisateur == null) {
            int nb = tentatives.getOrDefault(email, 0) + 1;
            tentatives.put(email, nb);

            int restantes = MAX_TENTATIVES - nb;
            if (restantes <= 0) {
                blocages.put(email, System.currentTimeMillis());
                tentatives.remove(email);
                afficherMessage("Trop de tentatives. Compte bloqué 2 minutes.", false);
            } else {
                afficherMessage("Email ou mot de passe incorrect. (" + restantes + " tentative(s) restante(s))", false);
            }
            return;
        }

        tentatives.remove(email);
        blocages.remove(email);
        Session.connecter(utilisateur);

        try {
            HelloApplication.changerScene(emailField, "main-view.fxml", "Football Ticket Manager", 1150, 720);
        } catch (Exception e) {
            e.printStackTrace();
            afficherMessage("Erreur lors du chargement de l'application.", false);
        }
    }

    @FXML
    public void allerInscription() {
        try {
            HelloApplication.changerScene(emailField, "inscription-view.fxml", "Créer un compte", 480, 560);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void afficherMessage(String message, boolean succes) {
        messageLabel.setText(message);
        messageLabel.getStyleClass().removeAll("error-label", "success-label");
        messageLabel.getStyleClass().add(succes ? "success-label" : "error-label");
        messageLabel.setVisible(!message.isEmpty());
        messageLabel.setManaged(!message.isEmpty());
    }
}
