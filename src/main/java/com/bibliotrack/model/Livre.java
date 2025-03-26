package com.bibliotrack.model;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant un livre dans le système de gestion de bibliothèque
 */
public class Livre {
    // Attributs de la classe correspondant aux colonnes de la table LIVRE
    private int isbn;               // Identifiant unique du livre
    private String titre;           // Titre du livre
    private String auteur;          // Auteur du livre
    private Categorie categorie;    // Catégorie du livre (enum)
    private int nombrePages;        // Nombre de pages du livre
    private int nombreExemplaires;  // Nombre d'exemplaires disponibles
    private LocalDate dateCreation; // Date de création/ajout du livre

    /**
     * Constructeur par défaut nécessaire pour certaines opérations
     */
    public Livre() {
    }


    // ==================== GETTERS & SETTERS ====================

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

    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
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

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }



    /**
     * Enumération des catégories possibles pour un livre
     * Correspond à l'ENUM dans la base de données
     */
    public enum Categorie {
        // Valeurs possibles avec leur libellé correspondant
        ROMAN("Roman"),
        SCIENCE_FICTION("Science-Fiction"),
        // ... autres catégories ...
        AUTRE("Autre");

        private final String libelle;  // Libellé affichable de la catégorie

        /**
         * Constructeur de l'énumération
         * @param libelle le libellé à afficher
         */
        Categorie(String libelle) {
            this.libelle = libelle;
        }

        /**
         * @return le libellé de la catégorie
         */
        public String getLibelle() {
            return libelle;
        }

        /**
         * Convertit un String en valeur d'énumération
         * @param text le texte à convertir
         * @return la Categorie correspondante (ROMAN par défaut)
         */
        public static Categorie fromString(String text) {
            for (Categorie c : Categorie.values()) {
                if (c.libelle.equalsIgnoreCase(text)) {
                    return c;
                }
            }
            return ROMAN; // Valeur par défaut si non trouvé
        }
    }

    /**
     * Constructeur principal pour créer un livre
     * @param isbn identifiant unique
     * @param titre titre du livre
     * @param auteur auteur du livre
     * @param categorie catégorie du livre
     * @param nombrePages nombre de pages
     * @param nombreExemplaires nombre d'exemplaires
     * @param dateCreation date de création/ajout
     */
    public Livre(int isbn, String titre, String auteur, Categorie categorie,
                 int nombrePages, int nombreExemplaires, LocalDate dateCreation) {
        this.isbn = isbn;
        this.titre = titre;
        this.auteur = auteur;
        this.categorie = categorie;
        this.nombrePages = nombrePages;
        this.nombreExemplaires = nombreExemplaires;
        this.dateCreation = dateCreation;
    }

    // ==================== GETTERS & SETTERS ====================
    // Méthodes standard pour accéder et modifier les attributs

    /**
     * Établit une connexion à la base de données
     * @return Connection objet de connexion JDBC
     * @throws SQLException en cas d'erreur de connexion
     */
    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost/bibliodb";
        String username = "root";
        String password = "";

        try {
            // Chargement du driver JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Création de la connexion
            return DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC non trouvé", e);
        }
    }

    // ==================== METHODES METIER ====================

    /**
     * Ajoute le livre courant dans la base de données
     */
    public void ajouterLivre() {
        // Requête SQL paramétrée pour l'insertion
        String query = "INSERT INTO LIVRE (isbn, titre, auteur, categorie, nombre_pages, nombre_exemplaires, date_creation) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        // Try-with-resources pour garantir la fermeture des ressources
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Remplissage des paramètres de la requête
            stmt.setInt(1, isbn);
            stmt.setString(2, titre);
            stmt.setString(3, auteur);
            stmt.setString(4, categorie.getLibelle());
            stmt.setInt(5, nombrePages);
            stmt.setInt(6, nombreExemplaires);
            stmt.setDate(7, Date.valueOf(dateCreation));

            // Exécution de la requête
            stmt.executeUpdate();
            System.out.println("Livre ajouté avec succès");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du livre: " + e.getMessage());
        }
    }

    /**
     * Supprime le livre courant de la base de données
     */
    public void supprimerLivre() {
        String query = "DELETE FROM LIVRE WHERE isbn = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, isbn);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Livre supprimé avec succès");
            } else {
                System.out.println("Aucun livre trouvé avec cet ISBN");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du livre: " + e.getMessage());
        }
    }

    /**
     * Met à jour les informations du livre dans la base de données
     */
    public void modifierInformationLivre() {
        String query = "UPDATE LIVRE SET titre = ?, auteur = ?, categorie = ?, " +
                "nombre_pages = ?, date_creation = ? WHERE isbn = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, titre);
            stmt.setString(2, auteur);
            stmt.setString(3, categorie.getLibelle());
            stmt.setInt(4, nombrePages);
            stmt.setDate(5, Date.valueOf(dateCreation));
            stmt.setInt(6, isbn);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Informations du livre mises à jour avec succès");
            } else {
                System.out.println("Aucun livre trouvé avec cet ISBN");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du livre: " + e.getMessage());
        }
    }

    /**
     * Récupère tous les livres de la base de données
     * @return List<Livre> liste des livres trouvés
     */
    public static List<Livre> getAllLivres() {
        List<Livre> livres = new ArrayList<>();
        String query = "SELECT * FROM LIVRE";

        try (Connection conn = new Livre().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Parcours des résultats et création des objets Livre
            while (rs.next()) {
                Livre livre = new Livre(
                        rs.getInt("isbn"),
                        rs.getString("titre"),
                        rs.getString("auteur"),
                        Categorie.fromString(rs.getString("categorie")),
                        rs.getInt("nombre_pages"),
                        rs.getInt("nombre_exemplaires"),
                        rs.getDate("date_creation").toLocalDate()
                );
                livres.add(livre);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la liste des livres: " + e.getMessage());
        }
        return livres;
    }

    @Override
    public String toString() {
        return titre + " (" + auteur + ")";
    }

    /**
     * Affiche la liste des livres dans la console
     */
    public static void afficherListeLivres() {
        String query = "SELECT isbn, titre, auteur FROM LIVRE";

        try (Connection conn = new Livre().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("Liste des livres de la bibliothèque :");
            System.out.println("-------------------------------------");
            int i = 1;

            while (rs.next()) {
                System.out.println(i + ") ISBN: " + rs.getInt("isbn"));
                System.out.println("   Titre: " + rs.getString("titre"));
                System.out.println("   Auteur: " + rs.getString("auteur"));
                System.out.println("-------------------------------------");
                i++;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la liste des livres: " + e.getMessage());
        }
    }

    /**
     * Affiche les informations détaillées du livre courant
     */
    public void afficherInformationLivre() {
        String query = "SELECT * FROM LIVRE WHERE isbn = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, isbn);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("\nInformations détaillées du livre :");
                System.out.println("-------------------------------------");
                System.out.println("ISBN: " + rs.getInt("isbn"));
                System.out.println("Titre: " + rs.getString("titre"));
                System.out.println("Auteur: " + rs.getString("auteur"));
                System.out.println("Catégorie: " + rs.getString("categorie"));
                System.out.println("Nombre de pages: " + rs.getInt("nombre_pages"));
                System.out.println("Nombre d'exemplaires: " + rs.getInt("nombre_exemplaires"));
                System.out.println("Date de création: " + rs.getDate("date_creation"));
                System.out.println("-------------------------------------");
            } else {
                System.out.println("Aucun livre trouvé avec cet ISBN");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des informations du livre: " + e.getMessage());
        }
    }
}