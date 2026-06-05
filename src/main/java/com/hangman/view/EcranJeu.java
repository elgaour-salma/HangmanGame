package com.hangman.view;

import com.hangman.controller.GameController;
import javax.swing.*;
import java.awt.*;
/*
 * Classe EcranJeu - Gère la fenêtre principale du jeu en cours (Pendu).
 * Elle orchestre l'affichage du mot secret, les scores, le clavier virtuel et le dessin.
 */
public class EcranJeu extends JFrame {

    // --- VARIABLES ET COMPOSANTS ---
    private GameController controller;// Contrôleur reliant la vue au modèle (Logique métier)
    private JLabel labelMot;// Affiche le mot secret caché (ex: _ _ O _ R)
    private JLabel labelErreurs;// Affiche le compteur de fautes (ex: Erreurs : 2/7)
    private JLabel labelDiamants;// Affiche la monnaie du joueur
    private JLabel labelMessage;// Bannière de texte pour guider le joueur (gagné, perdu, etc.)
    private JLabel labelCategorie;// Rappel textuel de la catégorie et de la langue choisies
    private DessinPendu dessinPendu;// Notre panneau de dessin personnalisé (gère l'affichage du bonhomme)
    private JButton[] boutonsClavier = new JButton[26];// Tableau stockant les 26 boutons du clavier virtuel (A-Z)
    private String langue;
    private String categorie;

    // Mode 2 joueurs : chaque joueur joue son mot COMPLET indépendamment
    private boolean mode2Joueurs;// Vrai si on joue en mode Duel, Faux si Solo

    // Résultats de chaque manche
    private boolean joueur1AGagne = false;// Mémorise si le Joueur 1 a trouvé son mot secret
    private boolean joueur1AJoue  = false; // manche joueur 1 terminée ?

    // Constructeur 1 joueur (Solo)
    public EcranJeu(String nomJoueur, String langue, String categorie) {
        this.langue = langue;
        this.categorie = categorie;
        this.mode2Joueurs = false;
        controller = new GameController(nomJoueur);// Initialise le contrôleur avec un seul joueur
        initialiserJeu();
        construireInterface();
    }

    // Constructeur mode 2 joueurs (Duel)
    public EcranJeu(String nom1, String nom2, String langue, String categorie) {
        this.langue = langue;
        this.categorie = categorie;
        this.mode2Joueurs = true;
        controller = new GameController(nom1, nom2);// Initialise le contrôleur avec les deux compétiteurs
        initialiserJeu();
        construireInterface();
    }
    /*
     * Détermine le bon dictionnaire de mots textuel à charger selon la configuration choisie.
     */
    private void initialiserJeu() {
        // Sélectionne le chemin du fichier .txt dynamique (ex: "src/main/resources/words/animaux_fr.txt")
        String fichier = langue.equals("fr")
            ? "src/main/resources/words/" + categorie + "_fr.txt"
            : "src/main/resources/words/" + categorie + "_en.txt";
        controller.chargerMots(fichier);// Demande au contrôleur de lire le fichier textuel
        controller.nouvellePartie();// Pioche le premier mot de manière aléatoire
    }
    /*
     * Crée, stylise et dispose tous les éléments visuels de la fenêtre de jeu (Mise en page).
     */
    private void construireInterface() {
        // Configuration de la fenêtre de base
        setTitle("Pendu — " + controller.getNomJoueurActuel());
        setSize(700, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        // Panel principal utilisant un BorderLayout (Nord, Centre, Sud) avec des espaces de 10px
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBackground(new Color(240, 240, 255));
        main.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));// Marges de sécurité sur les bords

        // --- HAUT ---
        JPanel panelHaut = new JPanel(new GridLayout(2, 3));// Grille de 2 lignes par 3 colonnes
        panelHaut.setOpaque(false);

        labelErreurs = new JLabel("Erreurs : 0/7", SwingConstants.LEFT);
        labelErreurs.setFont(new Font("Arial", Font.BOLD, 16));

        labelMessage = new JLabel("Tour de : " + controller.getNomJoueurActuel(), SwingConstants.CENTER);
        labelMessage.setFont(new Font("Arial", Font.ITALIC, 14));
        labelMessage.setForeground(new Color(108, 52, 131));

        labelDiamants = new JLabel(
            "Diamants : " + controller.getJoueurActuel().getDiamants(),
            SwingConstants.RIGHT);
        labelDiamants.setFont(new Font("Arial", Font.BOLD, 16));
        labelDiamants.setForeground(new Color(41, 128, 185));

        labelCategorie = new JLabel(
            "Categorie : " + categorie.toUpperCase() + " | Langue : " + langue.toUpperCase(),
            SwingConstants.LEFT);
        labelCategorie.setFont(new Font("Arial", Font.PLAIN, 13));
        labelCategorie.setForeground(new Color(80, 80, 80));
        
        // Ajout des étiquettes dans la grille
        panelHaut.add(labelErreurs);
        panelHaut.add(labelMessage);
        panelHaut.add(labelDiamants);
        panelHaut.add(labelCategorie);
        panelHaut.add(new JLabel());// Case vide pour l'espacement
        panelHaut.add(new JLabel());// Case vide pour l'espacement

        // --- MILIEU ---
        JPanel panelMilieu = new JPanel(new BorderLayout(20, 0));
        panelMilieu.setOpaque(false);

        dessinPendu = new DessinPendu();// Récupère le canvas graphique du pendu

        // Label contenant les tirets du mot masqué
        labelMot = new JLabel(controller.getAffichageMot(), SwingConstants.CENTER);
        labelMot.setFont(new Font("Courier New", Font.BOLD, 36));// Police "Courier" pour aligner parfaitement les lettres
        labelMot.setForeground(new Color(108, 52, 131));
        labelMot.setOpaque(true);
        labelMot.setBackground(new Color(240, 240, 255));

        panelMilieu.add(dessinPendu, BorderLayout.WEST);// Dessin positionné à gauche
        panelMilieu.add(labelMot, BorderLayout.CENTER);// Affichage du mot au milieu

        // --- BAS : clavier ---
        JPanel panelBas = new JPanel(new BorderLayout(5, 5));
        panelBas.setOpaque(false);
        // Le clavier est configuré en grille de 2 lignes et 13 colonnes (2x13 = 26 lettres)
        JPanel panelClavier = new JPanel(new GridLayout(2, 13, 4, 4));
        panelClavier.setOpaque(false);

        char[] lettres = {
            'A','B','C','D','E','F','G','H','I','J','K','L','M',
            'N','O','P','Q','R','S','T','U','V','W','X','Y','Z'
        };
        // Boucle pour instancier et configurer individuellement chaque lettre
        for (int i = 0; i < 26; i++) {
            char lettre = lettres[i];
            boutonsClavier[i] = new JButton(String.valueOf(lettre));
            boutonsClavier[i].setFont(new Font("Arial", Font.BOLD, 14));
            boutonsClavier[i].setBackground(new Color(255, 249, 196));// Couleur jaune pastel
            boutonsClavier[i].setFocusPainted(false);
            // W ne sera plus tronqué en "..."
            boutonsClavier[i].setPreferredSize(new Dimension(48, 40));// Évite les bugs de dimension de la lettre W
            boutonsClavier[i].setMargin(new Insets(2, 2, 2, 2));
            final int index = i;
            // Quand le joueur clique sur une touche du clavier virtuel
            boutonsClavier[i].addActionListener(e -> jouerLettre(lettre, index));
            panelClavier.add(boutonsClavier[i]);
        }
        
        // Bouton d'aide Joker
        JButton btnReveler = new JButton("Reveler une lettre (-5 diamants)");
        btnReveler.setBackground(new Color(142, 68, 173));
        btnReveler.setForeground(Color.WHITE);
        btnReveler.setFont(new Font("Arial", Font.BOLD, 13));
        btnReveler.setFocusPainted(false);
        btnReveler.addActionListener(e -> revelerLettre());

        // Assemblage final des sous-parties du clavier
        panelBas.add(panelClavier, BorderLayout.CENTER);
        panelBas.add(btnReveler, BorderLayout.SOUTH);
 
        // Injection globale dans la fenêtre principale
        main.add(panelHaut, BorderLayout.NORTH);
        main.add(panelMilieu, BorderLayout.CENTER);
        main.add(panelBas, BorderLayout.SOUTH);
        add(main);
    }

    /*
     * Traite l'action de soumettre une lettre (clic sur le clavier virtuel).
     */
    private void jouerLettre(char lettre, int index) {
        // Envoie la lettre au contrôleur pour vérifier sa présence
        String resultat = controller.jouerLettre(lettre);
        
        // Désactive la touche cliquée pour éviter de la rejouer
        boutonsClavier[index].setEnabled(false);

        // Colore la touche selon la validité de la proposition
        if (resultat.equals("correcte") || resultat.equals("gagne")) {
            boutonsClavier[index].setBackground(new Color(39, 174, 96));
            GestionnaireSons.sonCorrect();
        } else {
            boutonsClavier[index].setBackground(new Color(231, 76, 60));
            GestionnaireSons.sonFaux();
        }

        // Rafraîchissement en temps réel des indicateurs visuels
        labelMot.setText(controller.getAffichageMot());
        labelErreurs.setText("Erreurs : " + controller.getPartie().getErreurs() + "/7");
        dessinPendu.setErreurs(controller.getPartie().getErreurs());
        labelDiamants.setText("Diamants : " + controller.getJoueurActuel().getDiamants());

        // --- CAS DE VICTOIRE DE LA MANCHE EN COURS ---
        if (resultat.equals("gagne")) {
            labelMessage.setForeground(new Color(39, 174, 96));
            labelMessage.setText("BRAVO " + controller.getNomJoueurActuel() + " ! +10 diamants");
            GestionnaireSons.sonVictoire();

            if (mode2Joueurs && !joueur1AJoue) {
                // Manche joueur 1 terminée : victoire
                joueur1AGagne = true;
                joueur1AJoue  = true;
                JOptionPane.showMessageDialog(this,
                    "Bravo " + controller.getNomJoueurActuel() + " ! Mot trouve !\n\n" +
                    "Au tour de " + controller.getJoueur2().getNom() + " maintenant.",
                    "Manche 1 terminee", JOptionPane.INFORMATION_MESSAGE);
                lancerMancheJoueur2();
            } else if (mode2Joueurs) {
                // Manche joueur 2 terminée : victoire → comparer
                JOptionPane.showMessageDialog(this,
                    "Bravo " + controller.getNomJoueurActuel() + " ! Mot trouve !",
                    "Manche 2 terminee", JOptionPane.INFORMATION_MESSAGE);
                afficherResultatFinal(true);
            } else {
                // Mode Solo standard
                JOptionPane.showMessageDialog(this,
                    "Bravo " + controller.getNomJoueurActuel() + " ! +10 diamants !",
                    "Victoire", JOptionPane.INFORMATION_MESSAGE);
                proposerNouvellePartie();
            }

        // --- CAS DE DÉFAITE DE LA MANCHE EN COURS ---
        } else if (resultat.equals("perdu")) {
            labelMessage.setForeground(Color.RED);
            labelMessage.setText("Perdu ! Le mot etait : " + controller.getPartie().getMot().getMot());
            GestionnaireSons.sonDefaite();

            if (mode2Joueurs && !joueur1AJoue) {
                // Manche joueur 1 terminée : défaite
                joueur1AGagne = false;
                joueur1AJoue  = true;
                JOptionPane.showMessageDialog(this,
                    "Perdu " + controller.getNomJoueurActuel() + " !\n" +
                    "Le mot etait : " + controller.getPartie().getMot().getMot() + "\n\n" +
                    "Au tour de " + controller.getJoueur2().getNom() + " maintenant.",
                    "Manche 1 terminee", JOptionPane.ERROR_MESSAGE);
                lancerMancheJoueur2();
            } else if (mode2Joueurs) {
                // Manche joueur 2 terminée : défaite → comparer
                JOptionPane.showMessageDialog(this,
                    "Perdu " + controller.getNomJoueurActuel() + " !\n" +
                    "Le mot etait : " + controller.getPartie().getMot().getMot(),
                    "Manche 2 terminee", JOptionPane.ERROR_MESSAGE);
                afficherResultatFinal(false);
            } else {
                // Mode Solo standard
                JOptionPane.showMessageDialog(this,
                    "Perdu !\nLe mot etait : " + controller.getPartie().getMot().getMot(),
                    "Defaite", JOptionPane.ERROR_MESSAGE);
                proposerNouvellePartie();
            }

        } else {
            // Partie en cours — afficher le joueur actuel (inchangé en mode 2 joueurs)
            labelMessage.setText("Tour de : " + controller.getNomJoueurActuel());
            labelMessage.setForeground(new Color(108, 52, 131));
        }
    }

    // Lance la session complète du joueur 2 avec un nouveau mot
    private void lancerMancheJoueur2() {
        controller.forcerJoueur2();// Bascule l'index du joueur actif vers le Joueur 2 dans le modèle
        controller.nouvellePartie();// Pioche un tout nouveau mot secret indépendant.
        
        // Remise à zéro visuelle de l'ensemble des éléments graphiques de l'écran
        labelMot.setText(controller.getAffichageMot());
        labelErreurs.setText("Erreurs : 0/7");
        dessinPendu.setErreurs(0);
        labelMessage.setText("Tour de : " + controller.getNomJoueurActuel());
        labelMessage.setForeground(new Color(108, 52, 131));
        labelDiamants.setText("Diamants : " + controller.getJoueurActuel().getDiamants());
        setTitle("Pendu — " + controller.getNomJoueurActuel());

        // Réactivation et nettoyage complet des 26 touches du clavier
        for (JButton btn : boutonsClavier) {
            btn.setEnabled(true);
            btn.setBackground(new Color(255, 249, 196));
        }
    }

    // Compare les résultats et affiche le gagnant de la manche complète
    private void afficherResultatFinal(boolean joueur2AGagne) {
        String nomJ1 = controller.getJoueur().getNom();
        String nomJ2 = controller.getJoueur2().getNom();

        String message;
        String titre;

        // Comparaison des états logiques binaires
        if (joueur1AGagne && joueur2AGagne) {
            titre   = "Egalite !";
            message = "Les deux joueurs ont trouve le mot !\n\nEgalite parfaite !";
        } else if (!joueur1AGagne && !joueur2AGagne) {
            titre   = "Defaite generale !";
            message = "Aucun des deux n'a trouve le mot.\nLes deux ont perdu !";
        } else if (joueur1AGagne) {
            titre   = "Victoire de " + nomJ1 + " !";
            message = "Le gagnant est : " + nomJ1 + " !\n\n" +
                      nomJ1 + " a trouve le mot, " + nomJ2 + " n'a pas reussi.";
        } else {
            titre   = "Victoire de " + nomJ2 + " !";
            message = "Le gagnant est : " + nomJ2 + " !\n\n" +
                      nomJ2 + " a trouve le mot, " + nomJ1 + " n'a pas reussi.";
        }

        // Pop-up finale demandant si le duo veut refaire un match
        int choix = JOptionPane.showConfirmDialog(this,
            message + "\n\nVoulez-vous rejouer ?",
            titre,
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE);

        if (choix == JOptionPane.YES_OPTION) {
            // Nouvelle manche complète : joueur 1 recommence
            joueur1AGagne = false;
            joueur1AJoue  = false;
            controller.reinitialiserTour(); // Revient au Joueur 1
            controller.nouvellePartie();

            // Nettoyage de l'interface graphique
            labelMot.setText(controller.getAffichageMot());
            labelErreurs.setText("Erreurs : 0/7");
            dessinPendu.setErreurs(0);
            labelMessage.setText("Tour de : " + controller.getNomJoueurActuel());
            labelMessage.setForeground(new Color(108, 52, 131));
            labelDiamants.setText("Diamants : " + controller.getJoueurActuel().getDiamants());
            setTitle("Pendu — " + controller.getNomJoueurActuel());

            for (JButton btn : boutonsClavier) {
                btn.setEnabled(true);
                btn.setBackground(new Color(255, 249, 196));
            }
        } else {
            // Quitte le jeu en cours et réaffiche l'écran d'accueil général
            dispose();
            new EcranAccueil().setVisible(true);
        }
    }

    /*
     * Logique de triche/aide (Joker) : Déduit des diamants pour afficher automatiquement une lettre correcte.
     */
    private void revelerLettre() {
        char lettre = controller.revelerLettre();
        if (lettre == ' ') {
            // Le contrôleur renvoie un espace vide si le joueur possède moins de 5 diamants
            JOptionPane.showMessageDialog(this,
                "Pas assez de diamants ! Il faut 5 diamants",
                "Impossible", JOptionPane.WARNING_MESSAGE);
        } else {
            // Lettre révélée avec succès : Mise à jour des labels graphiques
            labelMot.setText(controller.getAffichageMot());
            labelDiamants.setText("Diamants : " + controller.getJoueurActuel().getDiamants());
            labelMessage.setText("Lettre revelee : " + lettre);
            labelMessage.setForeground(new Color(108, 52, 131));
            // Recherche la touche correspondante sur le clavier virtuel pour la griser en vert
            for (int i = 0; i < 26; i++) {
                if (boutonsClavier[i].getText().equals(String.valueOf(lettre))) {
                    boutonsClavier[i].setEnabled(false);
                    boutonsClavier[i].setBackground(new Color(39, 174, 96));
                }
            }
        }
    }

    /*
     * En mode Solo : Pop-up standard de fin de partie pour relancer une session de jeu.
     */
    private void proposerNouvellePartie() {
        int choix = JOptionPane.showConfirmDialog(this,
            "Voulez-vous rejouer ?", "Nouvelle partie",
            JOptionPane.YES_NO_OPTION);
        if (choix == JOptionPane.YES_OPTION) {
            controller.reinitialiserTour();
            controller.nouvellePartie();
            // Remise à zéro globale de l'interface
            labelMot.setText(controller.getAffichageMot());
            labelErreurs.setText("Erreurs : 0/7");
            labelMessage.setText("Tour de : " + controller.getNomJoueurActuel());
            labelMessage.setForeground(new Color(108, 52, 131));
            labelDiamants.setText("Diamants : " + controller.getJoueurActuel().getDiamants());
            dessinPendu.setErreurs(0);
            setTitle("Pendu — " + controller.getNomJoueurActuel());
            for (JButton btn : boutonsClavier) {
                btn.setEnabled(true);
                btn.setBackground(new Color(255, 249, 196));
            }
        } else {
            // Retour au menu principal
            dispose();
            new EcranAccueil().setVisible(true);
        }
    }
}