package com.example.footballticketmanager.session;

import com.example.footballticketmanager.model.Utilisateur;

public class Session {

    private static Utilisateur utilisateurConnecte;

    public static void connecter(Utilisateur u) {
        utilisateurConnecte = u;
    }

    public static Utilisateur getUtilisateur() {
        return utilisateurConnecte;
    }

    public static boolean isAdmin() {
        return utilisateurConnecte != null && "admin".equals(utilisateurConnecte.getRole());
    }

    public static void deconnecter() {
        utilisateurConnecte = null;
    }
}
