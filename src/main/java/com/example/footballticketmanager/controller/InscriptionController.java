package com.example.footballticketmanager.controller;

import com.example.footballticketmanager.HelloApplication;
import com.example.footballticketmanager.dao.JournalDAO;
import com.example.footballticketmanager.dao.UtilisateurDAO;
import com.example.footballticketmanager.model.Utilisateur;
import com.example.footballticketmanager.util.PasswordUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class InscriptionController {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private PasswordField motDePasseField;
    @FXML private PasswordField confirmField;
    @FXML private Label messageLabel;

    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private final JournalDAO journalDAO = new JournalDAO();

    @FXML
    public void sInscrire() {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        String motDePasse = motDePasseField.getText();
        String confirm = confirmField.getText();

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty()
                || motDePasse.isEmpty() || confirm.isEmpty()) {
            afficherMessage("Veuillez remplir tous les champs.", false);
            return;
        }

        if (!motDePasse.equals(confirm)) {
            afficherMessage("Les mots de passe ne correspondent pas.", false);
            return;
        }

        String erreurMdp = PasswordUtils.validerComplexite(motDePasse);
        if (erreurMdp != null) {
            afficherMessage(erreurMdp, false);
            return;
        }

        if (email.length() > 254 || !email.matches("^[a-zA-Z0-9][a-zA-Z0-9._%+-]*@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            afficherMessage("Format email invalide (ex: nom@domaine.com).", false);
            return;
        }
        email = email.toLowerCase();

        if (utilisateurDAO.emailExiste(email)) {
            afficherMessage("Cet email est deja utilise.", false);
            return;
        }

        String hash = PasswordUtils.hasher(motDePasse);
        Utilisateur nouvel = new Utilisateur(nom, prenom, email, hash, "user", "");

        int nouvelId = utilisateurDAO.ajouter(nouvel);
        if (nouvelId > 0) {
            utilisateurDAO.sauvegarderHistorique(nouvelId, hash);
            journalDAO.enregistrer(email, "INSCRIPTION", "Nouveau compte créé : " + nom + " " + prenom, "SUCCES");
            afficherMessage("Inscription reussie ! Vous pouvez vous connecter.", true);
            viderChamps();
        } else {
            journalDAO.enregistrer(email, "INSCRIPTION_ECHEC", "Échec de la création du compte", "ECHEC");
            afficherMessage("Erreur lors de l'inscription. Reessayez.", false);
        }
    }

    @FXML
    public void allerLogin() {
        try {
            HelloApplication.changerScene(nomField, "login-view.fxml", "Connexion", 700, 480);
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

    private void viderChamps() {
        nomField.clear();
        prenomField.clear();
        emailField.clear();
        motDePasseField.clear();
        confirmField.clear();
    }
}
