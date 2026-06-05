package com.hangman.model;

import java.sql.*;

/*
 * Classe Joueur — représente un joueur du jeu du Pendu.
 * Gère les diamants, le score et la sauvegarde dans MySQL.
 */
public class Joueur {

    private String nom;// Prénom du joueur
    private int diamants;// Nombre de diamants actuels
    private int score;// Score de la session
    private int meilleurScore;// Meilleur score enregistré
    
    /*
     * Constructeur — crée un nouveau joueur avec 100 diamants de départ.
     * @param nom — le prénom du joueur
     */
    public Joueur(String nom) {
        this.nom = nom;
        this.diamants = 100;// Chaque joueur commence avec 100 💎
        this.score = 0;
        this.meilleurScore = 0;
    }

    // Gagner des diamants après victoire
    /*
     * Ajoute des diamants au joueur après une victoire.
     * Met à jour le meilleur score si dépassé.
     * @param quantite — nombre de diamants à ajouter
     */
    public void gagnerDiamants(int quantite) {
        this.diamants += quantite;
        this.score += quantite;
        if (this.score > this.meilleurScore) {
            this.meilleurScore = this.score;
        }
    }

    // Dépenser des diamants
    /*
     * Dépense des diamants (ex: révéler une lettre coûte 5 💎).
     * @param quantite — nombre de diamants à dépenser
     * @return true si le joueur a assez de diamants, false sinon
     */
    public boolean depanserDiamants(int quantite) {
        if (this.diamants >= quantite) {
            this.diamants -= quantite;
            return true;
        }
        return false;  // Pas assez de diamants
    }

    // Ajouter des points
    /*
     * Ajoute des points au score du joueur.
     * @param points — points à ajouter
     */
    public void ajouterScore(int points) {
        this.score += points;
        if (this.score > this.meilleurScore) {
            this.meilleurScore = this.score;
        }
    }

    // ✅ Sauvegarder dans MySQL
    /*
     * Sauvegarde ou met à jour le joueur dans la base MySQL.
     * Utilise ON DUPLICATE KEY pour éviter les doublons.
     */
    public void sauvegarder() {
        Connection conn = BaseDeDonnees.getConnection();
        if (conn == null) return;

        String sql = "INSERT INTO joueurs (nom, diamants, meilleur_score) " +
                     "VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE " +
                     "diamants = VALUES(diamants), " +
                     "meilleur_score = VALUES(meilleur_score)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nom);
            stmt.setInt(2, diamants);
            stmt.setInt(3, meilleurScore);
            stmt.executeUpdate();
            System.out.println("✅ Joueur sauvegardé : " + nom);
        } catch (SQLException e) {
            System.out.println("❌ Erreur sauvegarde : " + e.getMessage());
        }
    }

    // ✅ Charger depuis MySQL 
     /*
     * Charge un joueur existant depuis MySQL.
     * Si le joueur n'existe pas, crée un nouveau joueur.
     * @param nom — le prénom du joueur à charger
     * @return le joueur chargé ou un nouveau joueur
     */
    public static Joueur charger(String nom) {
        Connection conn = BaseDeDonnees.getConnection();
        if (conn == null) return new Joueur(nom);

        String sql = "SELECT * FROM joueurs WHERE nom = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nom);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Joueur trouvé — charger ses données
                Joueur j = new Joueur(rs.getString("nom"));
                j.setDiamants(rs.getInt("diamants"));
                j.setMeilleurScore(rs.getInt("meilleur_score"));
                System.out.println("✅ Joueur chargé : " + nom);
                return j;
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur chargement : " + e.getMessage());
        }
        // Joueur non trouvé — nouveau joueur
        System.out.println("ℹ️ Nouveau joueur créé : " + nom);
        return new Joueur(nom);
    }

    // Getters & Setters
    public String getNom()              { return nom; }
    public int getDiamants()            { return diamants; }
    public int getScore()               { return score; }
    public int getMeilleurScore()       { return meilleurScore; }
    public void setDiamants(int d)      { this.diamants = d; }
    public void setScore(int s)         { this.score = s; }
    public void setMeilleurScore(int m) { this.meilleurScore = m; }
}