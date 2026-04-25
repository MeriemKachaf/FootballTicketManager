package com.example.footballticketmanager.controller;

import com.example.footballticketmanager.HelloApplication;
import com.example.footballticketmanager.dao.UtilisateurDAO;
import com.example.footballticketmanager.model.Utilisateur;
import com.example.footballticketmanager.session.Session;
import com.example.footballticketmanager.util.PasswordUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField motDePasseField;
    @FXML private Label messageLabel;

    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    @FXML
    public void seConnecter() {
        String email = emailField.getText().trim();
        String motDePasse = motDePasseField.getText();

        if (email.isEmpty() || motDePasse.isEmpty()) {
            afficherMessage("Veuillez remplir tous les champs.", false);
            return;
        }

        Utilisateur utilisateur = utilisateurDAO.authentifier(email, PasswordUtils.hasher(motDePasse));

        if (utilisateur == null) {
            afficherMessage("Email ou mot de passe incorrect.", false);
            return;
        }

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
