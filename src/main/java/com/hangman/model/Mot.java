
package com.hangman.model;
/**
 * Classe Mot — représente le mot à deviner dans le jeu du Pendu.
 * Gère l'affichage du mot avec les lettres trouvées et cachées.
 */
public class Mot {
    private String mot;   // Le mot complet en majuscules
    private char[] lettresTrouvees;  // Tableau des lettres devinées (_ si pas encore trouvée)
    
    /**
     * Constructeur — initialise le mot et cache toutes les lettres avec '_'
     * @parammot — le mot à deviner
     */
    public Mot(String mot) {
        this.mot = mot.toUpperCase();
        this.lettresTrouvees = new char[mot.length()];
        for (int i = 0; i < mot.length(); i++) {
            lettresTrouvees[i] = '_'; // Toutes les lettres sont cachées au départ
        }
    }

    // Vérifie si la lettre est dans le mot
    /* Si oui, dévoile toutes ses occurrences.
     * @param lettre — la lettre proposée par le joueur
     * @return true si la lettre est dans le mot, false sinon
     */
    public boolean verifierLettre(char lettre) {
        boolean trouvee = false;
        for (int i = 0; i < mot.length(); i++) {
            if (mot.charAt(i) == Character.toUpperCase(lettre)) {
                lettresTrouvees[i] = mot.charAt(i);  // Dévoiler la lettre
                trouvee = true;
            }
        }
        return trouvee;
    }
    
    /* @return true si le mot est complet, false sinon
     */
    // Vérifie si le mot est entièrement deviné
    public boolean estComplet() {
        for (char c : lettresTrouvees) {
            if (c == '_') return false; // Il reste des lettres cachées
        }
        return true;
    }
    /* Retourne l'affichage du mot avec les lettres trouvées.
     Retourne "_ H _ _ _" pour afficher
    * @return String — le mot avec les lettres trouvées et '_' pour les autres*/
    public String afficher() {
        StringBuilder sb = new StringBuilder();
        for (char c : lettresTrouvees) {
            sb.append(c).append(" ");
        }
        return sb.toString().trim();
    }

    //Révèle TOUTES les occurrences de la première lettre cachée
    /* Coûte 5 diamants au joueur.
     * @return la lettre révélée, ou ' ' si toutes les lettres sont déjà trouvées
     */
    public char revelerLettre() {
        // 1) Trouver quelle lettre révéler (la première encore cachée)
        char lettreAReveler = ' ';
        for (int i = 0; i < lettresTrouvees.length; i++) {
            if (lettresTrouvees[i] == '_') {
                lettreAReveler = mot.charAt(i);
                break; // on a trouvé la lettre à révéler
            }
        }

        // 2) Révéler TOUTES les occurrences de cette lettre
        if (lettreAReveler != ' ') {
            for (int i = 0; i < mot.length(); i++) {
                if (mot.charAt(i) == lettreAReveler) {
                    lettresTrouvees[i] = lettreAReveler;
                }
            }
        }

        return lettreAReveler; // retourne la lettre révélée
    }
    
    // Getters
    public String getMot() { return mot; }
    public char[] getLettresTrouvees() { return lettresTrouvees; }
}