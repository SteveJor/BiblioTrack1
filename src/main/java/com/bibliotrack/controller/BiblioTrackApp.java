package com.bibliotrack.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BiblioTrackApp extends Application {

    // La méthode start est appelée au démarrage de l'application
    @Override
    public void start(Stage primaryStage) throws Exception {

        // Charger le fichier FXML qui définit l'interface utilisateur
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));

        // Charger le nœud racine du fichier FXML
        Parent root = loader.load();

        // Définir le titre de la fenêtre principale
        primaryStage.setTitle("BiblioTrack");

        // Créer une scène avec le nœud racine (root) et des dimensions (800x600)
        primaryStage.setScene(new Scene(root, 800, 600));

        // Afficher la fenêtre principale
        primaryStage.show();
    }

    // Méthode main qui lance l'application
    public static void main(String[] args) {

        // Lancer l'application JavaFX
        launch(args);
    }
}
