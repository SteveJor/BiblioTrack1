package com.bibliotrack.model;

import java.sql.*;
import java.time.LocalDate;

public class Livre {
    private int isbn;
    private String titre;
    private String auteur;
    private String categorie;
    private int nombrePages;
    private int nombreExemplaires;
    private LocalDate dateCreation;

    // Constructeur
    public Livre(int isbn, String titre, String auteur, String categorie, int nombrePages, int nombreExemplaires, LocalDate dateCreation) {
        this.isbn = isbn;
        this.titre = titre;
        this.auteur = auteur;
        this.categorie = categorie;
        this.nombrePages = nombrePages;
        this.nombreExemplaires = nombreExemplaires;
        this.dateCreation = dateCreation;
    }

    // Getters et Setters
    public int getIsbn() {
        return isbn;
    }
    public void setIsbn(int isbn) {
        this.isbn = isbn;
    }

    public String getTitre() {
        return titre;
    }
    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getAuteur() {
        return auteur;
    }
    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public String getCategorie() {
        return categorie;
    }
    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public int getNombrePages() {
        return nombrePages;
    }
    public void setNombrePages(int nombrePages) {
        this.nombrePages = nombrePages;
    }

    public int getNombreExemplaires() {
        return nombreExemplaires;
    }
    public void setNombreExemplaires(int nombreExemplaires) {
        this.nombreExemplaires = nombreExemplaires;
    }

    public LocalDate getDatePublication() {
        return dateCreation;
    }
    public void setDatePublication(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }

    // Méthode pour établir la connexion à la base de données
    private Connection getConnection() throws SQLException, ClassNotFoundException {
        String url = "jdbc:mysql://localhost/Bibliothèque";
        String user = "root";
        String password = "";

        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(url, user, password);
    }

    // Ajouter un livre
    public void ajouterLivre() {
        String query = "INSERT INTO Livre (isbn, titre, auteur, categorie, nombrePages, nombreExemplaires, dateCreation) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setInt(1, isbn);
            statement.setString(2, titre);
            statement.setString(3, auteur);
            statement.setString(4, categorie);
            statement.setInt(5, nombrePages);
            statement.setInt(6, nombreExemplaires);
            statement.setDate(7, Date.valueOf(dateCreation));

            statement.executeUpdate();
            System.out.println("Livre ajouté avec succès !");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Supprimer un livre
    public void supprimerLivre() {
        String query = "DELETE FROM Livre WHERE isbn = ?";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setInt(1, isbn);
            statement.executeUpdate();
            System.out.println("Livre supprimé avec succès !");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Modifier les informations d'un livre (à implémenter)
    public void modifierInformationLivre() {
        // TODO : Ajouter la logique de mise à jour d'un livre
    }

    // Afficher la liste des livres
    public void afficherListeLivres() {
        String query = "SELECT titre, auteur FROM Livre";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            System.out.println("Liste des livres de la bibliothèque :");
            int i = 1;
            while (resultSet.next()) {
                System.out.println(i + ") " + resultSet.getString("titre") + " - " + resultSet.getString("auteur"));
                System.out.println("--------------------------------------------------------------");
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Afficher les informations d'un livre spécifique
    public void afficherInformationLivre() {
        String query = "SELECT * FROM Livre WHERE isbn = ?";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setInt(1, isbn);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    System.out.println("Informations du livre sélectionné :");
                    System.out.println("ISBN : " + resultSet.getInt("isbn"));
                    System.out.println("Titre : " + resultSet.getString("titre"));
                    System.out.println("Auteur : " + resultSet.getString("auteur"));
                    System.out.println("Catégorie : " + resultSet.getString("categorie"));
                    System.out.println("Nombre de pages : " + resultSet.getInt("nombrePages"));
                    System.out.println("Nombre d'exemplaires : " + resultSet.getInt("nombreExemplaires"));
                    System.out.println("Date de creation : " + resultSet.getDate("dateCreation"));
                } else {
                    System.out.println("Aucun livre trouvé avec cet ISBN.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
