package com.example.footballticketmanager.model;

import java.sql.Date;

public class Reservation {

    private int id;
    private int supporterId;
    private int ticketId;
    private Date dateReservation;
    private String supporterNom;
    private String ticketInfo;

    public Reservation(int id, int supporterId, int ticketId, Date dateReservation,
                        String supporterNom, String ticketInfo) {
        this.id = id;
        this.supporterId = supporterId;
        this.ticketId = ticketId;
        this.dateReservation = dateReservation;
        this.supporterNom = supporterNom;
        this.ticketInfo = ticketInfo;
    }

    public Reservation(int supporterId, int ticketId, Date dateReservation) {
        this.supporterId = supporterId;
        this.ticketId = ticketId;
        this.dateReservation = dateReservation;
    }

    public int getId() { return id; }
    public int getSupporterId() { return supporterId; }
    public int getTicketId() { return ticketId; }
    public Date getDateReservation() { return dateReservation; }
    public String getSupporterNom() { return supporterNom; }
    public String getTicketInfo() { return ticketInfo; }

    @Override
    public String toString() {
        return id + " - " + supporterNom + " | " + ticketInfo + " | Date : " + dateReservation;
    }
}
