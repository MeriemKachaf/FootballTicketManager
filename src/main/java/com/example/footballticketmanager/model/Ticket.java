package com.example.footballticketmanager.model;

public class Ticket {

    private int id;
    private int matchId;
    private double prix;
    private String categorie;

    public Ticket(int id, int matchId, double prix, String categorie) {
        this.id = id;
        this.matchId = matchId;
        this.prix = prix;
        this.categorie = categorie;
    }

    public int getId() {
        return id;
    }

    public int getMatchId() {
        return matchId;
    }

    public double getPrix() {
        return prix;
    }

    public String getCategorie() {
        return categorie;
    }
}