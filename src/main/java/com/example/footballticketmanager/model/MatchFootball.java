package com.example.footballticketmanager.model;

import java.sql.Date;

public class MatchFootball {

    private int id;
    private String equipeDomicile;
    private String equipeExterieure;
    private int stadeId;
    private String stadeNom;
    private Date dateMatch;

    public MatchFootball(int id, String equipeDomicile, String equipeExterieure,
                         int stadeId, String stadeNom, Date dateMatch) {
        this.id = id;
        this.equipeDomicile = equipeDomicile;
        this.equipeExterieure = equipeExterieure;
        this.stadeId = stadeId;
        this.stadeNom = stadeNom;
        this.dateMatch = dateMatch;
    }

    public MatchFootball(String equipeDomicile, String equipeExterieure, int stadeId, Date dateMatch) {
        this.equipeDomicile = equipeDomicile;
        this.equipeExterieure = equipeExterieure;
        this.stadeId = stadeId;
        this.dateMatch = dateMatch;
    }

    public MatchFootball(int id, String equipeDomicile, String equipeExterieure, int stadeId, Date dateMatch) {
        this.id = id;
        this.equipeDomicile = equipeDomicile;
        this.equipeExterieure = equipeExterieure;
        this.stadeId = stadeId;
        this.dateMatch = dateMatch;
    }

    public int getId() { return id; }
    public String getEquipeDomicile() { return equipeDomicile; }
    public String getEquipeExterieure() { return equipeExterieure; }
    public int getStadeId() { return stadeId; }
    public String getStadeNom() { return stadeNom; }
    public Date getDateMatch() { return dateMatch; }

    @Override
    public String toString() {
        return id + " - " + equipeDomicile + " vs " + equipeExterieure
            + " - " + (stadeNom != null ? stadeNom : stadeId) + " - " + dateMatch;
    }
}
