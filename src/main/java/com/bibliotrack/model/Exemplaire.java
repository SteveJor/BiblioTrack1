package com.bibliotrack.model;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant un exemplaire physique d'un livre dans le système de bibliothèque.
 * Chaque exemplaire est associé à un livre et possède ses propres caractéristiques.
 */
public class Exemplaire {
    // Attributs correspondant aux colonnes de la table EXEMPLAIRE
    private int numeroExemplaire;    // Identifiant unique auto-généré
    private EtatExemplaire etat;     // État physique de l'exemplaire
    private boolean disponible;      // Indique si l'exemplaire peut être emprunté
    private LocalDate dateCreation;  // Date d'ajout dans le système
    private Livre livre;             // Référence au livre associé

    /**
     * Enumération des états possibles d'un exemplaire
     * Correspond à l'ENUM('neuf','bon','moyen','mauvais') dans la base de données
     */
    public enum EtatExemplaire {
        NEUF("neuf"),       // Exemplaire en parfait état
        BON("bon"),         // Exemplaire en bon état avec de légers signes d'usure
        MOYEN("moyen"),     // Exemplaire moyennement usé mais utilisable
        MAUVAIS("mauvais"); // Exemplaire très usé, à remplacer

        private final String libelle; // Libellé correspondant à la valeur en base

        EtatExemplaire(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }

        /**
         * Convertit une chaîne de caractères en valeur d'énumération
         * @param text Le texte à convertir
         * @return L'état correspondant (BON par défaut si non trouvé)
         */
        public static EtatExemplaire fromString(String text) {
            for (EtatExemplaire e : EtatExemplaire.values()) {
                if (e.libelle.equalsIgnoreCase(text)) {
                    return e;
                }
            }
            return BON; // Valeur par défaut
        }
    }

    // ==================== CONSTRUCTEURS ====================

    /**
     * Constructeur par défaut nécessaire pour certaines opérations
     */
    public Exemplaire() {
    }

    /**
     * Constructeur complet pour créer un exemplaire
     * @param numeroExemplaire Identifiant unique
     * @param etat État physique
     * @param disponible Disponibilité
     * @param dateCreation Date d'ajout
     * @param livre Livre associé
     */
    public Exemplaire(int numeroExemplaire, EtatExemplaire etat, boolean disponible,
                      LocalDate dateCreation, Livre livre) {
        this.numeroExemplaire = numeroExemplaire;
        this.etat = etat;
        this.disponible = disponible;
        this.dateCreation = dateCreation;
        this.livre = livre;
    }

    // ==================== GETTERS & SETTERS ====================

    public int getNumeroExemplaire() {
        return numeroExemplaire;
    }

    public void setNumeroExemplaire(int numeroExemplaire) {
        this.numeroExemplaire = numeroExemplaire;
    }

    public EtatExemplaire getEtat() {
        return etat;
    }

    public void setEtat(EtatExemplaire etat) {
        this.etat = etat;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Livre getLivre() {
        return livre;
    }

    public void setLivre(Livre livre) {
        this.livre = livre;
    }

    // ==================== METHODES D'ACCES A LA BASE ====================

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
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC non trouvé", e);
        }
    }

    // ==================== METHODES METIER ====================

    /**
     * Ajoute l'exemplaire courant dans la base de données
     * Met à jour le numéro d'exemplaire avec la valeur générée
     */
    public void ajouterExemplaire() {
        String query = "INSERT INTO EXEMPLAIRE (etat, disponible, date_creation, isbn) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            // Paramétrage de la requête
            stmt.setString(1, etat.getLibelle());
            stmt.setBoolean(2, disponible);
            stmt.setDate(3, Date.valueOf(dateCreation));
            stmt.setInt(4, livre.getIsbn());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        this.numeroExemplaire = generatedKeys.getInt(1);
                    }
                }
                System.out.println("Exemplaire ajouté avec succès. Numéro: " + this.numeroExemplaire);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'exemplaire: " + e.getMessage());
        }
    }

    /**
     * Supprime l'exemplaire courant de la base de données
     */
    public void supprimerExemplaire() {
        String query = "DELETE FROM EXEMPLAIRE WHERE numero_exemplaire = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, numeroExemplaire);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Exemplaire supprimé avec succès");
            } else {
                System.out.println("Aucun exemplaire trouvé avec ce numéro");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'exemplaire: " + e.getMessage());
        }
    }

    /**
     * Met à jour les informations de l'exemplaire dans la base de données
     */
    public void modifierInformationExemplaire() {
        String query = "UPDATE EXEMPLAIRE SET etat = ?, disponible = ? " +
                "WHERE numero_exemplaire = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, etat.getLibelle());
            stmt.setBoolean(2, disponible);
            stmt.setInt(3, numeroExemplaire);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Informations de l'exemplaire mises à jour avec succès");
            } else {
                System.out.println("Aucun exemplaire trouvé avec ce numéro");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'exemplaire: " + e.getMessage());
        }
    }

    /**
     * Récupère tous les exemplaires de la base de données
     * @return List<Exemplaire> liste des exemplaires trouvés
     */
    public static List<Exemplaire> getAllExemplaires() {
        List<Exemplaire> exemplaires = new ArrayList<>();
        String query = "SELECT e.*, l.titre, l.auteur, l.categorie, l.nombre_pages, " +
                "l.nombre_exemplaires, l.date_creation " +
                "FROM EXEMPLAIRE e JOIN LIVRE l ON e.isbn = l.isbn";

        try (Connection conn = new Exemplaire().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                // Création du livre associé
                Livre livre = new Livre(
                        rs.getInt("isbn"),
                        rs.getString("titre"),
                        rs.getString("auteur"),
                        Livre.Categorie.fromString(rs.getString("categorie")),
                        rs.getInt("nombre_pages"),
                        rs.getInt("nombre_exemplaires"),
                        rs.getDate("date_creation").toLocalDate()
                );

                // Création de l'exemplaire
                Exemplaire exemplaire = new Exemplaire(
                        rs.getInt("numero_exemplaire"),
                        EtatExemplaire.fromString(rs.getString("etat")),
                        rs.getBoolean("disponible"),
                        rs.getDate("date_creation").toLocalDate(),
                        livre
                );

                exemplaires.add(exemplaire);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la liste des exemplaires: " + e.getMessage());
        }
        return exemplaires;
    }
    @Override
    public String toString() {
        return "Exemplaire #" + numeroExemplaire + " - " + etat.getLibelle() +
                " (" + (disponible ? "Disponible" : "Indisponible") + ")";
    }

    public String getDisponible() {
        return disponible ? "Oui" : "Non";
    }

    /**
     * Affiche la liste des exemplaires dans la console
     */
    public static void afficherListeExemplaires() {
        String query = "SELECT e.numero_exemplaire, e.etat, e.disponible, l.titre " +
                "FROM EXEMPLAIRE e JOIN LIVRE l ON e.isbn = l.isbn";

        try (Connection conn = new Exemplaire().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("Liste des exemplaires de la bibliothèque :");
            System.out.println("------------------------------------------");
            int i = 1;

            while (rs.next()) {
                System.out.println(i + ") Numéro: " + rs.getInt("numero_exemplaire"));
                System.out.println("   État: " + rs.getString("etat"));
                System.out.println("   Disponible: " + (rs.getBoolean("disponible") ? "Oui" : "Non"));
                System.out.println("   Livre: " + rs.getString("titre"));
                System.out.println("------------------------------------------");
                i++;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la liste des exemplaires: " + e.getMessage());
        }
    }

    /**
     * Affiche les informations détaillées de l'exemplaire courant
     */
    public void afficherInformationExemplaire() {
        String query = "SELECT e.*, l.titre, l.auteur " +
                "FROM EXEMPLAIRE e JOIN LIVRE l ON e.isbn = l.isbn " +
                "WHERE e.numero_exemplaire = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, numeroExemplaire);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("\nInformations détaillées de l'exemplaire :");
                System.out.println("------------------------------------------");
                System.out.println("Numéro: " + rs.getInt("numero_exemplaire"));
                System.out.println("État: " + rs.getString("etat"));
                System.out.println("Disponible: " + (rs.getBoolean("disponible") ? "Oui" : "Non"));
                System.out.println("Date de création: " + rs.getDate("date_creation"));
                System.out.println("Livre associé:");
                System.out.println("   Titre: " + rs.getString("titre"));
                System.out.println("   Auteur: " + rs.getString("auteur"));
                System.out.println("   ISBN: " + rs.getInt("isbn"));
                System.out.println("------------------------------------------");
            } else {
                System.out.println("Aucun exemplaire trouvé avec ce numéro");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des informations de l'exemplaire: " + e.getMessage());
        }
    }

    /**
     * Récupère les exemplaires disponibles pour un livre donné
     * @param isbn ISBN du livre recherché
     * @return List<Exemplaire> liste des exemplaires disponibles
     */
    public static List<Exemplaire> getExemplairesDisponiblesByLivre(int isbn) {
        List<Exemplaire> exemplaires = new ArrayList<>();
        String query = "SELECT e.*, l.titre, l.auteur, l.categorie, l.nombre_pages, " +
                "l.nombre_exemplaires, l.date_creation " +
                "FROM EXEMPLAIRE e JOIN LIVRE l ON e.isbn = l.isbn " +
                "WHERE e.isbn = ? AND e.disponible = true";

        try (Connection conn = new Exemplaire().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, isbn);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Livre livre = new Livre(
                        rs.getInt("isbn"),
                        rs.getString("titre"),
                        rs.getString("auteur"),
                        Livre.Categorie.fromString(rs.getString("categorie")),
                        rs.getInt("nombre_pages"),
                        rs.getInt("nombre_exemplaires"),
                        rs.getDate("date_creation").toLocalDate()
                );

                Exemplaire exemplaire = new Exemplaire(
                        rs.getInt("numero_exemplaire"),
                        EtatExemplaire.fromString(rs.getString("etat")),
                        rs.getBoolean("disponible"),
                        rs.getDate("date_creation").toLocalDate(),
                        livre
                );

                exemplaires.add(exemplaire);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des exemplaires disponibles: " + e.getMessage());
        }
        return exemplaires;
    }

    /**
     * Recherche un exemplaire par son numéro
     * @param numeroExemplaire Numéro de l'exemplaire recherché
     * @return Exemplaire trouvé ou null si non trouvé
     */
    public static Exemplaire trouverExemplaireParNumero(int numeroExemplaire) {
        String query = "SELECT e.*, l.titre, l.auteur, l.categorie, l.nombre_pages, " +
                "l.nombre_exemplaires, l.date_creation " +
                "FROM EXEMPLAIRE e JOIN LIVRE l ON e.isbn = l.isbn " +
                "WHERE e.numero_exemplaire = ?";

        try (Connection conn = new Exemplaire().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, numeroExemplaire);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Livre livre = new Livre(
                        rs.getInt("isbn"),
                        rs.getString("titre"),
                        rs.getString("auteur"),
                        Livre.Categorie.fromString(rs.getString("categorie")),
                        rs.getInt("nombre_pages"),
                        rs.getInt("nombre_exemplaires"),
                        rs.getDate("date_creation").toLocalDate()
                );

                return new Exemplaire(
                        rs.getInt("numero_exemplaire"),
                        EtatExemplaire.fromString(rs.getString("etat")),
                        rs.getBoolean("disponible"),
                        rs.getDate("date_creation").toLocalDate(),
                        livre
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de l'exemplaire: " + e.getMessage());
        }
        return null;
    }
}