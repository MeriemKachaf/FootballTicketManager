package com.example.footballticketmanager.model;

public class Supporter {

    private int id;
    private String nom;
    private String email;
    private String equipeFavorite;

    public Supporter(int id, String nom, String email, String equipeFavorite) {
        this.id = id;
        this.nom = nom;
        this.email = email;
        this.equipeFavorite = equipeFavorite;
    }

    public Supporter(String nom, String email, String equipeFavorite) {
        this.nom = nom;
        this.email = email;
        this.equipeFavorite = equipeFavorite;
    }

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getEmail() {
        return email;
    }

    public String getEquipeFavorite() {
        return equipeFavorite;
    }

    @Override
    public String toString() {
        return id + " - " + nom + " - " + email + " - " + equipeFavorite;
    }
}