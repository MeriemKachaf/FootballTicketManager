package com.example.footballticketmanager.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

public class PasswordUtils {

    private static final int ITERATIONS = 65_536;
    private static final int KEY_LENGTH = 256;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    /**
     * Hache un mot de passe avec PBKDF2 + sel aléatoire.
     * Format stocké : "iterations:selBase64:hashBase64"
     */
    public static String hasher(String motDePasse) {
        try {
            byte[] sel = new byte[16];
            new SecureRandom().nextBytes(sel);
            KeySpec spec = new PBEKeySpec(motDePasse.toCharArray(), sel, ITERATIONS, KEY_LENGTH);
            byte[] hash = SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).getEncoded();
            return ITERATIONS + ":"
                + Base64.getEncoder().encodeToString(sel) + ":"
                + Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Erreur de hachage PBKDF2", e);
        }
    }

    /**
     * Vérifie un mot de passe contre le hash stocké.
     * Compatible avec l'ancien format SHA-256 (migration transparente).
     */
    public static boolean verifier(String motDePasse, String hashStocke) {
        if (hashStocke == null || motDePasse == null) return false;

        // Ancien format SHA-256 : 64 hex chars sans ":"
        if (!hashStocke.contains(":")) {
            return hasherSHA256(motDePasse).equals(hashStocke);
        }

        // Nouveau format PBKDF2 : "iterations:selBase64:hashBase64"
        try {
            String[] parts = hashStocke.split(":");
            if (parts.length != 3) return false;
            int iterations     = Integer.parseInt(parts[0]);
            byte[] sel         = Base64.getDecoder().decode(parts[1]);
            byte[] hashAttendu = Base64.getDecoder().decode(parts[2]);
            KeySpec spec = new PBEKeySpec(motDePasse.toCharArray(), sel, iterations, KEY_LENGTH);
            byte[] hashCalcule = SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).getEncoded();
            // Comparaison en temps constant (résiste aux attaques temporelles)
            if (hashCalcule.length != hashAttendu.length) return false;
            int diff = 0;
            for (int i = 0; i < hashCalcule.length; i++)
                diff |= hashCalcule[i] ^ hashAttendu[i];
            return diff == 0;
        } catch (Exception e) {
            return false;
        }
    }

    // Gardé uniquement pour la rétrocompatibilité avec les anciens comptes SHA-256
    private static String hasherSHA256(String motDePasse) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(motDePasse.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                String h = Integer.toHexString(0xff & b);
                if (h.length() == 1) hex.append('0');
                hex.append(h);
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
