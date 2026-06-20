package com.example.footballticketmanager.model;

// Modèle MVC : représente un ticket (correspond à la table "ticket" en BDD)
public class Ticket {

    private int id;
    private int matchId;       // clé étrangère → table match_football
    private double prix;
    private String categorie;  // VIP, Tribune ou Standard
    private String zone;
    private int quantite;      // stock total disponible pour ce type de ticket

    // Constructeur complet : utilisé quand on lit depuis la BDD
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
