package com.bibliotrack.model;
import java.time.LocalDate;

public class Exemplaire {
    private int numeroExemplaire;
    private String etat;
    private boolean disponible;
    private LocalDate dateCreation;
    private Livre livre;

    // Constructeur
    public Exemplaire(int numeroExemplaire, String etat, boolean disponible, LocalDate dateCreation, Livre livre) {
        this.numeroExemplaire = numeroExemplaire;
        this.etat = etat;
        this.disponible = disponible;
        this.dateCreation = dateCreation;
        this.livre = livre;
    }

    // Getters et Setters
    public int getNumeroExemplaire() {
        return numeroExemplaire;
    }
    public void setNumeroExemplaire(int numeroExemplaire) {
        this.numeroExemplaire = numeroExemplaire;
    }

    public String getEtat() {
        return etat;
    }
    public void setEtat(String etat) {
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
}
