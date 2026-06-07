package com.example.footballticketmanager.model;

import java.sql.Date;

public class Reservation {

    private int id;
    private int utilisateurId;
    private int ticketId;
    private int quantite;
    private Date dateReservation;
    private String utilisateurNom;
    private String ticketInfo;
    private String statutPaiement;

    public Reservation(int id, int utilisateurId, int ticketId, int quantite, Date dateReservation,
                       String utilisateurNom, String ticketInfo, String statutPaiement) {
        this.id = id;
        this.utilisateurId = utilisateurId;
        this.ticketId = ticketId;
        this.quantite = quantite;
        this.dateReservation = dateReservation;
        this.utilisateurNom = utilisateurNom;
        this.ticketInfo = ticketInfo;
        this.statutPaiement = statutPaiement;
    }

    public Reservation(int utilisateurId, int ticketId, int quantite, Date dateReservation) {
        this.utilisateurId = utilisateurId;
        this.ticketId = ticketId;
        this.quantite = quantite;
        this.dateReservation = dateReservation;
    }

    public int getId() { return id; }
    public int getUtilisateurId() { return utilisateurId; }
    public int getTicketId() { return ticketId; }
    public int getQuantite() { return quantite; }
    public Date getDateReservation() { return dateReservation; }
    public String getUtilisateurNom() { return utilisateurNom; }
    public String getTicketInfo() { return ticketInfo; }
    public String getStatutPaiement() { return statutPaiement; }

    @Override
    public String toString() {
        return id + " - " + utilisateurNom + " | " + ticketInfo + " | Date : " + dateReservation;
    }
}
