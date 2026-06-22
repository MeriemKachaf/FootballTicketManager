# Diagrammes UML — Football Ticket Manager

## 1. Diagramme de classes

```mermaid
classDiagram
    %% ── Application ──────────────────────────────────────────
    class HelloApplication {
        -Stage primaryStage$
        +changerScene(node, fxml, titre, width, height)$
        +main(args)
    }

    %% ── Session ───────────────────────────────────────────────
    class Session {
        -Utilisateur utilisateurConnecte$
        +connecter(u)$
        +deconnecter()$
        +getUtilisateur() Utilisateur$
        +isAdmin() boolean$
    }

    %% ── Modeles ───────────────────────────────────────────────
    class Utilisateur {
        -int id
        -String nom
        -String prenom
        -String email
        -String motDePasse
        -String role
        -String equipeFavorite
        +getId() int
        +getEmail() String
        +getRole() String
    }
    class Stade {
        -int id
        -String nom
        -String ville
        -int capacite
        -String localisation
        +getId() int
        +getNom() String
    }
    class MatchFootball {
        -int id
        -String equipeDomicile
        -String equipeExterieure
        -int stadeId
        -String stadeNom
        -Date dateMatch
        +getId() int
        +getStadeNom() String
    }
    class Ticket {
        -int id
        -int matchId
        -double prix
        -String categorie
        -String zone
        -int quantite
        +getId() int
        +getPrix() double
        +getCategorie() String
    }
    class Reservation {
        -int id
        -int utilisateurId
        -int ticketId
        -int quantite
        -Date dateReservation
        -String statutPaiement
        +getId() int
        +getStatutPaiement() String
    }
    class Paiement {
        -int id
        -int reservationId
        -double montant
        -Date datePaiement
        -String modePaiement
        -String statut
        +getId() int
        +getMontant() double
        +getStatut() String
    }

    %% ── DAO ───────────────────────────────────────────────────
    class DatabaseConnection {
        -String URL$
        -String USER$
        -String PASSWORD$
        +getConnection() Connection$
    }
    class UtilisateurDAO {
        +authentifier(email, motDePasse) Utilisateur
        +emailExiste(email) boolean
        +ajouter(u) int
        +getAllUsers() List
        +updateProfil(id, nom, prenom, equipe) boolean
        +updateMotDePasse(id, hash) boolean
        +deleteUtilisateur(id) boolean
    }
    class MatchDAO {
        +getAllMatches() List
        +addMatch(m)
        +updateMatch(m) boolean
        +deleteMatch(id)
    }
    class TicketDAO {
        +getAllTickets() List
        +getTicketsByMatchId(matchId) List
        +getDisponible(ticketId) int
        +addTicket(t) boolean
        +deleteTicket(id) boolean
    }
    class StadeDAO {
        +getAllStades() List
        +ajouter(s) boolean
        +supprimer(id) boolean
    }
    class ReservationDAO {
        +getAllReservations() List
        +getReservationsByUtilisateurId(id) List
        +ajouter(r) boolean
        +supprimer(id) boolean
        +existeDeja(utilisateurId, ticketId) boolean
        +getTotalParMatch(utilisateurId, matchId) int
        +getBilletsParMatch() Map
    }
    class PaiementDAO {
        +getAllPaiements() List
        +ajouter(p) boolean
        +updateStatut(id, statut) boolean
        +getTotalRevenu() double
        +countEnAttente() int
        +getStatutsRepartition() Map
    }
    class JournalDAO {
        +enregistrer(email, action, detail, statut)
        +getJournal() List
    }

    %% ── Utilitaires ───────────────────────────────────────────
    class PasswordUtils {
        -int ITERATIONS$
        -int KEY_LENGTH$
        -String ALGORITHM$
        +hasher(motDePasse) String$
        +verifier(motDePasse, hashStocke) boolean$
        +validerComplexite(motDePasse) String$
    }
    class ExportService {
        +exporterReservations(fichier, donnees, isAdmin)$
        -escapeHtml(s) String$
    }

    %% ── Controleurs ───────────────────────────────────────────
    class LoginController {
        -int MAX_TENTATIVES$
        -long DUREE_BLOCAGE_MS$
        -Map tentatives
        -Map blocages
        +seConnecter()
        +allerInscription()
    }
    class InscriptionController {
        +sInscrire()
        +allerLogin()
    }
    class MainController {
        +initialize()
        +afficherMatchs()
        +ajouterMatch()
        +modifierMatch()
        +supprimerMatch()
        +ajouterReservation()
        +supprimerReservation()
        +chargerPaiements()
        +validerPaiement()
        +annulerPaiement()
        +afficherJournal()
        +changerMotDePasse()
        +modifierProfil()
        +supprimerCompte()
        +exporterPDF()
    }

    HelloApplication ..> LoginController : charge
    HelloApplication ..> InscriptionController : charge
    HelloApplication ..> MainController : charge

    LoginController ..> Session : connecter()
    MainController ..> Session : getUtilisateur()

    LoginController --> UtilisateurDAO
    LoginController --> JournalDAO
    InscriptionController --> UtilisateurDAO
    InscriptionController --> JournalDAO
    MainController --> MatchDAO
    MainController --> TicketDAO
    MainController --> StadeDAO
    MainController --> ReservationDAO
    MainController --> PaiementDAO
    MainController --> UtilisateurDAO
    MainController --> JournalDAO
    MainController ..> ExportService

    UtilisateurDAO --> DatabaseConnection
    MatchDAO --> DatabaseConnection
    TicketDAO --> DatabaseConnection
    StadeDAO --> DatabaseConnection
    ReservationDAO --> DatabaseConnection
    PaiementDAO --> DatabaseConnection
    JournalDAO --> DatabaseConnection

    UtilisateurDAO ..> Utilisateur
    MatchDAO ..> MatchFootball
    TicketDAO ..> Ticket
    StadeDAO ..> Stade
    ReservationDAO ..> Reservation
    PaiementDAO ..> Paiement

    LoginController ..> PasswordUtils
    InscriptionController ..> PasswordUtils
    MainController ..> PasswordUtils

    Session --> Utilisateur

    MatchFootball "N" --> "1" Stade : se joue dans
    Ticket "N" --> "1" MatchFootball : concerne
    Reservation "N" --> "1" Utilisateur : appartient a
    Reservation "N" --> "1" Ticket : porte sur
    Paiement "1" --> "1" Reservation : paye
```

---

## 2. Diagramme de cas d'utilisation

```mermaid
flowchart LR
    V(["Visiteur"])
    U(["Utilisateur"])
    A(["Administrateur"])

    subgraph Auth["Authentification"]
        L["Se connecter"]
        I["S'inscrire"]
    end

    subgraph EU["Espace Utilisateur"]
        M["Consulter les matchs"]
        ST["Selectionner match et categorie"]
        R["Reserver un ticket (max 3/match)"]
        PM["Choisir le mode de paiement"]
        MR["Consulter ses reservations"]
        AR["Annuler une reservation"]
        EX["Exporter ses reservations HTML"]
        PR["Modifier son profil"]
        MP["Changer son mot de passe"]
        DC["Supprimer son compte"]
    end

    subgraph EA["Espace Administrateur"]
        GM["Gerer les matchs CRUD"]
        GT["Gerer les tickets CRUD"]
        GS["Gerer les stades CRUD"]
        GU["Gerer les utilisateurs CRUD"]
        RA["Consulter toutes les reservations"]
        PA["Valider / Annuler un paiement"]
        JA["Journal d'activite"]
        DB["Tableau de bord"]
    end

    V --> L & I

    U --> L & M & ST & R & MR & AR & EX & PR & MP & DC

    ST -.->|include| M
    R -.->|include| ST
    R -.->|include| PM
    EX -.->|extend| MR

    A --> L & GM & GT & GS & GU & RA & PA & JA & DB & EX

    GT -.->|include| GM
    PA -.->|include| RA
```

---

## 3. Sequence — Connexion

```mermaid
sequenceDiagram
    actor U as Utilisateur
    participant LC as LoginController
    participant DAO as UtilisateurDAO
    participant DB as BDD utilisateur
    participant JDAO as JournalDAO
    participant JDB as BDD journal
    participant PU as PasswordUtils
    participant S as Session
    participant MC as MainController

    U->>LC: saisit email + mot de passe et clique Se connecter
    activate LC

    alt champs vides
        LC-->>U: Veuillez remplir tous les champs.
    else compte bloque moins de 2 min
        LC-->>U: Compte bloque. Reessayez dans X secondes.
    else verification normale
        LC->>DAO: authentifier(email, motDePasse)
        activate DAO
        DAO->>DB: SELECT * FROM utilisateur WHERE email = ?
        activate DB
        DB-->>DAO: ResultSet hash stocke
        deactivate DB
        DAO->>PU: verifier(motDePasse, hashStocke)
        activate PU
        Note right of PU: Decoupe le hash, recalcule PBKDF2 avec le meme sel, compare bit a bit XOR
        PU-->>DAO: true / false
        deactivate PU
        DAO-->>LC: Utilisateur / null
        deactivate DAO

        alt authentification echouee
            LC->>LC: tentatives[email]++
            alt tentatives >= 3
                LC->>LC: blocages[email] = now()
                LC->>JDAO: enregistrer COMPTE_BLOQUE ECHEC
                activate JDAO
                JDAO->>JDB: INSERT INTO journal_activite
                deactivate JDAO
                LC-->>U: Trop de tentatives. Compte bloque 2 minutes.
            else tentatives < 3
                LC->>JDAO: enregistrer CONNEXION_ECHEC ECHEC
                activate JDAO
                JDAO->>JDB: INSERT INTO journal_activite
                deactivate JDAO
                LC-->>U: Email ou mot de passe incorrect. X tentative(s) restante(s)
            end
        else authentification reussie
            LC->>LC: tentatives.remove + blocages.remove
            LC->>JDAO: enregistrer CONNEXION SUCCES
            activate JDAO
            JDAO->>JDB: INSERT INTO journal_activite
            deactivate JDAO
            LC->>S: connecter(utilisateur)
            activate S
            S->>S: utilisateurConnecte = utilisateur
            deactivate S
            LC->>MC: changerScene(main-view.fxml)
            activate MC
            MC->>S: getUtilisateur() + isAdmin()
            S-->>MC: utilisateur + role
            MC->>MC: afficher/cacher panneaux selon le role
            MC-->>U: Affiche l'application principale
            deactivate MC
        end
    end

    deactivate LC
```

---

## 4. Sequence — Reservation d'un ticket

```mermaid
sequenceDiagram
    actor U as Utilisateur
    participant MC as MainController
    participant MDAO as MatchDAO
    participant TDAO as TicketDAO
    participant RDAO as ReservationDAO
    participant PDAO as PaiementDAO
    participant JDAO as JournalDAO
    participant DB as MySQL
    participant S as Session

    U->>MC: clique sur Matchs
    activate MC
    MC->>MDAO: getAllMatches()
    MDAO->>DB: SELECT m.*, s.nom FROM match_football m JOIN stade s ON m.stade_id = s.id
    DB-->>MDAO: liste des matchs
    MDAO-->>MC: List MatchFootball
    MC-->>U: affiche le tableau des matchs disponibles

    U->>MC: clique sur un match dans le tableau
    MC->>MC: selectionnerMatchDepuisTable(row)
    MC->>TDAO: getTicketsByMatchId(matchId)
    TDAO->>DB: SELECT * FROM ticket WHERE match_id = ?
    DB-->>TDAO: liste des tickets
    TDAO-->>MC: List Ticket

    loop pour chaque ticket
        MC->>TDAO: getDisponible(ticketId)
        TDAO->>DB: SELECT quantite - COUNT(reservations) FROM ticket...
        DB-->>TDAO: nbDisponible
        TDAO-->>MC: int
    end

    MC-->>U: affiche carte match + categories (Tribune / Loge / VIP avec places restantes)

    U->>MC: selectionne une categorie (ex VIP)
    MC->>MC: onCategorieSelectionne()
    MC-->>U: affiche carte ticket (prix, zone, indicateur dispo vert/orange/rouge)

    U->>MC: clique Ajouter au panier
    MC->>S: getUtilisateur()
    S-->>MC: utilisateur connecte
    MC->>TDAO: getDisponible(selectedTicketId)
    TDAO->>DB: calcul stock restant
    DB-->>TDAO: nbDisponible
    TDAO-->>MC: int

    alt plus de places disponibles
        MC-->>U: Il n'y a plus de places disponibles.
    end

    MC->>RDAO: getTotalParMatch(userId, matchId)
    RDAO->>DB: SELECT SUM(quantite) FROM reservation r JOIN ticket t WHERE utilisateur_id = ? AND match_id = ?
    DB-->>RDAO: nbDejaReserve
    RDAO-->>MC: int

    alt limite 3 tickets atteinte
        MC-->>U: Vous avez deja reserve 3 tickets pour ce match (limite maximale).
    end

    MC-->>U: Dialogue - Combien de tickets ? (max = min(reste autorise, places dispo))
    U->>MC: choisit la quantite (ex 2)
    MC-->>U: Dialogue - Choisir le mode de paiement (Carte / Especes / Virement)
    U->>MC: choisit le mode de paiement

    MC->>RDAO: ajouter(Reservation(userId, ticketId, quantite, date))
    RDAO->>DB: INSERT INTO reservation (utilisateur_id, ticket_id, quantite, date_reservation)
    DB-->>RDAO: OK
    RDAO-->>MC: true

    MC->>RDAO: getAllReservations()
    RDAO->>DB: SELECT pour recuperer le dernier id
    DB-->>RDAO: liste
    RDAO-->>MC: derniere reservationId

    MC->>PDAO: ajouter(Paiement(reservationId, montant, date, mode, en_attente))
    PDAO->>DB: INSERT INTO paiement (reservation_id, montant, date_paiement, mode_paiement, statut)
    DB-->>PDAO: OK
    PDAO-->>MC: true

    MC->>JDAO: enregistrer(email, RESERVATION, N ticket(s) reserve(s), SUCCES)
    JDAO->>DB: INSERT INTO journal_activite
    DB-->>JDAO: OK

    MC->>MC: chargerReservations(), rafraichit le stock
    MC-->>U: Reservation effectuee ! Paiement en attente de validation.

    deactivate MC
```

---

## 5. MLD — Base de donnees

```mermaid
erDiagram
    UTILISATEUR {
        int id PK
        varchar nom
        varchar prenom
        varchar email UK
        varchar mot_de_passe
        enum role
        varchar equipe_favorite
    }
    HISTORIQUE_MDP {
        int id PK
        int utilisateur_id FK
        varchar mot_de_passe
        datetime date_modification
    }
    JOURNAL_ACTIVITE {
        int id PK
        varchar email
        varchar action
        varchar detail
        enum statut
        datetime date
    }
    STADE {
        int id PK
        varchar nom
        varchar ville
        int capacite
        varchar localisation
    }
    MATCH_FOOTBALL {
        int id PK
        varchar equipe_domicile
        varchar equipe_exterieure
        int stade_id FK
        date date_match
    }
    TICKET {
        int id PK
        int match_id FK
        double prix
        varchar categorie
        varchar zone
        int quantite
    }
    RESERVATION {
        int id PK
        int utilisateur_id FK
        int ticket_id FK
        int quantite
        date date_reservation
    }
    PAIEMENT {
        int id PK
        int reservation_id FK
        double montant
        date date_paiement
        enum mode_paiement
        enum statut
    }

    UTILISATEUR ||--o{ HISTORIQUE_MDP : "possede (CASCADE)"
    UTILISATEUR ||--o{ RESERVATION : "effectue (CASCADE)"
    STADE ||--o{ MATCH_FOOTBALL : "accueille (RESTRICT)"
    MATCH_FOOTBALL ||--o{ TICKET : "propose (CASCADE)"
    TICKET ||--o{ RESERVATION : "est reserve par (CASCADE)"
    RESERVATION ||--|| PAIEMENT : "est paye par (CASCADE)"
```
