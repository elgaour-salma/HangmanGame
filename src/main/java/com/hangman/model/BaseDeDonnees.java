package com.hangman.model;

import java.sql.*;
/*
 * Classe BaseDeDonnees — gère la connexion à la base de données MySQL.
 * Fournit les méthodes pour connecter, sauvegarder et charger les données.
 * Utilise le pattern Singleton pour garder une seule connexion ouverte.
 */
public class BaseDeDonnees {
    // URL de connexion à la base MySQL
    private static final String URL      = "jdbc:mysql://localhost:3306/hangman_db";
     // Nom d'utilisateur MySQL
    private static final String USER     = "root";
    // Mot de passe MySQL
    private static final String PASSWORD = "";
    // Connexion unique partagée (Singleton)
    private static Connection connexion = null;

    // ✅ BUG SCORES CORRIGÉ : charger le driver MySQL au chargement de la classe
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver MySQL charge.");
        } catch (ClassNotFoundException e) {
            System.out.println("Driver MySQL introuvable : " + e.getMessage());
        }
    }
     /*
     * Retourne la connexion MySQL.
     * Si la connexion n'existe pas ou est fermée, en crée une nouvelle.
     * Pattern Singleton — une seule connexion à la fois.
     * @return la connexion MySQL active, ou null si erreur
     */
    public static Connection getConnection() {
        try {
            if (connexion == null || connexion.isClosed()) {
                connexion = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connexion MySQL reussie !");
            }
        } catch (SQLException e) {
            System.out.println("Erreur connexion MySQL : " + e.getMessage());
            return null;
        }
        return connexion;
    }
    /*
     * Sauvegarde un nouveau joueur dans la table `joueurs`.
     * Si le joueur existe déjà (même nom), met à jour ses données.
     * @param joueur — le joueur à sauvegarder
     */
    public static void sauvegarderJoueur(Joueur joueur) {
        String sql = "INSERT INTO joueurs (nom, diamants, meilleur_score) VALUES (?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, joueur.getNom());
            ps.setInt(2, joueur.getDiamants());
            ps.setInt(3, joueur.getMeilleurScore());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur sauvegarde : " + e.getMessage());
        }
    }
    /*
     * Met à jour uniquement les diamants d'un joueur dans MySQL.
     * @param nom — nom du joueur à mettre à jour
     * @param diamants — nouveau nombre de diamants
     */
    public static void mettreAJourDiamants(String nom, int diamants) {
        String sql = "UPDATE joueurs SET diamants = ? WHERE nom = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, diamants);
            ps.setString(2, nom);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur mise a jour : " + e.getMessage());
        }
    }
     /*
     * Charge un joueur existant depuis la table `joueurs`.
     * @param nom — nom du joueur à charger
     * @return le joueur trouvé, ou null si introuvable
     */
    public static Joueur chargerJoueur(String nom) {
        String sql = "SELECT * FROM joueurs WHERE nom = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, nom);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                // Reconstruire l'objet Joueur depuis les données MySQL
                Joueur joueur = new Joueur(rs.getString("nom"));
                joueur.setDiamants(rs.getInt("diamants"));
                joueur.setMeilleurScore(rs.getInt("meilleur_score"));
                return joueur;
            }
        } catch (SQLException e) {
            System.out.println("Erreur chargement : " + e.getMessage());
        }
        return null;// Joueur non trouvé
    }
    /*
     * Sauvegarde une partie terminée dans la table `parties`.
     * @param joueurId  — id du joueur dans MySQL
     * @param mot       — le mot qui était à deviner
     * @param categorie — la catégorie du mot (sport, animaux...)
     * @param niveau    — le niveau de difficulté
     * @param resultat  — "gagne" ou "perdu"
     */
    public static void sauvegarderPartie(int joueurId, String mot,
                                          String categorie, String niveau,
                                          String resultat) {
        String sql = "INSERT INTO parties (joueur_id, mot, categorie, niveau, resultat) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, joueurId);
            ps.setString(2, mot);
            ps.setString(3, categorie);
            ps.setString(4, niveau);
            ps.setString(5, resultat);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur partie : " + e.getMessage());
        }
    }
    /*
     * Ferme la connexion MySQL proprement.
     * À appeler quand l'application se ferme.
     */
    public static void fermerConnexion() {
        try {
            if (connexion != null && !connexion.isClosed()) {
                connexion.close();
                System.out.println("Connexion fermee.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur fermeture : " + e.getMessage());
        }
    }
}