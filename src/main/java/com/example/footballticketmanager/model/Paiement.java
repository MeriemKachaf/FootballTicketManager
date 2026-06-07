package com.example.footballticketmanager.model;

import java.sql.Date;

public class Paiement {

    private int id;
    private int reservationId;
    private double montant;
    private Date datePaiement;
    private String modePaiement;
    private String statut;
    private String utilisateurNom;
    private String ticketInfo;

    public Paiement(int id, int reservationId, double montant, Date datePaiement,
                    String modePaiement, String statut, String utilisateurNom, String ticketInfo) {
        this.id = id;
        this.reservationId = reservationId;
        this.montant = montant;
        this.datePaiement = datePaiement;
        this.modePaiement = modePaiement;
        this.statut = statut;
        this.utilisateurNom = utilisateurNom;
        this.ticketInfo = ticketInfo;
    }

    public Paiement(int reservationId, double montant, Date datePaiement,
                    String modePaiement, String statut) {
        this.reservationId = reservationId;
        this.montant = montant;
        this.datePaiement = datePaiement;
        this.modePaiement = modePaiement;
        this.statut = statut;
    }

    public int getId() { return id; }
    public int getReservationId() { return reservationId; }
    public double getMontant() { return montant; }
    public Date getDatePaiement() { return datePaiement; }
    public String getModePaiement() { return modePaiement; }
    public String getStatut() { return statut; }
    public String getUtilisateurNom() { return utilisateurNom; }
    public String getTicketInfo() { return ticketInfo; }

    @Override
    public String toString() {
        return id + " - " + utilisateurNom + " | " + ticketInfo
            + " | " + montant + "€ | " + modePaiement
            + " | Statut : " + statut.toUpperCase() + " | " + datePaiement;
    }
}
