package com.hangman.view;

import javax.swing.*;
import java.awt.*;
/*
 * Classe EcranAccueil - Représente la fenêtre principale (Menu) du jeu du Pendu.
 * Elle hérite de JFrame pour créer une interface graphique.
 */
public class EcranAccueil extends JFrame {
    /*
     * Constructeur : Initialise la fenêtre et crée tous les composants graphiques.
     */
    public EcranAccueil() {
        setTitle("Jeu du Pendu");// Titre de la fenêtre
        setSize(500, 480);// Dimensions (Largeur, Hauteur)
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// Ferme l'application quand on clique sur la croix
        setLocationRelativeTo(null);// Centre la fenêtre sur l'écran
        setResizable(false);// Empêche le redimensionnement de la fenêtre
        // --- 2. CRÉATION DU PANEL PRINCIPAL (FOND) ---
        JPanel fond = new JPanel();
        fond.setBackground(new Color(108, 52, 131));
        fond.setLayout(new BoxLayout(fond, BoxLayout.Y_AXIS));
        fond.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));
        // --- 3. CRÉATION DES COMPOSANTS (TITRE ET SÉLECTEURS) ---
        // Titre principal
        JLabel titre = new JLabel("HANGMAN");
        titre.setFont(new Font("Arial", Font.BOLD, 42));
        titre.setForeground(Color.WHITE);
        titre.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Sélection de la Langue
        JLabel labelLangue = new JLabel("Langue :");
        labelLangue.setForeground(Color.WHITE);
        labelLangue.setAlignmentX(Component.CENTER_ALIGNMENT);

        String[] langues = {"Francais", "English"};
        JComboBox<String> comboLangue = new JComboBox<>(langues);
        comboLangue.setMaximumSize(new Dimension(200, 30));
        comboLangue.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Sélection de la Catégorie de mots
        JLabel labelCategorie = new JLabel("Categorie :");
        labelCategorie.setForeground(Color.WHITE);
        labelCategorie.setAlignmentX(Component.CENTER_ALIGNMENT);

        String[] categories = {"animaux", "sport", "pays", "fruits", "metiers"};
        JComboBox<String> comboCategorie = new JComboBox<>(categories);
        comboCategorie.setMaximumSize(new Dimension(200, 30));
        comboCategorie.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Sélection du Mode de jeu (Solo ou Duel)
        JLabel labelMode = new JLabel("Mode de jeu :");
        labelMode.setForeground(Color.WHITE);
        labelMode.setAlignmentX(Component.CENTER_ALIGNMENT);

        String[] modes = {"1 Joueur", "2 Joueurs"};
        JComboBox<String> comboMode = new JComboBox<>(modes);
        comboMode.setMaximumSize(new Dimension(200, 30));
        comboMode.setAlignmentX(Component.CENTER_ALIGNMENT);
        // --- 4. CRÉATION DES BOUTONS D'ACTION ---
        // Utilisation de la méthode utilitaire "creerBouton" définie plus bas
        JButton btnJouer   = creerBouton("Jouer",   new Color(39, 174, 96));
        JButton btnScores  = creerBouton("Scores",  new Color(230, 126, 34));
        JButton btnQuitter = creerBouton("Quitter", new Color(231, 76, 60));
        // --- 5. LOGIQUE DES ÉVÉNEMENTS (ACTION LISTENERS) ---
        
        // Clic sur le bouton "Jouer"
        btnJouer.addActionListener(e -> {
            // Récupération des options choisies par l'utilisateur
            String langue    = comboLangue.getSelectedIndex() == 0 ? "fr" : "en";
            String categorie = (String) comboCategorie.getSelectedItem();
            int modeIndex    = comboMode.getSelectedIndex();

            if (modeIndex == 0) {
                //on utilise un JTextField dans un JOptionPane
                // pour pouvoir détecter Annuler (retourne null) vs OK avec champ vide
                // ------ MODE 1 JOUEUR ------
                JTextField champNom = new JTextField();
                Object[] message = {"Ton prenom :", champNom};
                // Boîte de dialogue pour demander le nom du joueur
                int option = JOptionPane.showConfirmDialog(
                    this, message, "Joueur",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

                // ✅ Si Annuler ou fermeture de la fenêtre → on ne lance PAS le jeu
                if (option != JOptionPane.OK_OPTION) return;
                // Nettoyage et gestion du nom par défaut si vide
                String nom = champNom.getText().trim();
                if (nom.isEmpty()) nom = "Joueur";
                // Ouvre l'écran de jeu solo et ferme l'accueil
                new EcranJeu(nom, langue, categorie).setVisible(true);
                dispose();

            } else {
                // ------ MODE 2 JOUEURS ------
                // Joueur 1
                JTextField champNom1 = new JTextField();
                Object[] msg1 = {"Prenom du Joueur 1 :", champNom1};
                int opt1 = JOptionPane.showConfirmDialog(
                    this, msg1, "Joueur 1",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

                // ✅ Annuler sur joueur 1 → on ne lance PAS le jeu
                if (opt1 != JOptionPane.OK_OPTION) return;

                String nom1 = champNom1.getText().trim();
                if (nom1.isEmpty()) nom1 = "Joueur 1";

                // Joueur 2
                JTextField champNom2 = new JTextField();
                Object[] msg2 = {"Prenom du Joueur 2 :", champNom2};
                int opt2 = JOptionPane.showConfirmDialog(
                    this, msg2, "Joueur 2",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

                // ✅ Annuler sur joueur 2 → on ne lance PAS le jeu
                if (opt2 != JOptionPane.OK_OPTION) return;

                String nom2 = champNom2.getText().trim();
                if (nom2.isEmpty()) nom2 = "Joueur 2";
                // Ouvre l'écran de jeu multijoueur et ferme l'accueil
                new EcranJeu(nom1, nom2, langue, categorie).setVisible(true);
                dispose();
            }
        });
        // Clic sur le bouton "Scores" -> appelle la méthode d'affichage
        btnScores.addActionListener(e -> afficherScores());
        // Clic sur le bouton "Quitter" -> ferme proprement l'application JVM
        btnQuitter.addActionListener(e -> System.exit(0));
        
        // --- 6. ASSEMBLAGE DES COMPOSANTS DANS LE PANELS ---
        // Box.createRigidArea permet de créer des espaces vides (interstices) entre les éléments
        fond.add(titre);
        fond.add(Box.createRigidArea(new Dimension(0, 15)));
        fond.add(labelLangue);
        fond.add(Box.createRigidArea(new Dimension(0, 5)));
        fond.add(comboLangue);
        fond.add(Box.createRigidArea(new Dimension(0, 10)));
        fond.add(labelCategorie);
        fond.add(Box.createRigidArea(new Dimension(0, 5)));
        fond.add(comboCategorie);
        fond.add(Box.createRigidArea(new Dimension(0, 10)));
        fond.add(labelMode);
        fond.add(Box.createRigidArea(new Dimension(0, 5)));
        fond.add(comboMode);
        fond.add(Box.createRigidArea(new Dimension(0, 20)));
        fond.add(btnJouer);
        fond.add(Box.createRigidArea(new Dimension(0, 10)));
        fond.add(btnScores);
        fond.add(Box.createRigidArea(new Dimension(0, 10)));
        fond.add(btnQuitter);
        // Ajout du panel principal à la JFrame
        add(fond);
    }
    /*
     * Méthode utilitaire pour créer et styliser un JButton de manière uniforme.
     */
    private JButton creerBouton(String texte, Color couleur) {
        JButton btn = new JButton(texte);
        btn.setBackground(couleur);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setMaximumSize(new Dimension(200, 40));// Taille fixe pour l'harmonie visuelle
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFocusPainted(false);// Supprime le rectangle de focus moche autour du texte
        return btn;
    }
    /*
     * Se connecte à la base de données MySQL et affiche le TOP 10 des scores.
     */
    private void afficherScores() {
        // forcer le chargement du driver MySQL avant la connexion
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this,
                "Driver MySQL introuvable.\nVerifie que mysql-connector-j est dans les dependances.",
                "Erreur Driver", JOptionPane.ERROR_MESSAGE);
            return;// On arrête la méthode si le driver n'est pas là
        }
        // Étape 2 : Récupération de la connexion via ta classe modèle dédiée
        java.sql.Connection conn = com.hangman.model.BaseDeDonnees.getConnection();
        if (conn == null) {
            JOptionPane.showMessageDialog(this,
                "Impossible de se connecter a MySQL.\n" +
                "Verifie que :\n" +
                "- MySQL est demarré\n" +
                "- La base 'hangman_db' existe\n" +
                "- Le mot de passe dans BaseDeDonnees.java est correct",
                "Erreur connexion", JOptionPane.ERROR_MESSAGE);
            return;// On arrête la méthode si la base de données est inaccessible
        }
        // Étape 3 : Exécution de la requête SQL et construction du texte des scores
        try {
            java.sql.Statement st = conn.createStatement();
            // Requête pour récupérer les 10 meilleurs scores classés du plus grand au plus petit
            java.sql.ResultSet rs = st.executeQuery(
                "SELECT nom, diamants, meilleur_score FROM joueurs ORDER BY meilleur_score DESC LIMIT 10");
            StringBuilder sb = new StringBuilder("TOP SCORES\n\n");
            int rang = 1;
            // Parcours des lignes de résultats renvoyées par MySQL
            while (rs.next()) {
                sb.append(rang).append(". ")
                  .append(rs.getString("nom")).append(" — ")
                  .append(rs.getInt("meilleur_score")).append(" pts | ")
                  .append(rs.getInt("diamants")).append(" diamants\n");
                rang++;
            }
            // Si la boucle n'a pas tourné (rang est resté à 1), la table est vide
            if (rang == 1) sb.append("Aucun score encore !");
            // Affichage final des scores dans une pop-up d'information
            JOptionPane.showMessageDialog(this, sb.toString(), "Scores", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            // Gestion d'une éventuelle erreur de syntaxe SQL ou de lecture réseau
            JOptionPane.showMessageDialog(this,
                "Erreur lecture scores : " + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}