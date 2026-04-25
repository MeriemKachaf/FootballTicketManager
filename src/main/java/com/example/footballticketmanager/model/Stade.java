package com.example.footballticketmanager.model;

public class Stade {

    private int id;
    private String nom;
    private String ville;
    private int capacite;

    public Stade(int id, String nom, String ville, int capacite) {
        this.id = id;
        this.nom = nom;
        this.ville = ville;
        this.capacite = capacite;
    }

    public Stade(String nom, String ville, int capacite) {
        this.nom = nom;
        this.ville = ville;
        this.capacite = capacite;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getVille() { return ville; }
    public int getCapacite() { return capacite; }

    @Override
    public String toString() {
        return id + " - " + nom + " (" + ville + ") - Capacite : " + capacite;
    }
}
