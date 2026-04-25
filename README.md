# Projet Client Lourd - Football Ticket Manager

## Description

Ce projet a été réalisé dans le cadre du TP de **client lourd** pour le
**BTS SIO SLAM**.

L'objectif est de développer une application de **billetterie de matchs
de football** permettant de gérer les matchs, les supporters et les
tickets à partir d'une base de données MySQL.

L'application est développée en **Java avec JavaFX** pour l'interface
graphique et utilise le modèle **DAO (Data Access Object)** pour accéder
aux données de la base.

Le projet inclut également : - la modélisation UML de la base de
données - les scripts SQL de création de la base - des données de test -
des classes Java permettant l'accès aux données

------------------------------------------------------------------------

## Structure du projet

    FootballTicketManager
    │
    ├── database
    │   ├── football.sql        → Script de création de la base de données
    │   ├── data.sql            → Données de test
    │   └── football.puml       → Diagramme UML
    │
    ├── src
    │   └── main
    │       ├── java
    │       │   └── com.example.footballticketmanager
    │       │       ├── HelloApplication.java
    │       │       ├── controller
    │       │       │   └── MainController.java
    │       │       ├── dao
    │       │       │   ├── MatchDAO.java
    │       │       │   ├── SupporterDAO.java
    │       │       │   └── TicketDAO.java
    │       │       ├── database
    │       │       │   └── DatabaseConnection.java
    │       │       └── model
    │       │           ├── MatchFootball.java
    │       │           ├── Supporter.java
    │       │           └── Ticket.java
    │       │
    │       └── resources
    │           └── view
    │               └── main-view.fxml
    │
    └── pom.xml

------------------------------------------------------------------------

## Technologies utilisées

-   **Java**
-   **JavaFX**
-   **MySQL**
-   **JDBC**
-   **Maven**
-   **PlantUML**
-   **phpMyAdmin**
-   **Git / GitHub**

------------------------------------------------------------------------

## Base de données

Nom de la base :

football_manager

Tables utilisées :

-   match_football
-   supporter
-   ticket

Relations : - un **ticket** est associé à un **match** - les
**supporters** représentent les clients du système

------------------------------------------------------------------------

## Fonctionnalités

### Gestion des matchs

L'utilisateur peut : - afficher les matchs - ajouter un match - modifier
un match - supprimer un match

### Gestion des supporters

L'utilisateur peut : - afficher les supporters - ajouter un supporter -
modifier un supporter - supprimer un supporter

### Gestion des tickets

L'utilisateur peut : - afficher les tickets

------------------------------------------------------------------------

## Accès aux données (DAO)

### Modèles

-   MatchFootball.java
-   Supporter.java
-   Ticket.java

### DAO

-   MatchDAO.java
-   SupporterDAO.java
-   TicketDAO.java

Chaque DAO permet : - récupérer les données - ajouter des
enregistrements - modifier des enregistrements - supprimer des
enregistrements

------------------------------------------------------------------------

## Connexion à la base de données

La connexion est gérée par la classe :

DatabaseConnection.java

Cette classe utilise **JDBC** pour établir la connexion avec la base
MySQL.

------------------------------------------------------------------------

## Installation et exécution

### 1. Installer les outils

-   Java JDK 21
-   IntelliJ IDEA
-   XAMPP (MySQL + phpMyAdmin)

### 2. Importer la base de données

Ouvrir phpMyAdmin :

http://localhost/phpmyadmin

Créer la base :

football_manager

Importer ensuite : 1. football.sql 2. data.sql

### 3. Lancer l'application

Dans IntelliJ, exécuter la classe :

HelloApplication.java

L'interface JavaFX s'ouvrira.

------------------------------------------------------------------------

## Diagramme UML

Le diagramme UML du projet a été réalisé avec **PlantUML** et se trouve
dans :

database/football.puml

------------------------------------------------------------------------

## Auteur

**Meriem Kachaf**
BTS SIO SLAM
Année : 2025 / 2026
