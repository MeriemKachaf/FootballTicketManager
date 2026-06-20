# Football Ticket Manager

Application desktop de gestion de billetterie de matchs de football, développée en Java avec JavaFX et MySQL.

**Auteure :** Meriem Kachaf — BTS SIO SLAM 2025/2026  
**Dernière mise à jour :** Juin 2026 — version finale

---

## Technologies utilisées

| Technologie | Rôle |
|-------------|------|
| Java 17 | Langage principal |
| JavaFX 21 | Interface graphique (FXML + CSS) |
| MySQL 8.0 | Base de données relationnelle |
| JDBC | Connexion Java ↔ MySQL |
| Maven | Gestion des dépendances et build |
| JUnit 5 | Tests unitaires |
| PlantUML | Diagrammes UML |
| Git / GitHub | Versionnement |

---

## Architecture — Pattern MVC + DAO

```
Vue (FXML)          → ce que l'utilisateur voit
Contrôleur (Java)   → la logique métier
DAO (Java)          → les requêtes SQL
Base de données     → MySQL
```

---

## Structure du projet

```
FootballTicketManager/
│
├── basededonnees/
│   ├── football.sql          → Schéma complet (tables + contraintes + FK)
│   └── data.sql              → Données de démonstration
│
├── doc/                      → Modélisations UML
│   ├── 01_diagramme_classes.puml
│   ├── 02_diagramme_cas_utilisation.puml
│   ├── 03_diagramme_sequence_connexion.puml
│   ├── 04_diagramme_sequence_reservation.puml
│   └── 05_MLD_base_de_donnees.puml
│
├── modelisation/
│   └── football.puml         → Diagramme de classes simplifié
│
├── src/
│   ├── main/
│   │   ├── java/com/example/footballticketmanager/
│   │   │   ├── HelloApplication.java       → Point d'entrée JavaFX
│   │   │   ├── Launcher.java               → Classe de lancement (Fat JAR)
│   │   │   ├── controller/
│   │   │   │   ├── LoginController.java    → Connexion + anti-brute-force
│   │   │   │   ├── InscriptionController.java → Création de compte
│   │   │   │   └── MainController.java     → Toutes les fonctionnalités
│   │   │   ├── dao/
│   │   │   │   ├── UtilisateurDAO.java
│   │   │   │   ├── MatchDAO.java
│   │   │   │   ├── TicketDAO.java
│   │   │   │   ├── StadeDAO.java
│   │   │   │   ├── ReservationDAO.java
│   │   │   │   ├── PaiementDAO.java
│   │   │   │   └── JournalDAO.java
│   │   │   ├── database/
│   │   │   │   └── DatabaseConnection.java → Connexion JDBC via config.properties
│   │   │   ├── model/
│   │   │   │   ├── Utilisateur.java
│   │   │   │   ├── Stade.java
│   │   │   │   ├── MatchFootball.java
│   │   │   │   ├── Ticket.java
│   │   │   │   ├── Reservation.java
│   │   │   │   └── Paiement.java
│   │   │   ├── session/
│   │   │   │   └── Session.java            → Utilisateur connecté en mémoire
│   │   │   └── util/
│   │   │       ├── PasswordUtils.java      → Hachage PBKDF2
│   │   │       └── ExportService.java      → Export HTML des réservations
│   │   │
│   │   └── resources/
│   │       ├── config.properties           → Paramètres BDD (exclu de Git)
│   │       └── view/
│   │           ├── login-view.fxml
│   │           ├── inscription-view.fxml
│   │           ├── main-view.fxml
│   │           └── style.css
│   │
│   └── test/
│       └── java/com/example/footballticketmanager/util/
│           ├── PasswordUtilsTest.java      → 6 tests JUnit 5
│           └── EmailValidatorTest.java     → 10 tests JUnit 5
│
├── DEPLOIEMENT.md            → Procédure d'installation complète
└── pom.xml                   → Configuration Maven
```

---

## Base de données

**Nom :** `football_manager`

| Table | Description |
|-------|-------------|
| `utilisateur` | Comptes (admin / user), mot de passe hashé PBKDF2 |
| `stade` | Stades avec nom, ville, capacité, localisation |
| `match_football` | Matchs (équipes, stade, date) |
| `ticket` | Tickets par match (catégorie VIP/Tribune/Standard, prix, stock) |
| `reservation` | Réservations (max 3 tickets par personne par match) |
| `paiement` | Paiements liés aux réservations (payé / en_attente / annulé) |
| `journal_activite` | Traçabilité de toutes les actions |
| `historique_mot_de_passe` | 3 derniers hashes pour empêcher la réutilisation |

---

## Fonctionnalités

### Espace Administrateur
- Tableau de bord : statistiques en temps réel, PieChart et BarChart
- Gestion des matchs (CRUD) avec badges d'équipes colorés
- Gestion des tickets (CRUD) avec calcul du stock disponible
- Gestion des stades (CRUD)
- Gestion des utilisateurs (CRUD)
- Validation / annulation des paiements
- Journal d'activité (500 dernières entrées)
- Export HTML des réservations

### Espace Utilisateur
- Consultation des matchs disponibles
- Réservation de tickets (max 3 par match, choix de la quantité et du mode de paiement)
- Indicateur de disponibilité en temps réel (vert / orange / rouge)
- Consultation de ses réservations avec statut de paiement
- Export HTML de ses réservations
- Modification du profil
- Changement de mot de passe sécurisé
- Suppression du compte

---

## Sécurité

| Mesure | Implémentation |
|--------|---------------|
| Hachage PBKDF2 | `PasswordUtils.java` — sel aléatoire + 65 536 itérations |
| Anti-brute-force | `LoginController.java` — blocage 2 min après 3 tentatives |
| Complexité du mot de passe | 12 car. min, majuscule, minuscule, caractère spécial |
| Historique mots de passe | Empêche la réutilisation des 3 derniers |
| Protection injection SQL | `PreparedStatement` dans tous les DAOs |
| Contrôle d'accès par rôle | `Session.isAdmin()` — panneaux admin cachés pour les users |
| Credentials hors du code | `config.properties` exclu de Git via `.gitignore` |
| Protection XSS | `escapeHtml()` dans `ExportService.java` |
| Journalisation | Toutes les actions tracées dans `journal_activite` |

---

## Tests unitaires

**16 tests JUnit 5** — tous verts

| Fichier | Tests |
|---------|-------|
| `PasswordUtilsTest.java` | Hash différent pour même mot de passe, vérification correcte/incorrecte, format PBKDF2 |
| `EmailValidatorTest.java` | Email valide, sans @, sans domaine, null, trop long, commençant par un point |

Lancer les tests :
```bash
mvn test
```

---

## Installation rapide

### Prérequis
- Java 17+
- MySQL 8.0+

### 1. Importer la base de données
```bash
mysql -u root -p < basededonnees/football.sql
mysql -u root -p football_manager < basededonnees/data.sql
```

### 2. Créer l'utilisateur applicatif
```sql
CREATE USER 'ftm_user'@'localhost' IDENTIFIED BY 'VotreMotDePasse';
GRANT ALL PRIVILEGES ON football_manager.* TO 'ftm_user'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Configurer la connexion
Créer `src/main/resources/config.properties` :
```properties
db.url=jdbc:mysql://localhost:3306/football_manager
db.user=ftm_user
db.password=VotreMotDePasse
```

### 4. Lancer
```bash
mvn clean package -DskipTests
java -jar target/FootballTicketManager-1.0.jar
```

> Voir `DEPLOIEMENT.md` pour la procédure complète.

---

## Comptes de démonstration

| Email | Mot de passe | Rôle |
|-------|-------------|------|
| admin@football.com | admin123 | Administrateur |
| meriem@gmail.com | user123 | Utilisateur |
| karim@gmail.com | user123 | Utilisateur |
| sophie@gmail.com | user123 | Utilisateur |
