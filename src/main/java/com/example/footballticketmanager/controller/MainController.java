package com.example.footballticketmanager.controller;

import com.example.footballticketmanager.HelloApplication;
import com.example.footballticketmanager.dao.*;
import com.example.footballticketmanager.model.*;
import com.example.footballticketmanager.session.Session;
import com.example.footballticketmanager.util.PasswordUtils;
import com.example.footballticketmanager.util.ExportService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.awt.Desktop;
import java.io.File;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MainController {

    // --- Barre du haut ---
    @FXML private Label welcomeLabel;
    @FXML private Label adminBadge;
    @FXML private ScrollPane scrollTop;

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
    @FXML private TextField localisationStadeField;

    // --- Reservations ---
    @FXML private HBox ligneSupporterCombo;
    @FXML private HBox ligneTicketCombo;
    @FXML private VBox ligneChoixUser;
    @FXML private ComboBox<String> supporterCombo;
    @FXML private ComboBox<String> ticketCombo;
    @FXML private ComboBox<String> matchComboUser;
    @FXML private ComboBox<String> categorieCombo;

    // --- Carte match (vue utilisateur) ---
    @FXML private VBox matchInfoCard;
    @FXML private Label matchDomicileLabel;
    @FXML private Label matchExterieureLabel;
    @FXML private Label matchDateLabel;
    @FXML private Label matchStadeLabel;

    // --- Carte ticket (vue utilisateur) ---
    @FXML private VBox step2Box;
    @FXML private VBox ticketInfoCard;
    @FXML private Label ticketCategorieLabel;
    @FXML private Label ticketZoneLabel;
    @FXML private Label ticketPrixLabel;
    @FXML private Label ticketDisponibleLabel;

    // --- Panneaux de formulaires ---
    @FXML private VBox panneauMatchs;
    @FXML private VBox panneauSupporters;
    @FXML private VBox panneauStades;
    @FXML private VBox panneauTickets;
    @FXML private VBox panneauReservations;
    @FXML private VBox panneauPaiements;
    @FXML private VBox panneauMonCompte;

    // --- Mon compte ---
    @FXML private Label compteEmailLabel;
    @FXML private TextField comptePrenomField;
    @FXML private TextField compteNomField;
    @FXML private TextField compteEquipeField;
    @FXML private PasswordField ancienMdpField;
    @FXML private PasswordField nouveauMdpField;
    @FXML private PasswordField confirmerMdpField;
    @FXML private Label compteMessageLabel;

    // --- Champs Tickets (admin) ---
    @FXML private ComboBox<String> matchTicketCombo;
    @FXML private TextField categorieTicketField;
    @FXML private TextField zoneTicketField;
    @FXML private TextField prixTicketField;
    @FXML private TextField quantiteTicketField;

    // --- Sidebar user ---
    @FXML private VBox navUser;

    // --- Boutons réservation (séparés admin/user) ---
    @FXML private HBox btnAdminReservations;
    @FXML private HBox btnUserReservations;

    // --- Zone tableau ---
    @FXML private Label tableTitle;
    @FXML private Label rowCountLabel;
    @FXML private TextField searchField;
    @FXML private TableView<String[]> tableView;

    // --- Graphiques ---
    @FXML private PieChart pieStatuts;
    @FXML private BarChart<String, Number> barMatchs;

    // --- Barre de statut ---
    @FXML private Label statusLabel;

    private final MatchDAO matchDAO = new MatchDAO();
    private final TicketDAO ticketDAO = new TicketDAO();
    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final StadeDAO stadeDAO = new StadeDAO();
    private final PaiementDAO paiementDAO = new PaiementDAO();
    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    private int selectedMatchId = -1;
    private int selectedSupporterId = -1;
    private int selectedTicketId = -1;
    private List<String[]> currentTableData = new ArrayList<>();
    private List<MatchFootball> matchesDisponibles = new ArrayList<>();
    private List<Ticket> ticketsMatch = new ArrayList<>();
    private boolean tableShowsMatches = false;

    // =========================================
    // INITIALISATION
    // =========================================

    @FXML
    public void initialize() {
        try {
            Utilisateur u = Session.getUtilisateur();
            if (u == null) return;

            welcomeLabel.setText("Bonjour, " + u.getPrenom() + " " + u.getNom());

            boolean admin = Session.isAdmin();

            if (adminBadge != null) {
                adminBadge.setVisible(admin);
                adminBadge.setManaged(admin);
            }

            pannelAdmin.setVisible(admin);
            pannelAdmin.setManaged(admin);
            navAdmin.setVisible(admin);
            navAdmin.setManaged(admin);
            statsPanel.setVisible(admin);
            statsPanel.setManaged(admin);
            if (navUser != null) { navUser.setVisible(!admin); navUser.setManaged(!admin); }

            if (searchField != null) {
                searchField.textProperty().addListener((obs, oldVal, newVal) -> filtrerTable(newVal));
            }

            if (admin) {
                try { chargerComboBoxes(); } catch (Exception e) { e.printStackTrace(); }
                try { chargerStats(); } catch (Exception e) { e.printStackTrace(); }
                try { afficherMatchs(); } catch (Exception e) { e.printStackTrace(); }
            } else {
                ligneSupporterCombo.setVisible(false);
                ligneSupporterCombo.setManaged(false);
                ligneTicketCombo.setVisible(false);
                ligneTicketCombo.setManaged(false);
                ligneChoixUser.setVisible(true);
                ligneChoixUser.setManaged(true);
                if (btnAdminReservations != null) { btnAdminReservations.setVisible(false); btnAdminReservations.setManaged(false); }
                if (btnUserReservations  != null) { btnUserReservations.setVisible(true);   btnUserReservations.setManaged(true); }

                try {
                    matchesDisponibles = matchDAO.getAllMatches();
                    for (MatchFootball m : matchesDisponibles)
                        matchComboUser.getItems().add(
                            m.getId() + " - " + m.getEquipeDomicile() + " vs " + m.getEquipeExterieure()
                            + " — " + m.getDateMatch()
                        );
                } catch (Exception e) { e.printStackTrace(); }

                try { chargerReservations(); } catch (Exception e) { e.printStackTrace(); }

                // Listener : clic sur une ligne du tableau → sélectionner le match pour achat
                try {
                    tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                        if (newVal != null && !Session.isAdmin() && tableShowsMatches)
                            selectionnerMatchDepuisTable(newVal);
                    });
                } catch (Exception e) { e.printStackTrace(); }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void chargerComboBoxes() {
        supporterCombo.getItems().clear();
        ticketCombo.getItems().clear();
        stadeCombo.getItems().clear();
        if (matchTicketCombo != null) matchTicketCombo.getItems().clear();
        for (Utilisateur u : utilisateurDAO.getAllUsers())
            supporterCombo.getItems().add(u.getId() + " - " + (u.getPrenom() + " " + u.getNom()).trim());
        ticketCombo.getItems().addAll(ticketDAO.getAllTicketsAvecMatch());
        for (Stade s : stadeDAO.getAllStades())
            stadeCombo.getItems().add(s.getId() + " - " + s.getNom());
        if (matchTicketCombo != null) {
            for (MatchFootball m : matchDAO.getAllMatches())
                matchTicketCombo.getItems().add(
                    m.getId() + " - " + m.getEquipeDomicile() + " vs " + m.getEquipeExterieure()
                    + " — " + m.getDateMatch()
                );
        }
    }

    private void chargerStats() {
        statMatchs.setText(String.valueOf(matchDAO.getAllMatches().size()));
        statSupporters.setText(String.valueOf(utilisateurDAO.countUsers()));
        statTickets.setText(String.valueOf(ticketDAO.getAllTickets().size()));
        statReservations.setText(String.valueOf(reservationDAO.getAllReservations().size()));
        statRevenu.setText(String.format("%.0f €", paiementDAO.getTotalRevenu()));
        statEnAttente.setText(String.valueOf(paiementDAO.countEnAttente()));

        // PieChart — statuts des paiements
        if (pieStatuts != null) {
            pieStatuts.getData().clear();
            paiementDAO.getStatutsRepartition().forEach((statut, nb) ->
                pieStatuts.getData().add(new PieChart.Data(statut.toUpperCase() + " (" + nb + ")", nb))
            );
        }

        // BarChart — billets vendus par match
        if (barMatchs != null) {
            barMatchs.getData().clear();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            reservationDAO.getBilletsParMatch().forEach((match, nb) ->
                series.getData().add(new XYChart.Data<>(match, nb))
            );
            barMatchs.getData().add(series);
        }
    }

    private void montrerPanneaux(VBox... aAfficher) {
        VBox[] tous = {panneauMatchs, panneauSupporters, panneauStades, panneauTickets, panneauReservations, panneauPaiements, panneauMonCompte};
        for (VBox p : tous) {
            if (p != null) { p.setVisible(false); p.setManaged(false); }
        }
        for (VBox p : aAfficher) {
            if (p != null) { p.setVisible(true); p.setManaged(true); }
        }
        if (searchField != null) searchField.clear();
    }

    private void afficherDansTable(String[] colonnes, List<String[]> donnees) {
        currentTableData = donnees;
        tableView.getColumns().clear();
        tableView.getItems().clear();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
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
        tableView.setPlaceholder(new Label("Aucune donnée trouvée."));
        tableView.getItems().addAll(donnees);
        updateRowCount(donnees.size());
    }

    private void filtrerTable(String searchText) {
        tableView.getItems().clear();
        if (searchText == null || searchText.isEmpty()) {
            tableView.getItems().addAll(currentTableData);
        } else {
            String lower = searchText.toLowerCase();
            for (String[] row : currentTableData) {
                for (String cell : row) {
                    if (cell != null && cell.toLowerCase().contains(lower)) {
                        tableView.getItems().add(row);
                        break;
                    }
                }
            }
        }
        updateRowCount(tableView.getItems().size());
    }

    private void updateRowCount(int count) {
        if (rowCountLabel != null)
            rowCountLabel.setText(count + " ligne(s)");
    }

    private String[] getSelectedRow() {
        return tableView.getSelectionModel().getSelectedItem();
    }

    private void setStatus(String message) {
        if (statusLabel != null)
            statusLabel.setText(message);
    }

    private boolean confirmerSuppression(String element) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation de suppression");
        confirm.setHeaderText(null);
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer " + element + " ?");
        Optional<ButtonType> result = confirm.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    // =========================================
    // BADGES D'EQUIPES
    // =========================================

    private static final Map<String, String[]> EQUIPES = new HashMap<>();
    static {
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
        EQUIPES.put("Liverpool",     new String[]{"LFC", "#c8102e", "white"});
    }

    private String[] getInfoEquipe(String equipe) {
        String[] info = EQUIPES.get(equipe);
        if (info != null) return info;
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
                nom.setStyle("-fx-font-size:13px; -fx-text-fill:#e8e8e8;");
                HBox hbox = new HBox(8, badge, nom);
                hbox.setAlignment(Pos.CENTER_LEFT);
                setGraphic(hbox);
                setText(null);
            }
        });
        return col;
    }

    private void afficherMatchsDansTable(List<MatchFootball> matchs) {
        currentTableData = new ArrayList<>();
        tableView.getColumns().clear();
        tableView.getItems().clear();

        TableColumn<String[], String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[0]));
        colId.setVisible(false); colId.setMaxWidth(0); colId.setMinWidth(0);

        TableColumn<String[], String> colDom = creerColonneEquipe("Équipe Domicile", 1);
        TableColumn<String[], String> colExt = creerColonneEquipe("Équipe Extérieure", 2);

        TableColumn<String[], String> colStade = new TableColumn<>("Stade");
        colStade.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[3]));

        TableColumn<String[], String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[4]));

        tableView.getColumns().addAll(colId, colDom, colExt, colStade, colDate);
        tableView.setPlaceholder(new Label("Aucun match trouvé."));

        for (MatchFootball m : matchs) {
            String[] row = new String[]{
                String.valueOf(m.getId()), m.getEquipeDomicile(), m.getEquipeExterieure(),
                m.getStadeNom() != null ? m.getStadeNom() : "",
                m.getDateMatch() != null ? m.getDateMatch().toString() : ""
            };
            currentTableData.add(row);
            tableView.getItems().add(row);
        }
        updateRowCount(matchs.size());
    }

    // =========================================
    // MATCHS
    // =========================================

    @FXML
    public void afficherMatchs() {
        if (!Session.isAdmin()) {
            tableShowsMatches = true;
            montrerPanneaux(panneauReservations);
            tableTitle.setText("🏆  Matchs disponibles — Cliquez sur un match pour acheter");
            // Recharger depuis la BD à chaque ouverture pour avoir les données à jour
            matchesDisponibles = matchDAO.getAllMatches();
            matchComboUser.getItems().clear();
            for (MatchFootball m : matchesDisponibles)
                matchComboUser.getItems().add(
                    m.getId() + " - " + m.getEquipeDomicile() + " vs " + m.getEquipeExterieure()
                    + " — " + m.getDateMatch()
                );
            afficherMatchsDansTable(matchesDisponibles);
            setStatus("Cliquez sur un match dans le tableau pour voir les billets disponibles et les prix.");
            return;
        }
        tableShowsMatches = false;
        try {
            montrerPanneaux(panneauMatchs);
            tableTitle.setText("🏆  Liste des Matchs");
            afficherMatchsDansTable(matchDAO.getAllMatches());
            setStatus("Matchs chargés avec succès.");
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
            setStatus("Match \"" + domicile + " vs " + exterieure + "\" ajouté avec succès.");
        } catch (IllegalArgumentException e) {
            showAlert("Erreur", "La date doit être au format YYYY-MM-DD.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ajouter le match.");
        }
    }

    @FXML
    public void chargerMatchSelectionne() {
        try {
            String[] row = getSelectedRow();
            if (row == null || row.length < 5) { showAlert("Erreur", "Sélectionnez un match d'abord."); return; }
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
            setStatus("Match #" + selectedMatchId + " chargé dans le formulaire.");
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
                setStatus("Match modifié avec succès.");
            } else {
                showAlert("Erreur", "Aucune modification effectuée.");
            }
        } catch (IllegalArgumentException e) {
            showAlert("Erreur", "La date doit être au format YYYY-MM-DD.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de modifier le match.");
        }
    }

    @FXML
    public void supprimerMatch() {
        try {
            String[] row = getSelectedRow();
            if (row == null) { showAlert("Erreur", "Sélectionnez un match à supprimer."); return; }
            if (!confirmerSuppression("ce match")) return;
            matchDAO.deleteMatch(Integer.parseInt(row[0]));
            chargerStats();
            afficherMatchs();
            setStatus("Match supprimé.");
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
            tableTitle.setText("👥  Liste des Utilisateurs");
            List<Utilisateur> users = utilisateurDAO.getAllUsers();
            String[] cols = {"ID", "Nom", "Email", "Équipe Favorite"};
            List<String[]> rows = new ArrayList<>();
            for (Utilisateur u : users)
                rows.add(new String[]{String.valueOf(u.getId()), (u.getPrenom() + " " + u.getNom()).trim(), u.getEmail(), u.getEquipeFavorite()});
            afficherDansTable(cols, rows);
            setStatus("Utilisateurs chargés avec succès.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'afficher les utilisateurs.");
        }
    }

    @FXML
    public void ajouterSupporter() {
        try {
            String nom = nomSupporterField.getText().trim();
            String email = emailSupporterField.getText().trim();
            String equipe = equipeFavoriteField.getText().trim();
            if (nom.isEmpty() || email.isEmpty() || equipe.isEmpty()) {
                showAlert("Erreur", "Tous les champs sont obligatoires.");
                return;
            }
            if (utilisateurDAO.emailExiste(email)) {
                showAlert("Erreur", "Cet email est déjà utilisé.");
                return;
            }
            utilisateurDAO.ajouter(new Utilisateur(nom, "", email, PasswordUtils.hasher("user123"), "user", equipe));
            clearSupporterFields();
            chargerComboBoxes();
            chargerStats();
            afficherSupporters();
            setStatus("Utilisateur \"" + nom + "\" ajouté (mot de passe par défaut : user123).");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ajouter l'utilisateur.");
        }
    }

    @FXML
    public void chargerSupporterSelectionne() {
        try {
            String[] row = getSelectedRow();
            if (row == null || row.length < 4) { showAlert("Erreur", "Sélectionnez un supporter d'abord."); return; }
            selectedSupporterId = Integer.parseInt(row[0]);
            nomSupporterField.setText(row[1]);
            emailSupporterField.setText(row[2]);
            equipeFavoriteField.setText(row[3]);
            setStatus("Utilisateur #" + selectedSupporterId + " chargé dans le formulaire.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger le supporter.");
        }
    }

    @FXML
    public void modifierSupporter() {
        try {
            if (selectedSupporterId == -1) { showAlert("Erreur", "Chargez un utilisateur avant de le modifier."); return; }
            String nom = nomSupporterField.getText().trim();
            String email = emailSupporterField.getText().trim();
            String equipe = equipeFavoriteField.getText().trim();
            if (nom.isEmpty() || email.isEmpty() || equipe.isEmpty()) {
                showAlert("Erreur", "Tous les champs sont obligatoires.");
                return;
            }
            boolean ok = utilisateurDAO.updateUtilisateur(selectedSupporterId, nom, email, equipe);
            if (ok) {
                clearSupporterFields();
                selectedSupporterId = -1;
                chargerComboBoxes();
                chargerStats();
                afficherSupporters();
                setStatus("Utilisateur modifié avec succès.");
            } else {
                showAlert("Erreur", "Aucune modification effectuée.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de modifier l'utilisateur.");
        }
    }

    @FXML
    public void supprimerSupporter() {
        try {
            String[] row = getSelectedRow();
            if (row == null) { showAlert("Erreur", "Sélectionnez un utilisateur à supprimer."); return; }
            if (!confirmerSuppression("cet utilisateur")) return;
            utilisateurDAO.deleteUtilisateur(Integer.parseInt(row[0]));
            chargerComboBoxes();
            chargerStats();
            afficherSupporters();
            setStatus("Utilisateur supprimé.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de supprimer l'utilisateur.");
        }
    }

    // =========================================
    // TICKETS
    // =========================================

    @FXML
    public void afficherTickets() {
        try {
            montrerPanneaux(panneauTickets);
            tableTitle.setText("🎟  Liste des Tickets");
            List<Ticket> tickets = ticketDAO.getAllTickets();
            String[] cols = {"ID", "Match ID", "Catégorie", "Zone / Placement", "Prix (€)", "Stock"};
            List<String[]> rows = new ArrayList<>();
            for (Ticket t : tickets)
                rows.add(new String[]{
                    String.valueOf(t.getId()), String.valueOf(t.getMatchId()),
                    t.getCategorie(), t.getZone(),
                    String.format("%.2f", t.getPrix()), String.valueOf(t.getQuantite())
                });
            afficherDansTable(cols, rows);
            setStatus("Tickets chargés avec succès.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'afficher les tickets.");
        }
    }

    @FXML
    public void ajouterTicket() {
        try {
            String matchItem = matchTicketCombo.getValue();
            String categorie = categorieTicketField.getText().trim();
            String zone = zoneTicketField.getText().trim();
            String prixText = prixTicketField.getText().trim();
            String qteText = quantiteTicketField.getText().trim();

            if (matchItem == null || categorie.isEmpty() || prixText.isEmpty() || qteText.isEmpty()) {
                showAlert("Erreur", "Match, catégorie, prix et quantité sont obligatoires.");
                return;
            }
            int matchId = Integer.parseInt(matchItem.split(" - ")[0]);
            double prix = Double.parseDouble(prixText.replace(",", "."));
            int quantite = Integer.parseInt(qteText);

            Ticket t = new Ticket(0, matchId, prix, categorie, zone, quantite);
            if (ticketDAO.addTicket(t)) {
                categorieTicketField.clear();
                zoneTicketField.clear();
                prixTicketField.clear();
                quantiteTicketField.clear();
                matchTicketCombo.setValue(null);
                chargerComboBoxes();
                chargerStats();
                afficherTickets();
                setStatus("Ticket \"" + categorie + "\" ajouté avec succès.");
            } else {
                showAlert("Erreur", "Impossible d'ajouter le ticket.");
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Prix et quantité doivent être des nombres valides.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ajouter le ticket.");
        }
    }

    @FXML
    public void supprimerTicket() {
        try {
            String[] row = getSelectedRow();
            if (row == null) { showAlert("Erreur", "Sélectionnez un ticket à supprimer."); return; }
            if (!confirmerSuppression("ce ticket")) return;
            if (ticketDAO.deleteTicket(Integer.parseInt(row[0]))) {
                chargerComboBoxes();
                chargerStats();
                afficherTickets();
                setStatus("Ticket supprimé.");
            } else {
                showAlert("Erreur", "Impossible de supprimer le ticket.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de supprimer le ticket.");
        }
    }

    // =========================================
    // STADES
    // =========================================

    @FXML
    public void afficherStades() {
        try {
            montrerPanneaux(panneauStades);
            tableTitle.setText("🏟  Liste des Stades");
            List<Stade> stades = stadeDAO.getAllStades();
            String[] cols = {"ID", "Nom", "Ville", "Capacité", "Localisation"};
            List<String[]> rows = new ArrayList<>();
            for (Stade s : stades)
                rows.add(new String[]{
                    String.valueOf(s.getId()), s.getNom(), s.getVille(),
                    String.valueOf(s.getCapacite()), s.getLocalisation()
                });
            afficherDansTable(cols, rows);
            setStatus("Stades chargés avec succès.");
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
            String localisation = localisationStadeField != null ? localisationStadeField.getText().trim() : "";
            if (nom.isEmpty() || ville.isEmpty() || capText.isEmpty()) {
                showAlert("Erreur", "Nom, ville et capacité sont obligatoires.");
                return;
            }
            if (stadeDAO.ajouter(new Stade(nom, ville, Integer.parseInt(capText), localisation))) {
                nomStadeField.clear(); villeStadeField.clear(); capaciteStadeField.clear();
                if (localisationStadeField != null) localisationStadeField.clear();
                chargerComboBoxes();
                afficherStades();
                setStatus("Stade \"" + nom + "\" ajouté avec succès.");
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "La capacité doit être un nombre entier.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ajouter le stade.");
        }
    }

    @FXML
    public void supprimerStade() {
        try {
            String[] row = getSelectedRow();
            if (row == null) { showAlert("Erreur", "Sélectionnez un stade à supprimer."); return; }
            if (!confirmerSuppression("ce stade")) return;
            if (stadeDAO.supprimer(Integer.parseInt(row[0]))) {
                chargerComboBoxes();
                afficherStades();
                setStatus("Stade supprimé.");
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
        tableShowsMatches = false;
        try {
            montrerPanneaux(panneauReservations);
            tableTitle.setText(Session.isAdmin() ? "🎫  Toutes les Réservations" : "🎫  Mes Réservations");

            // En mode utilisateur, toujours recharger la liste des matchs
            if (!Session.isAdmin() && matchComboUser != null) {
                matchesDisponibles = matchDAO.getAllMatches();
                matchComboUser.getItems().clear();
                for (MatchFootball m : matchesDisponibles)
                    matchComboUser.getItems().add(
                        m.getId() + " - " + m.getEquipeDomicile() + " vs " + m.getEquipeExterieure()
                        + " — " + m.getDateMatch()
                    );
            }

            List<Reservation> reservations = Session.isAdmin()
                ? reservationDAO.getAllReservations()
                : reservationDAO.getReservationsByUtilisateurId(Session.getUtilisateur().getId());

            currentTableData = new ArrayList<>();
            tableView.getColumns().clear();
            tableView.getItems().clear();
            tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            // Colonne ID cachée
            TableColumn<String[], String> colId = new TableColumn<>("ID");
            colId.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[0]));
            colId.setVisible(false); colId.setMaxWidth(0); colId.setMinWidth(0); colId.setPrefWidth(0);

            TableColumn<String[], String> colSupp = new TableColumn<>("Utilisateur");
            colSupp.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[1]));
            colSupp.setPrefWidth(160);

            TableColumn<String[], String> colMatch = new TableColumn<>("Match");
            colMatch.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[2]));
            colMatch.setPrefWidth(200);

            TableColumn<String[], String> colCat = new TableColumn<>("Catégorie");
            colCat.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[3]));
            colCat.setPrefWidth(90);

            TableColumn<String[], String> colQte = new TableColumn<>("Qté");
            colQte.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[4]));
            colQte.setPrefWidth(50);

            TableColumn<String[], String> colPrix = new TableColumn<>("Total");
            colPrix.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[5]));
            colPrix.setPrefWidth(80);

            TableColumn<String[], String> colDate = new TableColumn<>("Date");
            colDate.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[6]));
            colDate.setPrefWidth(100);

            TableColumn<String[], String> colStatut = new TableColumn<>("Paiement");
            colStatut.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[7]));
            colStatut.setPrefWidth(90);
            colStatut.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(String statut, boolean empty) {
                    super.updateItem(statut, empty);
                    if (empty || statut == null) { setGraphic(null); setText(null); return; }
                    Label badge = new Label(statut.toUpperCase());
                    if ("paye".equals(statut))         badge.getStyleClass().add("badge-paye");
                    else if ("annule".equals(statut))  badge.getStyleClass().add("badge-annule");
                    else                               badge.getStyleClass().add("badge-attente");
                    setGraphic(badge);
                    setText(null);
                }
            });

            if (Session.isAdmin()) {
                tableView.getColumns().addAll(colId, colSupp, colMatch, colCat, colQte, colPrix, colDate, colStatut);
            } else {
                tableView.getColumns().addAll(colId, colMatch, colCat, colQte, colPrix, colDate, colStatut);
            }
            tableView.setPlaceholder(new Label("Aucune réservation trouvée."));

            for (Reservation r : reservations) {
                String[] parts = r.getTicketInfo().split(" - ");
                String matchName = parts.length > 0 ? parts[0].trim() : r.getTicketInfo();
                String categorie = parts.length > 1 ? parts[1].trim() : "";
                String prixUnit  = parts.length > 2 ? parts[2].replace("€", "").trim() : "0";
                double total = 0;
                try { total = Double.parseDouble(prixUnit) * r.getQuantite(); } catch (Exception ignored) {}
                String[] row = new String[]{
                    String.valueOf(r.getId()),
                    r.getUtilisateurNom() != null ? r.getUtilisateurNom() : "",
                    matchName,
                    categorie,
                    String.valueOf(r.getQuantite()),
                    String.format("%.2f €", total),
                    r.getDateReservation() != null ? r.getDateReservation().toString() : "",
                    r.getStatutPaiement() != null ? r.getStatutPaiement() : "en_attente"
                };
                currentTableData.add(row);
                tableView.getItems().add(row);
            }
            updateRowCount(reservations.size());
            setStatus("Réservations chargées.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les réservations.");
        }
    }

    private void selectionnerMatchDepuisTable(String[] row) {
        if (row.length == 0 || matchComboUser == null) return;
        try {
            String idStr = row[0];
            for (String item : matchComboUser.getItems()) {
                if (item.startsWith(idStr + " - ")) {
                    if (!item.equals(matchComboUser.getValue())) {
                        matchComboUser.setValue(item);
                        onMatchUserSelectionne();
                    }
                    if (scrollTop != null) Platform.runLater(() -> scrollTop.setVvalue(0));
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onMatchUserSelectionne() {
        String matchItem = matchComboUser.getValue();
        if (matchItem == null) return;
        try {
            int matchId = Integer.parseInt(matchItem.split(" - ")[0].trim());

            // Chercher le match — recharger depuis DB si absent en mémoire
            MatchFootball match = null;
            for (MatchFootball m : matchesDisponibles) {
                if (m.getId() == matchId) { match = m; break; }
            }
            if (match == null) {
                matchesDisponibles = matchDAO.getAllMatches();
                for (MatchFootball m : matchesDisponibles) {
                    if (m.getId() == matchId) { match = m; break; }
                }
            }

            // Afficher la carte match
            if (match != null && matchInfoCard != null) {
                matchDomicileLabel.setText(match.getEquipeDomicile());
                matchExterieureLabel.setText(match.getEquipeExterieure());
                matchDateLabel.setText("📅  " + (match.getDateMatch() != null ? match.getDateMatch().toString() : ""));
                matchStadeLabel.setText("🏟  " + (match.getStadeNom() != null ? match.getStadeNom() : "Stade non défini"));
                matchInfoCard.setVisible(true);
                matchInfoCard.setManaged(true);
            }

            // Charger les catégories de tickets
            categorieCombo.getItems().clear();
            categorieCombo.setValue(null);
            if (ticketInfoCard != null) { ticketInfoCard.setVisible(false); ticketInfoCard.setManaged(false); }
            selectedTicketId = -1;
            ticketsMatch = ticketDAO.getTicketsByMatchId(matchId);

            if (ticketsMatch.isEmpty()) {
                setStatus("Aucune catégorie trouvée pour ce match. Vérifiez les tickets dans la base de données.");
                if (step2Box != null) { step2Box.setVisible(false); step2Box.setManaged(false); }
                return;
            }

            for (Ticket t : ticketsMatch) {
                int dispo = ticketDAO.getDisponible(t.getId());
                String zone = (t.getZone() != null && !t.getZone().isEmpty()) ? " — " + t.getZone() : "";
                String label = t.getId() + " - " + t.getCategorie() + zone
                    + " — " + String.format("%.2f", t.getPrix()) + "€"
                    + "  (" + dispo + " place" + (dispo > 1 ? "s" : "") + " restante" + (dispo > 1 ? "s" : "") + ")";
                categorieCombo.getItems().add(label);
            }

            if (step2Box != null) { step2Box.setVisible(true); step2Box.setManaged(true); }

        } catch (Exception e) {
            e.printStackTrace();
            setStatus("Erreur lors du chargement des catégories : " + e.getMessage());
        }
    }

    @FXML
    public void onCategorieSelectionne() {
        String categorieItem = categorieCombo.getValue();
        if (categorieItem == null) return;
        try {
            selectedTicketId = Integer.parseInt(categorieItem.split(" - ")[0].trim());

            // Trouver le ticket dans la liste locale
            Ticket ticket = null;
            for (Ticket t : ticketsMatch) {
                if (t.getId() == selectedTicketId) { ticket = t; break; }
            }

            if (ticket != null) {
                int disponible = ticketDAO.getDisponible(selectedTicketId);
                if (ticketCategorieLabel  != null) ticketCategorieLabel.setText(ticket.getCategorie());
                if (ticketZoneLabel       != null) ticketZoneLabel.setText(
                    ticket.getZone() != null && !ticket.getZone().isEmpty() ? ticket.getZone() : "—"
                );
                if (ticketPrixLabel       != null) ticketPrixLabel.setText(String.format("%.2f €", ticket.getPrix()));
                if (ticketDisponibleLabel != null) {
                    ticketDisponibleLabel.setText(String.valueOf(disponible));
                    ticketDisponibleLabel.getStyleClass().removeAll("dispo-good", "dispo-warning", "dispo-bad");
                    if (disponible > 50)      ticketDisponibleLabel.getStyleClass().add("dispo-good");
                    else if (disponible > 10) ticketDisponibleLabel.getStyleClass().add("dispo-warning");
                    else                      ticketDisponibleLabel.getStyleClass().add("dispo-bad");
                }
            }
            if (ticketInfoCard != null) { ticketInfoCard.setVisible(true); ticketInfoCard.setManaged(true); }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                    showAlert("Erreur", "Sélectionnez un supporter et un ticket.");
                    return;
                }
                supporterId = Integer.parseInt(supporterItem.split(" - ")[0]);
                ticketId = Integer.parseInt(ticketItem.split(" - ")[0]);
                String[] parts = ticketItem.split(" - ");
                montant = Double.parseDouble(parts[parts.length - 1].replace("€", ""));
            } else {
                if (selectedTicketId == -1) {
                    showAlert("Erreur", "Sélectionnez un match puis une catégorie de ticket.");
                    return;
                }
                int disponible = ticketDAO.getDisponible(selectedTicketId);
                if (disponible <= 0) {
                    showAlert("Complet", "Il n'y a plus de places disponibles pour cette catégorie.");
                    return;
                }
                supporterId = Session.getUtilisateur().getId();
                ticketId = selectedTicketId;
                Ticket ticketChoisi = null;
                for (Ticket t : ticketsMatch) {
                    if (t.getId() == selectedTicketId) { ticketChoisi = t; break; }
                }
                montant = ticketChoisi != null ? ticketChoisi.getPrix() : 0.0;

                // Vérifier combien de tickets il a déjà pour ce match
                int dejaReserve = reservationDAO.getTotalParMatch(supporterId, ticketChoisi != null ? ticketChoisi.getMatchId() : -1);
                int reste = 3 - dejaReserve;
                if (reste <= 0) {
                    showAlert("Limite atteinte", "Vous avez déjà réservé 3 tickets pour ce match (limite maximale).");
                    return;
                }

                // Demander la quantité (max = min entre reste autorisé et places disponibles)
                int maxChoix = Math.min(reste, Math.min(disponible, 3));
                List<String> choixQte = new ArrayList<>();
                for (int i = 1; i <= maxChoix; i++) choixQte.add(String.valueOf(i));
                ChoiceDialog<String> dialogQte = new ChoiceDialog<>(choixQte.get(0), choixQte);
                dialogQte.setTitle("Nombre de tickets");
                dialogQte.setHeaderText("Places disponibles : " + disponible + " | Vous pouvez encore réserver : " + reste);
                dialogQte.setContentText("Combien de tickets ?");
                Optional<String> choixNb = dialogQte.showAndWait();
                if (choixNb.isEmpty()) return;
                int quantite = Integer.parseInt(choixNb.get());

                // Demander le mode de paiement
                ChoiceDialog<String> dialogPaie = new ChoiceDialog<>("Carte bancaire", "Carte bancaire", "Especes", "Virement");
                dialogPaie.setTitle("Confirmer la réservation");
                dialogPaie.setHeaderText(String.format("Total à payer : %.2f €", montant * quantite));
                dialogPaie.setContentText("Mode de paiement :");
                Optional<String> choixPaie = dialogPaie.showAndWait();
                if (choixPaie.isEmpty()) return;

                Reservation r = new Reservation(supporterId, ticketId, quantite, Date.valueOf(LocalDate.now()));
                if (reservationDAO.ajouter(r)) {
                    List<Reservation> all = reservationDAO.getAllReservations();
                    if (!all.isEmpty()) {
                        int reservationId = all.get(all.size() - 1).getId();
                        paiementDAO.ajouter(new Paiement(
                            reservationId, montant * quantite, Date.valueOf(LocalDate.now()),
                            choixPaie.get(), "en_attente"
                        ));
                    }
                    chargerReservations();
                    if (selectedTicketId != -1) {
                        try { onCategorieSelectionne(); } catch (Exception ignored) {}
                    }
                    setStatus("Réservation effectuée pour " + quantite + " ticket(s) ! Paiement en attente de validation.");
                } else {
                    showAlert("Erreur", "Impossible d'ajouter la réservation.");
                }
                return;
            }

            Reservation r = new Reservation(supporterId, ticketId, 1, Date.valueOf(LocalDate.now()));
            if (reservationDAO.ajouter(r)) {
                List<Reservation> all = reservationDAO.getAllReservations();
                if (!all.isEmpty()) {
                    int reservationId = all.get(all.size() - 1).getId();
                    paiementDAO.ajouter(new Paiement(
                        reservationId, montant, Date.valueOf(LocalDate.now()),
                        "Carte bancaire", "en_attente"
                    ));
                }
                chargerStats();
                chargerReservations();
                setStatus("Réservation effectuée !");
            } else {
                showAlert("Erreur", "Impossible d'ajouter la réservation.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ajout de la réservation.");
        }
    }

    @FXML
    public void supprimerReservation() {
        try {
            String[] row = getSelectedRow();
            if (row == null) { showAlert("Erreur", "Sélectionnez une réservation à supprimer."); return; }
            if (!confirmerSuppression("cette réservation")) return;
            if (reservationDAO.supprimer(Integer.parseInt(row[0]))) {
                if (Session.isAdmin()) chargerStats();
                chargerReservations();
                setStatus("Réservation supprimée.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la suppression.");
        }
    }

    // =========================================
    // PAIEMENTS
    // =========================================

    @FXML
    public void chargerPaiements() {
        try {
            montrerPanneaux(panneauPaiements);
            tableTitle.setText("💳  Historique des Paiements");
            currentTableData = new ArrayList<>();

            List<Paiement> paiements = paiementDAO.getAllPaiements();

            tableView.getColumns().clear();
            tableView.getItems().clear();
            tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            TableColumn<String[], String> colId = new TableColumn<>("ID");
            colId.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[0]));
            colId.setVisible(false); colId.setMaxWidth(0); colId.setMinWidth(0);

            TableColumn<String[], String> colSupp = new TableColumn<>("Supporter");
            colSupp.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[1]));

            TableColumn<String[], String> colMatch = new TableColumn<>("Match");
            colMatch.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[2]));

            TableColumn<String[], String> colMontant = new TableColumn<>("Montant (€)");
            colMontant.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[3] + " €"));

            TableColumn<String[], String> colMode = new TableColumn<>("Mode de paiement");
            colMode.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[4]));

            TableColumn<String[], String> colStatut = new TableColumn<>("Statut");
            colStatut.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[5]));
            colStatut.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(String statut, boolean empty) {
                    super.updateItem(statut, empty);
                    if (empty || statut == null) { setGraphic(null); setText(null); return; }
                    Label badge = new Label(statut.toUpperCase());
                    String cssClass;
                    if ("paye".equals(statut.toLowerCase())) {
                        cssClass = "badge-paye";
                    } else if ("annule".equals(statut.toLowerCase())) {
                        cssClass = "badge-annule";
                    } else {
                        cssClass = "badge-attente";
                    }
                    badge.getStyleClass().add(cssClass);
                    setGraphic(badge);
                    setText(null);
                }
            });

            TableColumn<String[], String> colDate = new TableColumn<>("Date");
            colDate.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[6]));

            tableView.getColumns().addAll(colId, colSupp, colMatch, colMontant, colMode, colStatut, colDate);
            tableView.setPlaceholder(new Label("Aucun paiement trouvé."));

            for (Paiement p : paiements) {
                String[] row = new String[]{
                    String.valueOf(p.getId()), p.getUtilisateurNom(), p.getTicketInfo(),
                    String.valueOf(p.getMontant()), p.getModePaiement(),
                    p.getStatut(), p.getDatePaiement().toString()
                };
                currentTableData.add(row);
                tableView.getItems().add(row);
            }
            updateRowCount(paiements.size());
            setStatus("Paiements chargés.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les paiements.");
        }
    }

    @FXML
    public void validerPaiement() {
        try {
            String[] row = getSelectedRow();
            if (row == null) { showAlert("Erreur", "Sélectionnez un paiement à valider."); return; }
            int paiementId = Integer.parseInt(row[0]);
            paiementDAO.updateStatut(paiementId, "paye");
            chargerStats();
            chargerPaiements();
            setStatus("Paiement #" + paiementId + " validé avec succès.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de valider le paiement.");
        }
    }

    @FXML
    public void annulerPaiement() {
        try {
            String[] row = getSelectedRow();
            if (row == null) { showAlert("Erreur", "Sélectionnez un paiement à annuler."); return; }
            int paiementId = Integer.parseInt(row[0]);
            paiementDAO.updateStatut(paiementId, "annule");
            chargerStats();
            chargerPaiements();
            setStatus("Paiement #" + paiementId + " annulé.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'annuler le paiement.");
        }
    }

    // =========================================
    // MON COMPTE
    // =========================================

    @FXML
    public void afficherMonCompte() {
        try {
            montrerPanneaux(panneauMonCompte);
            tableTitle.setText("👤  Mon Compte");
            Utilisateur u = Session.getUtilisateur();
            if (u != null) {
                if (compteEmailLabel  != null) compteEmailLabel.setText(u.getEmail());
                if (comptePrenomField != null) comptePrenomField.setText(u.getPrenom());
                if (compteNomField    != null) compteNomField.setText(u.getNom());
                if (compteEquipeField != null) compteEquipeField.setText(u.getEquipeFavorite());
            }
            if (compteMessageLabel != null) compteMessageLabel.setText("");
            setStatus("Gestion de votre compte.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void modifierProfil() {
        try {
            String prenom = comptePrenomField != null ? comptePrenomField.getText().trim() : "";
            String nom    = compteNomField    != null ? compteNomField.getText().trim()    : "";
            String equipe = compteEquipeField != null ? compteEquipeField.getText().trim() : "";
            if (prenom.isEmpty() || nom.isEmpty()) {
                afficherMessageCompte("Veuillez renseigner le prénom et le nom.", false);
                return;
            }
            Utilisateur u = Session.getUtilisateur();
            if (u == null) return;
            if (utilisateurDAO.updateProfil(u.getId(), nom, prenom, equipe)) {
                u.setNom(nom);
                u.setPrenom(prenom);
                u.setEquipeFavorite(equipe);
                welcomeLabel.setText("Bonjour, " + u.getPrenom() + " " + u.getNom());
                afficherMessageCompte("Profil mis à jour avec succès.", true);
            } else {
                afficherMessageCompte("Erreur lors de la mise à jour du profil.", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            afficherMessageCompte("Erreur inattendue.", false);
        }
    }

    @FXML
    public void changerMotDePasse() {
        try {
            String ancien    = ancienMdpField    != null ? ancienMdpField.getText()    : "";
            String nouveau   = nouveauMdpField   != null ? nouveauMdpField.getText()   : "";
            String confirmer = confirmerMdpField != null ? confirmerMdpField.getText() : "";

            if (ancien.isEmpty() || nouveau.isEmpty() || confirmer.isEmpty()) {
                afficherMessageCompte("Tous les champs mot de passe sont obligatoires.", false);
                return;
            }
            if (nouveau.length() < 6) {
                afficherMessageCompte("Le nouveau mot de passe doit contenir au moins 6 caractères.", false);
                return;
            }
            if (!nouveau.equals(confirmer)) {
                afficherMessageCompte("Le nouveau mot de passe et sa confirmation ne correspondent pas.", false);
                return;
            }
            Utilisateur u = Session.getUtilisateur();
            if (u == null) return;
            if (!PasswordUtils.verifier(ancien, u.getMotDePasse())) {
                afficherMessageCompte("L'ancien mot de passe est incorrect.", false);
                return;
            }
            String nouveauHash = PasswordUtils.hasher(nouveau);
            if (utilisateurDAO.updateMotDePasse(u.getId(), nouveauHash)) {
                u.setMotDePasse(nouveauHash);
                if (ancienMdpField    != null) ancienMdpField.clear();
                if (nouveauMdpField   != null) nouveauMdpField.clear();
                if (confirmerMdpField != null) confirmerMdpField.clear();
                afficherMessageCompte("Mot de passe changé avec succès.", true);
            } else {
                afficherMessageCompte("Erreur lors du changement de mot de passe.", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            afficherMessageCompte("Erreur inattendue.", false);
        }
    }

    @FXML
    public void supprimerCompte() {
        try {
            Utilisateur u = Session.getUtilisateur();
            if (u == null) return;
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Supprimer le compte");
            confirm.setHeaderText(null);
            confirm.setContentText("Êtes-vous sûr de vouloir supprimer définitivement votre compte ? Cette action est irréversible.");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isEmpty() || result.get() != ButtonType.OK) return;
            if (utilisateurDAO.deleteUtilisateur(u.getId())) {
                Session.deconnecter();
                HelloApplication.changerScene(welcomeLabel, "login-view.fxml", "Connexion", 700, 480);
            } else {
                afficherMessageCompte("Impossible de supprimer le compte.", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            afficherMessageCompte("Erreur lors de la suppression du compte.", false);
        }
    }

    private void afficherMessageCompte(String message, boolean succes) {
        if (compteMessageLabel == null) return;
        compteMessageLabel.setText(message);
        compteMessageLabel.setStyle(succes
            ? "-fx-text-fill:#4ade80; -fx-font-weight:bold;"
            : "-fx-text-fill:#f87171; -fx-font-weight:bold;"
        );
    }

    // =========================================
    // EXPORT PDF
    // =========================================

    @FXML
    public void exporterPDF() {
        try {
            if (currentTableData.isEmpty()) {
                showAlert("Export", "Aucune réservation à exporter.");
                return;
            }
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer le rapport");
            fileChooser.setInitialFileName("reservations_" + LocalDate.now() + ".html");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Rapport HTML (*.html)", "*.html")
            );
            File fichier = fileChooser.showSaveDialog(tableView.getScene().getWindow());
            if (fichier == null) return;

            ExportService.exporterReservations(fichier, currentTableData, Session.isAdmin());
            setStatus("Rapport exporté : " + fichier.getName());

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(fichier.toURI());
            } else {
                showAlert("Export réussi", "Rapport enregistré :\n" + fichier.getAbsolutePath()
                    + "\n\nOuvrez ce fichier dans votre navigateur pour l'imprimer en PDF.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de générer le rapport : " + e.getMessage());
        }
    }

    // =========================================
    // DROITS UTILISATEUR
    // =========================================

    @FXML
    public void afficherDroits() {
        try {
            montrerPanneaux();
            tableTitle.setText("📋  Mes Droits & Accès");

            currentTableData = new ArrayList<>();
            tableView.getColumns().clear();
            tableView.getItems().clear();

            // Politique : colonnes libres (pas contraintes)
            tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

            // Colonne N°
            TableColumn<String[], String> colNum = new TableColumn<>("#");
            colNum.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[0]));
            colNum.setMinWidth(42); colNum.setMaxWidth(42); colNum.setPrefWidth(42);
            colNum.setStyle("-fx-alignment:CENTER;");

            // Colonne Fonctionnalité
            TableColumn<String[], String> colFonc = new TableColumn<>("Fonctionnalité");
            colFonc.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[1]));
            colFonc.setPrefWidth(460); colFonc.setMinWidth(260);

            // Colonne Accès avec badge coloré
            TableColumn<String[], String> colAcces = new TableColumn<>("Accès");
            colAcces.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[2]));
            colAcces.setPrefWidth(155); colAcces.setMinWidth(120); colAcces.setMaxWidth(180);
            colAcces.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(String val, boolean empty) {
                    super.updateItem(val, empty);
                    if (empty || val == null) { setGraphic(null); setText(null); return; }
                    Label badge = new Label(val);
                    badge.getStyleClass().add(val.startsWith("✅") ? "badge-paye" : "badge-annule");
                    HBox box = new HBox(badge);
                    box.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(box);
                    setText(null);
                }
            });

            tableView.getColumns().addAll(colNum, colFonc, colAcces);
            tableView.setPlaceholder(new Label("Aucun droit à afficher."));

            List<String[]> rows = new ArrayList<>();
            rows.add(new String[]{"1",  "Consulter la liste des matchs disponibles",     "✅  Autorisé"});
            rows.add(new String[]{"2",  "Voir les catégories, zones et prix des tickets", "✅  Autorisé"});
            rows.add(new String[]{"3",  "Réserver un ticket (jusqu'à 3 par match)",       "✅  Autorisé"});
            rows.add(new String[]{"4",  "Choisir le mode de paiement à la réservation",   "✅  Autorisé"});
            rows.add(new String[]{"5",  "Consulter toutes mes réservations",               "✅  Autorisé"});
            rows.add(new String[]{"6",  "Annuler une de mes réservations",                 "✅  Autorisé"});
            rows.add(new String[]{"7",  "Modifier mon profil (nom, équipe favorite)",      "✅  Autorisé"});
            rows.add(new String[]{"8",  "Changer mon mot de passe",                        "✅  Autorisé"});
            rows.add(new String[]{"9",  "Exporter mes réservations (rapport HTML)",        "✅  Autorisé"});
            rows.add(new String[]{"10", "Gérer les matchs, stades et tickets",             "❌  Admin uniquement"});
            rows.add(new String[]{"11", "Voir et valider tous les paiements",              "❌  Admin uniquement"});
            rows.add(new String[]{"12", "Gérer les comptes utilisateurs",                  "❌  Admin uniquement"});
            rows.add(new String[]{"13", "Accéder aux statistiques du tableau de bord",     "❌  Admin uniquement"});

            currentTableData = rows;
            tableView.getItems().addAll(rows);
            updateRowCount(rows.size());
            setStatus("Consultation des droits utilisateur.");
        } catch (Exception e) {
            e.printStackTrace();
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
        currentTableData.clear();
        searchField.clear();
        supporterCombo.setValue(null);
        ticketCombo.setValue(null);
        if (nomStadeField != null) {
            nomStadeField.clear();
            villeStadeField.clear();
            capaciteStadeField.clear();
            if (localisationStadeField != null) localisationStadeField.clear();
        }
        setStatus("Formulaires réinitialisés.");
    }

    @FXML
    public void seDeconnecter() {
        Session.deconnecter();
        try {
            HelloApplication.changerScene(welcomeLabel, "login-view.fxml", "Connexion", 700, 480);
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
