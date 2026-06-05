package com.example.footballticketmanager.model;

public class Ticket {

    private int id;
    private int matchId;
    private double prix;
    private String categorie;
    private String zone;
    private int quantite;

    public Ticket(int id, int matchId, double prix, String categorie, String zone, int quantite) {
        this.id = id;
        this.matchId = matchId;
        this.prix = prix;
        this.categorie = categorie;
        this.zone = zone != null ? zone : "";
        this.quantite = quantite;
    }

    public Ticket(int id, int matchId, double prix, String categorie, int quantite) {
        this(id, matchId, prix, categorie, "", quantite);
    }

    public Ticket(int id, int matchId, double prix, String categorie) {
        this(id, matchId, prix, categorie, "", 100);
    }

    public int getId()           { return id; }
    public int getMatchId()      { return matchId; }
    public double getPrix()      { return prix; }
    public String getCategorie() { return categorie; }
    public String getZone()      { return zone; }
    public int getQuantite()     { return quantite; }
}
