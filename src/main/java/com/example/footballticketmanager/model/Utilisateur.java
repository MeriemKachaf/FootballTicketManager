package com.example.footballticketmanager.model;

public class Utilisateur {

    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String role;
    private String equipeFavorite;

    public Utilisateur(int id, String nom, String prenom, String email, String motDePasse, String role, String equipeFavorite) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.role = role;
        this.equipeFavorite = equipeFavorite;
    }

    public Utilisateur(String nom, String prenom, String email, String motDePasse, String role, String equipeFavorite) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.role = role;
        this.equipeFavorite = equipeFavorite;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getEmail() { return email; }
    public String getMotDePasse() { return motDePasse; }
    public String getRole() { return role; }
    public String getEquipeFavorite() { return equipeFavorite; }

    public void setNom(String nom) { this.nom = nom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    public void setEquipeFavorite(String equipeFavorite) { this.equipeFavorite = equipeFavorite; }
}
