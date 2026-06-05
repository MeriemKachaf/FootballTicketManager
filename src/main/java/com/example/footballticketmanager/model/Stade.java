package com.example.footballticketmanager.model;

public class Stade {

    private int id;
    private String nom;
    private String ville;
    private int capacite;
    private String localisation;

    public Stade(int id, String nom, String ville, int capacite, String localisation) {
        this.id = id;
        this.nom = nom;
        this.ville = ville;
        this.capacite = capacite;
        this.localisation = localisation != null ? localisation : "";
    }

    public Stade(int id, String nom, String ville, int capacite) {
        this(id, nom, ville, capacite, "");
    }

    public Stade(String nom, String ville, int capacite, String localisation) {
        this.nom = nom;
        this.ville = ville;
        this.capacite = capacite;
        this.localisation = localisation != null ? localisation : "";
    }

    public Stade(String nom, String ville, int capacite) {
        this(nom, ville, capacite, "");
    }

    public int getId()           { return id; }
    public String getNom()       { return nom; }
    public String getVille()     { return ville; }
    public int getCapacite()     { return capacite; }
    public String getLocalisation() { return localisation; }

    @Override
    public String toString() {
        return id + " - " + nom + " (" + ville + ")";
    }
}
