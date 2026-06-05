package com.hangman.controller;

import com.hangman.model.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/*
 * Classe GameController — le cerveau du jeu (couche Controller du MVC).
 * Fait le lien entre la logique du jeu (Model) et l'interface (View).
 * Gère les modes 1 joueur et 2 joueurs.
 */
public class GameController {

    private Partie partie;// La partie en cours
    private Joueur joueur;// Joueur 1
    private Joueur joueur2;// Joueur 2 (mode 2 joueurs)
    private boolean mode2Joueurs;// true si mode 2 joueurs actif
    private List<String> mots = new ArrayList<>();// Liste des mots chargés
    private String categorieActuelle = "normal";// Catégorie du fichier chargé
    private boolean tourJoueur1 = true;// true = joueur1 joue, false = joueur2

    // Constructeur 1 joueur
    /*
     * Constructeur mode 1 joueur.
     * @param nomJoueur — prénom du joueur
     */
    public GameController(String nomJoueur) {
        this.joueur = Joueur.charger(nomJoueur);
        this.mode2Joueurs = false;
        // ✅ S'assurer que le joueur existe dans MySQL dès le départ
        this.joueur.sauvegarder();
    }

    // Constructeur 2 joueurs
    /*
     * Constructeur mode 2 joueurs.
     * @param nomJoueur1 — prénom du joueur 1
     * @param nomJoueur2 — prénom du joueur 2
     */
    public GameController(String nomJoueur1, String nomJoueur2) {
        this.joueur = Joueur.charger(nomJoueur1);
        this.joueur2 = Joueur.charger(nomJoueur2);
        this.mode2Joueurs = true;
        this.tourJoueur1 = true;
        // ✅ S'assurer que LES DEUX joueurs existent dans MySQL dès le départ
        this.joueur.sauvegarder();
        this.joueur2.sauvegarder();
    }
    /**
     * Passe le tour au joueur 2 (mode 2 joueurs).
     */
    public void forcerJoueur2() {
        tourJoueur1 = false;
    }
    /**
     * Remet le tour au joueur 1 pour une nouvelle manche.
     */
    public void reinitialiserTour() {
        tourJoueur1 = true;
    }
    /*
     * Charge les mots depuis un fichier .txt.
     * Chaque ligne contient un mot.
     * @param cheminFichier — chemin vers le fichier de mots
     */
    public void chargerMots(String cheminFichier) {
        mots.clear();
        // Extraire la catégorie depuis le nom du fichier (ex: sport_fr.txt → sport)
        String nomFichier = cheminFichier;
        if (nomFichier.contains("/")) {
            nomFichier = nomFichier.substring(nomFichier.lastIndexOf("/") + 1);
        }
        if (nomFichier.contains("_")) {
            categorieActuelle = nomFichier.split("_")[0];
        }
        try (BufferedReader br = new BufferedReader(new FileReader(cheminFichier))) {
            String ligne;
            while ((ligne = br.readLine()) != null) {
                if (!ligne.trim().isEmpty()) mots.add(ligne.trim().toUpperCase());
            }
        } catch (IOException e) {
            // Mots de secours si le fichier est introuvable
            mots.add("HANGMAN");
            mots.add("JAVA");
            mots.add("ORDINATEUR");
        }
    }
    /*
     * Démarre une nouvelle partie avec un mot choisi aléatoirement.
     */
    public void nouvellePartie() {
        if (mots.isEmpty()) return;
        Random rand = new Random();
        String motChoisi = mots.get(rand.nextInt(mots.size()));
        this.partie = new Partie(new Mot(motChoisi));
    }
    /*
     * Joue une lettre pour le joueur actuel.
     * @param lettre — la lettre proposée
     * @return "correcte", "fausse", "gagne", "perdu" ou "fin"
     */
    public String jouerLettre(char lettre) {
        if (partie == null || !partie.isEnCours()) return "fin";

        // ✅ Capturer le joueur AVANT jouerLettre() change le tour
        Joueur joueurCeTour = getJoueurActuel();

        boolean correcte = partie.jouerLettre(lettre);

        if (partie.estGagne()) {
            // Le joueur gagne 10 diamants et 100 points
            joueurCeTour.gagnerDiamants(10);
            joueurCeTour.ajouterScore(100);
            joueurCeTour.sauvegarder();
            // ✅ Passer le joueur capturé AVANT le coup
            sauvegarderPartieMySQL(joueurCeTour, "gagne");
            return "gagne";
        }

        if (partie.estPerdu()) {
            joueurCeTour.sauvegarder();
            // ✅ Passer le joueur capturé AVANT le coup
            sauvegarderPartieMySQL(joueurCeTour, "perdu");
            return "perdu";
        }

        return correcte ? "correcte" : "fausse";
    }
    /*
     * Sauvegarde une partie terminée dans la table MySQL `parties`.
     * @param j — le joueur concerné
     * @param resultat — "gagne" ou "perdu"
     */
    private void sauvegarderPartieMySQL(Joueur j, String resultat) {
        try {
            Connection conn = BaseDeDonnees.getConnection();
            if (conn == null) {
                System.out.println("❌ Connexion MySQL null !");
                return;
            }

            // ✅ Chercher l'id du joueur
            PreparedStatement ps1 = conn.prepareStatement(
                "SELECT id FROM joueurs WHERE nom = ?");
            ps1.setString(1, j.getNom());
            ResultSet rs = ps1.executeQuery();

            if (rs.next()) {
                int joueurId = rs.getInt("id");
                System.out.println("✅ ID trouvé pour " + j.getNom() + " : " + joueurId);
                // Insérer la partie dans la table parties
                PreparedStatement ps2 = conn.prepareStatement(
                    "INSERT INTO parties " +
                    "(joueur_id, mot, categorie, niveau, resultat) " +
                    "VALUES (?, ?, ?, ?, ?)");
                ps2.setInt(1, joueurId);
                ps2.setString(2, partie.getMot().getMot());
                ps2.setString(3, categorieActuelle);
                ps2.setString(4, "normal");
                ps2.setString(5, resultat);
                ps2.executeUpdate();
                System.out.println("✅ Partie sauvegardée pour " +
                    j.getNom() + " — " + resultat);

            } else {
                // ✅ Si joueur pas trouvé → le créer d'abord puis réessayer
                System.out.println("⚠️ Joueur " + j.getNom() +
                    " pas trouvé dans MySQL — création en cours...");
                j.sauvegarder();

                // Réessayer après création
                ResultSet rs2 = ps1.executeQuery();
                if (rs2.next()) {
                    PreparedStatement ps2 = conn.prepareStatement(
                        "INSERT INTO parties " +
                        "(joueur_id, mot, categorie, niveau, resultat) " +
                        "VALUES (?, ?, ?, ?, ?)");
                    ps2.setInt(1, rs2.getInt("id"));
                    ps2.setString(2, partie.getMot().getMot());
                    ps2.setString(3, categorieActuelle);
                    ps2.setString(4, "normal");
                    ps2.setString(5, resultat);
                    ps2.executeUpdate();
                    System.out.println("✅ Partie sauvegardée après création joueur !");
                } else {
                    System.out.println("❌ Impossible de sauvegarder la partie !");
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur sauvegarde partie : " + e.getMessage());
        }
    }
    /*
     * Révèle une lettre cachée contre 5 diamants.
     * @return la lettre révélée, ou ' ' si pas assez de diamants
     */
    public char revelerLettre() {
        if (getJoueurActuel().depanserDiamants(5)) {
            return partie.getMot().revelerLettre();
        }
        return ' ';// Pas assez de diamants
    }
    /*
     * @return l'affichage du mot avec les lettres trouvées (ex: "_ H _ N G M _ N")
     */
    public String getAffichageMot() {
        if (partie == null) return "";
        return partie.getMot().afficher();
    }
    /*
     * Retourne le joueur dont c'est le tour.
     * En mode 2 joueurs : alterne entre joueur1 et joueur2.
     * @return le joueur actuel
     */
    public Joueur getJoueurActuel() {
        if (mode2Joueurs && !tourJoueur1) return joueur2;
        return joueur;
    }
    /*
     * @return le prénom du joueur dont c'est le tour
     */
    public String getNomJoueurActuel() { return getJoueurActuel().getNom(); }
    // Getters
    public boolean isMode2Joueurs()    { return mode2Joueurs; }
    public Joueur getJoueur()          { return joueur; }
    public Joueur getJoueur2()         { return joueur2; }
    public Partie getPartie()          { return partie; }
}