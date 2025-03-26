package com.bibliotrack.controller;

import com.bibliotrack.model.Exemplaire;
import com.bibliotrack.model.Livre;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class MainController {
    // Déclarations des éléments d'interface utilisateur
    @FXML private TabPane mainTabPane;  // Le panneau de tabulation principal
    @FXML private Label connectionStatus;  // Étiquette pour afficher le statut de connexion à la base de données

    // Onglet Livres
    @FXML private Tab livresTab;  // L'onglet Livres
    @FXML private TableView<Livre> livresTable;  // Table pour afficher les livres
    @FXML private TableColumn<Livre, Integer> isbnColumn;  // Colonne pour l'ISBN
    @FXML private TableColumn<Livre, String> titreColumn;  // Colonne pour le titre du livre
    @FXML private TableColumn<Livre, String> auteurColumn;  // Colonne pour l'auteur du livre

    // Onglet Exemplaires
    @FXML private Tab exemplairesTab;  // L'onglet Exemplaires
    @FXML private TableView<Exemplaire> exemplairesTable;  // Table pour afficher les exemplaires
    @FXML private TableColumn<Exemplaire, Integer> numExemplaireColumn;  // Colonne pour le numéro d'exemplaire
    @FXML private TableColumn<Exemplaire, String> etatColumn;  // Colonne pour l'état de l'exemplaire
    @FXML private TableColumn<Exemplaire, String> disponibleColumn;  // Colonne pour la disponibilité de l'exemplaire
    @FXML private TableColumn<Exemplaire, String> livreColumn;  // Colonne pour le livre associé à l'exemplaire

    // Méthode d'initialisation appelée lors du démarrage de l'application
    @FXML
    public void initialize() {
        // Vérifier la connexion à la base de données
        checkDatabaseConnection();

        // Configurer les tables (Livres et Exemplaires)
        setupLivresTable();
        setupExemplairesTable();

        // Charger les données dans les tables
        loadLivresData();
        loadExemplairesData();
    }

    // Vérifie la connexion à la base de données MySQL
    private void checkDatabaseConnection() {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost/bibliodb", "root", "")) {
            // Si la connexion est réussie, mettre à jour l'interface utilisateur
            connectionStatus.setText("Connecté à la base de données");
            connectionStatus.setStyle("-fx-text-fill: green;");
            System.out.println("Connexion réussie");
        } catch (SQLException e) {
            // Si la connexion échoue, afficher un message d'erreur
            connectionStatus.setText("Échec de connexion à la base de données");
            connectionStatus.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }

    // Configure les colonnes de la table des livres
    private void setupLivresTable() {
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        titreColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        auteurColumn.setCellValueFactory(new PropertyValueFactory<>("auteur"));
    }

    // Configure les colonnes de la table des exemplaires
    private void setupExemplairesTable() {
        numExemplaireColumn.setCellValueFactory(new PropertyValueFactory<>("numeroExemplaire"));
        etatColumn.setCellValueFactory(new PropertyValueFactory<>("etat"));
        disponibleColumn.setCellValueFactory(new PropertyValueFactory<>("disponible"));
        livreColumn.setCellValueFactory(new PropertyValueFactory<>("livre"));
    }

    // Charge les données des livres depuis la base de données et les affiche dans la table
    private void loadLivresData() {
        List<Livre> livres = Livre.getAllLivres();  // Récupérer tous les livres de la base de données
        ObservableList<Livre> livresData = FXCollections.observableArrayList(livres);  // Convertir la liste en ObservableList
        livresTable.setItems(livresData);  // Remplir la table avec les données des livres
    }

    // Charge les données des exemplaires depuis la base de données et les affiche dans la table
    private void loadExemplairesData() {
        List<Exemplaire> exemplaires = Exemplaire.getAllExemplaires();  // Récupérer tous les exemplaires de la base de données
        ObservableList<Exemplaire> exemplairesData = FXCollections.observableArrayList(exemplaires);  // Convertir la liste en ObservableList
        exemplairesTable.setItems(exemplairesData);  // Remplir la table avec les données des exemplaires
    }
}
