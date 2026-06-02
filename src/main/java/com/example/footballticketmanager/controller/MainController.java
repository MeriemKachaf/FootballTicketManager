package com.example.footballticketmanager.controller;

import com.example.footballticketmanager.HelloApplication;
import com.example.footballticketmanager.dao.*;
import com.example.footballticketmanager.model.*;
import com.example.footballticketmanager.session.Session;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainController {

    // --- Barre du haut ---
    @FXML private Label welcomeLabel;

    // --- Sidebar admin ---
    @FXML private VBox navAdmin;

    // --- Tableau de bord ---
    @FXML private VBox statsPanel;
    @FXML private Label statMatchs;
    @FXML private Label statSupporters;
    @FXML private Label statTickets;
    @FXML private Label statReservations;
    @FXML private Label statRevenu;
    @FXML private Label statEnAttente;

    // --- Panneau admin ---
    @FXML private VBox pannelAdmin;

    // --- Champs Matchs ---
    @FXML private TextField domicileField;
    @FXML private TextField exterieureField;
    @FXML private ComboBox<String> stadeCombo;
    @FXML private TextField dateField;

    // --- Champs Supporters ---
    @FXML private TextField nomSupporterField;
    @FXML private TextField emailSupporterField;
    @FXML private TextField equipeFavoriteField;

    // --- Champs Stades ---
    @FXML private TextField nomStadeField;
    @FXML private TextField villeStadeField;
    @FXML private TextField capaciteStadeField;

    // --- Reservations ---
    @FXML private HBox ligneSupporterCombo;
    @FXML private HBox ligneTicketCombo;
    @FXML private VBox ligneChoixUser;
    @FXML private ComboBox<String> supporterCombo;
    @FXML private ComboBox<String> ticketCombo;
    @FXML private ComboBox<String> matchComboUser;
    @FXML private ComboBox<String> categorieCombo;

    // --- Panneaux de formulaires ---
    @FXML private VBox panneauMatchs;
    @FXML private VBox panneauSupporters;
    @FXML private VBox panneauStades;
    @FXML private VBox panneauReservations;

    // --- Barre tableau ---
    @FXML private Label tableTitle;
    @FXML private TextField searchField;

    // --- Tableau de resultats (remplace ListView) ---
    @FXML private TableView<String[]> tableView;

    private final MatchDAO matchDAO = new MatchDAO();
    private final SupporterDAO supporterDAO = new SupporterDAO();
    private final TicketDAO ticketDAO = new TicketDAO();
    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final StadeDAO stadeDAO = new StadeDAO();
    private final PaiementDAO paiementDAO = new PaiementDAO();

    private int selectedMatchId = -1;
    private int selectedSupporterId = -1;
    private int supporterIdConnecte = -1;

    // =========================================
    // INITIALISATION
    // =========================================

    @FXML
    public void initialize() {
        Utilisateur u = Session.getUtilisateur();
        if (u == null) return;

        welcomeLabel.setText("Bonjour " + u.getPrenom() + " " + u.getNom()
            + "  |  Role : " + u.getRole().toUpperCase());

        boolean admin = Session.isAdmin();
        pannelAdmin.setVisible(admin);
        pannelAdmin.setManaged(admin);
        navAdmin.setVisible(admin);
        navAdmin.setManaged(admin);
        statsPanel.setVisible(admin);
        statsPanel.setManaged(admin);

        if (admin) {
            chargerComboBoxes();
            chargerStats();
            afficherMatchs();
        } else {
            ligneSupporterCombo.setVisible(false);
            ligneSupporterCombo.setManaged(false);
            ligneTicketCombo.setVisible(false);
            ligneTicketCombo.setManaged(false);
            ligneChoixUser.setVisible(true);
            ligneChoixUser.setManaged(true);

            Supporter s = supporterDAO.findByEmail(u.getEmail());
            if (s != null) supporterIdConnecte = s.getId();

            for (MatchFootball m : matchDAO.getAllMatches())
                matchComboUser.getItems().add(
                    m.getId() + " - " + m.getEquipeDomicile() + " vs " + m.getEquipeExterieure()
                    + " (" + m.getDateMatch() + ")"
                );
            chargerReservations();
        }
    }

    private void chargerComboBoxes() {
        supporterCombo.getItems().clear();
        ticketCombo.getItems().clear();
        stadeCombo.getItems().clear();
        for (Supporter s : supporterDAO.getAllSupporters())
            supporterCombo.getItems().add(s.getId() + " - " + s.getNom());
        ticketCombo.getItems().addAll(ticketDAO.getAllTicketsAvecMatch());
        for (Stade s : stadeDAO.getAllStades())
            stadeCombo.getItems().add(s.getId() + " - " + s.getNom());
    }

    private void chargerStats() {
        statMatchs.setText(String.valueOf(matchDAO.getAllMatches().size()));
        statSupporters.setText(String.valueOf(supporterDAO.getAllSupporters().size()));
        statTickets.setText(String.valueOf(ticketDAO.getAllTickets().size()));
        statReservations.setText(String.valueOf(reservationDAO.getAllReservations().size()));
        statRevenu.setText(String.format("%.0f €", paiementDAO.getTotalRevenu()));
        statEnAttente.setText(String.valueOf(paiementDAO.countEnAttente()));
    }

    private void montrerPanneaux(VBox... aAfficher) {
        VBox[] tous = {panneauMatchs, panneauSupporters, panneauStades, panneauReservations};
        for (VBox p : tous) { p.setVisible(false); p.setManaged(false); }
        for (VBox p : aAfficher) { p.setVisible(true); p.setManaged(true); }
    }

    // Methode centrale : affiche les donnees dans le tableau
    // La colonne 0 (ID) est conservee en interne mais masquee visuellement
    private void afficherDansTable(String[] colonnes, List<String[]> donnees) {
        tableView.getColumns().clear();
        tableView.getItems().clear();
        for (int i = 0; i < colonnes.length; i++) {
            final int idx = i;
            TableColumn<String[], String> col = new TableColumn<>(colonnes[idx]);
            col.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().length > idx ? data.getValue()[idx] : "")
            );
            if (i == 0) {
                col.setVisible(false);
                col.setMaxWidth(0);
                col.setMinWidth(0);
            }
            tableView.getColumns().add(col);
        }
        tableView.setPlaceholder(new Label("Aucune donnee trouvee."));
        tableView.getItems().addAll(donnees);
    }

    private String[] getSelectedRow() {
        return tableView.getSelectionModel().getSelectedItem();
    }

    // =========================================
    // BADGES D'EQUIPES — couleurs et abreviations
    // =========================================

    private static final Map<String, String[]> EQUIPES = new HashMap<>();
    static {
        // Format : "NOM_EQUIPE" -> { "ABREVIATION", "#couleurFond", "#couleurTexte" }
        EQUIPES.put("PSG",           new String[]{"PSG", "#001489", "white"});
        EQUIPES.put("OM",            new String[]{"OM",  "#009FC5", "white"});
        EQUIPES.put("Lyon",          new String[]{"OL",  "#ac1a2f", "white"});
        EQUIPES.put("Real Madrid",   new String[]{"RM",  "#0B2240", "white"});
        EQUIPES.put("Barcelona",     new String[]{"FCB", "#a50044", "white"});
        EQUIPES.put("Bayern Munich", new String[]{"FCB", "#dc052d", "white"});
        EQUIPES.put("Dortmund",      new String[]{"BVB", "#fde100", "#1a1a1a"});
        EQUIPES.put("Chelsea",       new String[]{"CHE", "#034694", "white"});
        EQUIPES.put("Arsenal",       new String[]{"ARS", "#ef0107", "white"});
        EQUIPES.put("Man United",    new String[]{"MUN", "#da291c", "white"});
        EQUIPES.put("Man City",      new String[]{"MCI", "#6cabdd", "#1a1a1a"});
        EQUIPES.put("AC Milan",      new String[]{"ACM", "#fb090b", "white"});
        EQUIPES.put("Inter Milan",   new String[]{"INT", "#0068a8", "white"});
        EQUIPES.put("Atletico",      new String[]{"ATM", "#cb3524", "white"});
        EQUIPES.put("Sevilla",       new String[]{"SEV", "#e5101e", "white"});
        EQUIPES.put("Leverkusen",    new String[]{"B04", "#e32221", "white"});
    }

    private String[] getInfoEquipe(String equipe) {
        String[] info = EQUIPES.get(equipe);
        if (info != null) return info;
        // Fallback : genere une abreviation automatique
        String[] mots = equipe.trim().split("\\s+");
        StringBuilder abr = new StringBuilder();
        for (String m : mots) if (!m.isEmpty()) abr.append(m.charAt(0));
        return new String[]{abr.toString().toUpperCase().substring(0, Math.min(3, abr.length())), "#64748b", "white"};
    }

    private TableColumn<String[], String> creerColonneEquipe(String titre, int idx) {
        TableColumn<String[], String> col = new TableColumn<>(titre);
        col.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().length > idx ? data.getValue()[idx] : "")
        );
        col.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String equipe, boolean empty) {
                super.updateItem(equipe, empty);
                if (empty || equipe == null || equipe.isEmpty()) { setGraphic(null); return; }

                String[] info = getInfoEquipe(equipe);

                Label badge = new Label(info[0]);
                badge.setMinWidth(40);
                badge.setPrefHeight(22);
                badge.setAlignment(Pos.CENTER);
                badge.setStyle(
                    "-fx-background-color:" + info[1] + ";" +
                    "-fx-background-radius:4;" +
                    "-fx-text-fill:" + info[2] + ";" +
                    "-fx-font-weight:bold;" +
                    "-fx-font-size:10px;" +
                    "-fx-padding:2 6 2 6;"
                );

                Label nom = new Label(equipe);
                nom.setStyle("-fx-font-size:13px;");

                HBox hbox = new HBox(8, badge, nom);
                hbox.setAlignment(Pos.CENTER_LEFT);
                setGraphic(hbox);
                setText(null);
            }
        });
        return col;
    }

    private void afficherMatchsDansTable(List<MatchFootball> matchs) {
        tableView.getColumns().clear();
        tableView.getItems().clear();

        TableColumn<String[], String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[0]));
        colId.setVisible(false); colId.setMaxWidth(0); colId.setMinWidth(0);

        TableColumn<String[], String> colDom = creerColonneEquipe("Equipe Domicile", 1);
        TableColumn<String[], String> colExt = creerColonneEquipe("Equipe Exterieure", 2);

        TableColumn<String[], String> colStade = new TableColumn<>("Stade");
        colStade.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[3]));

        TableColumn<String[], String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[4]));

        tableView.getColumns().addAll(colId, colDom, colExt, colStade, colDate);
        tableView.setPlaceholder(new Label("Aucun match trouve."));

        for (MatchFootball m : matchs)
            tableView.getItems().add(new String[]{
                String.valueOf(m.getId()), m.getEquipeDomicile(), m.getEquipeExterieure(),
                m.getStadeNom() != null ? m.getStadeNom() : "",
                m.getDateMatch() != null ? m.getDateMatch().toString() : ""
            });
    }

    // =========================================
    // MATCHS
    // =========================================

    @FXML
    public void afficherMatchs() {
        try {
            montrerPanneaux(panneauMatchs);
            afficherMatchsDansTable(matchDAO.getAllMatches());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'afficher les matchs.");
        }
    }

    @FXML
    public void ajouterMatch() {
        try {
            String domicile = domicileField.getText().trim();
            String exterieure = exterieureField.getText().trim();
            String dateText = dateField.getText().trim();
            String stadeItem = stadeCombo.getValue();
            if (domicile.isEmpty() || exterieure.isEmpty() || dateText.isEmpty() || stadeItem == null) {
                showAlert("Erreur", "Tous les champs sont obligatoires (y compris le stade).");
                return;
            }
            int stadeId = Integer.parseInt(stadeItem.split(" - ")[0]);
            matchDAO.addMatch(new MatchFootball(domicile, exterieure, stadeId, Date.valueOf(dateText)));
            clearMatchFields();
            chargerComboBoxes();
            chargerStats();
            afficherMatchs();
            showAlert("Succes", "Match ajoute avec succes.");
        } catch (IllegalArgumentException e) {
            showAlert("Erreur", "La date doit etre au format YYYY-MM-DD.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ajouter le match.");
        }
    }

    @FXML
    public void chargerMatchSelectionne() {
        try {
            String[] row = getSelectedRow();
            if (row == null || row.length < 5) { showAlert("Erreur", "Selectionnez un match d'abord."); return; }
            selectedMatchId = Integer.parseInt(row[0]);
            domicileField.setText(row[1]);
            exterieureField.setText(row[2]);
            dateField.setText(row[4]);
            for (String item : stadeCombo.getItems()) {
                if (item.split(" - ", 2).length > 1 && item.split(" - ", 2)[1].equals(row[3])) {
                    stadeCombo.setValue(item);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger le match.");
        }
    }

    @FXML
    public void modifierMatch() {
        try {
            if (selectedMatchId == -1) { showAlert("Erreur", "Chargez un match avant de le modifier."); return; }
            String domicile = domicileField.getText().trim();
            String exterieure = exterieureField.getText().trim();
            String dateText = dateField.getText().trim();
            String stadeItem = stadeCombo.getValue();
            if (domicile.isEmpty() || exterieure.isEmpty() || dateText.isEmpty() || stadeItem == null) {
                showAlert("Erreur", "Tous les champs sont obligatoires.");
                return;
            }
            int stadeId = Integer.parseInt(stadeItem.split(" - ")[0]);
            boolean ok = matchDAO.updateMatch(
                new MatchFootball(selectedMatchId, domicile, exterieure, stadeId, Date.valueOf(dateText))
            );
            if (ok) {
                clearMatchFields();
                selectedMatchId = -1;
                chargerComboBoxes();
                afficherMatchs();
                showAlert("Succes", "Match modifie avec succes.");
            } else {
                showAlert("Erreur", "Aucune modification effectuee.");
            }
        } catch (IllegalArgumentException e) {
            showAlert("Erreur", "La date doit etre au format YYYY-MM-DD.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de modifier le match.");
        }
    }

    @FXML
    public void supprimerMatch() {
        try {
            String[] row = getSelectedRow();
            if (row == null) { showAlert("Erreur", "Selectionnez un match a supprimer."); return; }
            matchDAO.deleteMatch(Integer.parseInt(row[0]));
            chargerStats();
            afficherMatchs();
            showAlert("Succes", "Match supprime.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de supprimer le match.");
        }
    }

    // =========================================
    // SUPPORTERS
    // =========================================

    @FXML
    public void afficherSupporters() {
        try {
            montrerPanneaux(panneauSupporters);
            List<Supporter> supporters = supporterDAO.getAllSupporters();
            String[] cols = {"ID", "Nom", "Email", "Equipe Favorite"};
            List<String[]> rows = new ArrayList<>();
            for (Supporter s : supporters)
                rows.add(new String[]{String.valueOf(s.getId()), s.getNom(), s.getEmail(), s.getEquipeFavorite()});
            afficherDansTable(cols, rows);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'afficher les supporters.");
        }
    }

    @FXML
    public void ajouterSupporter() {
        try {
            String nom = nomSupporterField.getText().trim();
            String email = emailSupporterField.getText().trim();
            String equipe = equipeFavoriteField.getText().trim();
            if (nom.isEmpty() || email.isEmpty() || equipe.isEmpty()) {
                showAlert("Erreur", "Tous les champs supporter sont obligatoires.");
                return;
            }
            supporterDAO.addSupporter(new Supporter(nom, email, equipe));
            clearSupporterFields();
            chargerComboBoxes();
            chargerStats();
            afficherSupporters();
            showAlert("Succes", "Supporter ajoute avec succes.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ajouter le supporter.");
        }
    }

    @FXML
    public void chargerSupporterSelectionne() {
        try {
            String[] row = getSelectedRow();
            if (row == null || row.length < 4) { showAlert("Erreur", "Selectionnez un supporter d'abord."); return; }
            selectedSupporterId = Integer.parseInt(row[0]);
            nomSupporterField.setText(row[1]);
            emailSupporterField.setText(row[2]);
            equipeFavoriteField.setText(row[3]);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger le supporter.");
        }
    }

    @FXML
    public void modifierSupporter() {
        try {
            if (selectedSupporterId == -1) { showAlert("Erreur", "Chargez un supporter avant de le modifier."); return; }
            String nom = nomSupporterField.getText().trim();
            String email = emailSupporterField.getText().trim();
            String equipe = equipeFavoriteField.getText().trim();
            if (nom.isEmpty() || email.isEmpty() || equipe.isEmpty()) {
                showAlert("Erreur", "Tous les champs supporter sont obligatoires.");
                return;
            }
            boolean ok = supporterDAO.updateSupporter(new Supporter(selectedSupporterId, nom, email, equipe));
            if (ok) {
                clearSupporterFields();
                selectedSupporterId = -1;
                chargerComboBoxes();
                chargerStats();
                afficherSupporters();
                showAlert("Succes", "Supporter modifie avec succes.");
            } else {
                showAlert("Erreur", "Aucune modification effectuee.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de modifier le supporter.");
        }
    }

    @FXML
    public void supprimerSupporter() {
        try {
            String[] row = getSelectedRow();
            if (row == null) { showAlert("Erreur", "Selectionnez un supporter a supprimer."); return; }
            supporterDAO.deleteSupporter(Integer.parseInt(row[0]));
            chargerComboBoxes();
            chargerStats();
            afficherSupporters();
            showAlert("Succes", "Supporter supprime.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de supprimer le supporter.");
        }
    }

    // =========================================
    // TICKETS
    // =========================================

    @FXML
    public void afficherTickets() {
        try {
            montrerPanneaux();
            List<Ticket> tickets = ticketDAO.getAllTickets();
            String[] cols = {"ID", "Match ID", "Categorie", "Prix (€)"};
            List<String[]> rows = new ArrayList<>();
            for (Ticket t : tickets)
                rows.add(new String[]{
                    String.valueOf(t.getId()), String.valueOf(t.getMatchId()),
                    t.getCategorie(), String.valueOf(t.getPrix())
                });
            afficherDansTable(cols, rows);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'afficher les tickets.");
        }
    }

    // =========================================
    // STADES
    // =========================================

    @FXML
    public void afficherStades() {
        try {
            montrerPanneaux(panneauStades);
            List<Stade> stades = stadeDAO.getAllStades();
            String[] cols = {"ID", "Nom", "Ville", "Capacite"};
            List<String[]> rows = new ArrayList<>();
            for (Stade s : stades)
                rows.add(new String[]{
                    String.valueOf(s.getId()), s.getNom(), s.getVille(), String.valueOf(s.getCapacite())
                });
            afficherDansTable(cols, rows);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'afficher les stades.");
        }
    }

    @FXML
    public void ajouterStade() {
        try {
            String nom = nomStadeField.getText().trim();
            String ville = villeStadeField.getText().trim();
            String capText = capaciteStadeField.getText().trim();
            if (nom.isEmpty() || ville.isEmpty() || capText.isEmpty()) {
                showAlert("Erreur", "Tous les champs stade sont obligatoires.");
                return;
            }
            if (stadeDAO.ajouter(new Stade(nom, ville, Integer.parseInt(capText)))) {
                nomStadeField.clear(); villeStadeField.clear(); capaciteStadeField.clear();
                chargerComboBoxes();
                afficherStades();
                showAlert("Succes", "Stade ajoute avec succes.");
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "La capacite doit etre un nombre entier.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ajouter le stade.");
        }
    }

    @FXML
    public void supprimerStade() {
        try {
            String[] row = getSelectedRow();
            if (row == null) { showAlert("Erreur", "Selectionnez un stade a supprimer."); return; }
            if (stadeDAO.supprimer(Integer.parseInt(row[0]))) {
                chargerComboBoxes();
                afficherStades();
                showAlert("Succes", "Stade supprime.");
            } else {
                showAlert("Erreur", "Impossible de supprimer : des matchs utilisent ce stade.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de supprimer le stade.");
        }
    }

    // =========================================
    // RESERVATIONS
    // =========================================

    @FXML
    public void chargerReservations() {
        try {
            montrerPanneaux(panneauReservations);
            List<Reservation> reservations = Session.isAdmin()
                ? reservationDAO.getAllReservations()
                : reservationDAO.getReservationsBySupporterId(supporterIdConnecte);
            String[] cols = {"ID", "Supporter", "Match - Categorie", "Date Reservation"};
            List<String[]> rows = new ArrayList<>();
            for (Reservation r : reservations)
                rows.add(new String[]{
                    String.valueOf(r.getId()), r.getSupporterNom(),
                    r.getTicketInfo(), r.getDateReservation().toString()
                });
            afficherDansTable(cols, rows);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les reservations.");
        }
    }

    @FXML
    public void onMatchUserSelectionne() {
        String matchItem = matchComboUser.getValue();
        if (matchItem == null) return;
        int matchId = Integer.parseInt(matchItem.split(" - ")[0]);
        categorieCombo.getItems().clear();
        for (Ticket t : ticketDAO.getTicketsByMatchId(matchId))
            categorieCombo.getItems().add(t.getId() + " - " + t.getCategorie() + " - " + t.getPrix() + "€");
    }

    @FXML
    public void ajouterReservation() {
        try {
            int supporterId;
            int ticketId;
            double montant;

            if (Session.isAdmin()) {
                String supporterItem = supporterCombo.getValue();
                String ticketItem = ticketCombo.getValue();
                if (supporterItem == null || ticketItem == null) {
                    showAlert("Erreur", "Selectionnez un supporter et un ticket.");
                    return;
                }
                supporterId = Integer.parseInt(supporterItem.split(" - ")[0]);
                ticketId = Integer.parseInt(ticketItem.split(" - ")[0]);
                String[] parts = ticketItem.split(" - ");
                montant = Double.parseDouble(parts[parts.length - 1].replace("€", ""));
            } else {
                if (supporterIdConnecte == -1) {
                    showAlert("Erreur", "Votre compte n'est pas lie a un profil supporter.");
                    return;
                }
                String categorieItem = categorieCombo.getValue();
                if (categorieItem == null) {
                    showAlert("Erreur", "Selectionnez un match et une categorie.");
                    return;
                }
                supporterId = supporterIdConnecte;
                ticketId = Integer.parseInt(categorieItem.split(" - ")[0]);
                String[] parts = categorieItem.split(" - ");
                montant = Double.parseDouble(parts[parts.length - 1].replace("€", ""));
            }

            Reservation r = new Reservation(supporterId, ticketId, Date.valueOf(LocalDate.now()));
            if (reservationDAO.ajouter(r)) {
                List<Reservation> all = reservationDAO.getAllReservations();
                if (!all.isEmpty()) {
                    int reservationId = all.get(all.size() - 1).getId();
                    paiementDAO.ajouter(new Paiement(
                        reservationId, montant, Date.valueOf(LocalDate.now()),
                        "Carte bancaire", "en_attente"
                    ));
                }
                if (Session.isAdmin()) chargerStats();
                chargerReservations();
                showAlert("Succes", "Reservation effectuee ! Paiement en attente de validation.");
            } else {
                showAlert("Erreur", "Impossible d'ajouter la reservation.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ajout de la reservation.");
        }
    }

    @FXML
    public void supprimerReservation() {
        try {
            String[] row = getSelectedRow();
            if (row == null) { showAlert("Erreur", "Selectionnez une reservation a supprimer."); return; }
            if (reservationDAO.supprimer(Integer.parseInt(row[0]))) {
                if (Session.isAdmin()) chargerStats();
                chargerReservations();
                showAlert("Succes", "Reservation supprimee.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la suppression.");
        }
    }

    // =========================================
    // PAIEMENTS (consultation uniquement)
    // =========================================

    @FXML
    public void chargerPaiements() {
        try {
            montrerPanneaux();
            List<Paiement> paiements = paiementDAO.getAllPaiements();
            String[] cols = {"ID", "Supporter", "Match", "Montant (€)", "Mode", "Statut", "Date"};
            List<String[]> rows = new ArrayList<>();
            for (Paiement p : paiements)
                rows.add(new String[]{
                    String.valueOf(p.getId()), p.getSupporterNom(), p.getTicketInfo(),
                    String.valueOf(p.getMontant()), p.getModePaiement(),
                    p.getStatut().toUpperCase(), p.getDatePaiement().toString()
                });
            afficherDansTable(cols, rows);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les paiements.");
        }
    }

    // =========================================
    // UTILITAIRES
    // =========================================

    @FXML
    public void resetFields() {
        clearMatchFields();
        clearSupporterFields();
        selectedMatchId = -1;
        selectedSupporterId = -1;
        tableView.getItems().clear();
        tableView.getColumns().clear();
        supporterCombo.setValue(null);
        ticketCombo.setValue(null);
        if (nomStadeField != null) {
            nomStadeField.clear();
            villeStadeField.clear();
            capaciteStadeField.clear();
        }
    }

    @FXML
    public void seDeconnecter() {
        Session.deconnecter();
        try {
            HelloApplication.changerScene(welcomeLabel, "login-view.fxml", "Connexion", 450, 480);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearMatchFields() {
        domicileField.clear();
        exterieureField.clear();
        stadeCombo.setValue(null);
        dateField.clear();
    }

    private void clearSupporterFields() {
        nomSupporterField.clear();
        emailSupporterField.clear();
        equipeFavoriteField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
