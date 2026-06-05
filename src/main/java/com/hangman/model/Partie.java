
package com.hangman.model;
/**
 * Classe Partie — gère le déroulement d'une partie du jeu du Pendu.
 * Suit les erreurs, les tours et détermine victoire ou défaite.
 */

public class Partie {

    private int erreurs;  // Nombre d'erreurs commises
    private int maxErreurs = 7;// Nombre maximum d'erreurs autorisées
    private boolean enCours; // true si la partie est encore en cours
    private Mot mot;// Le mot à deviner
    private int tour; // numéro du tour (pour mode 2 joueurs)
    
    /*
     * Constructeur — initialise une nouvelle partie avec le mot donné.
     * @param mot — le mot à deviner
     */
    public Partie(Mot mot) {
        this.mot = mot;
        this.erreurs = 0;
        this.enCours = true;
        this.tour = 0; 
    }

    // Jouer une lettre
    /*
     * vérifie si elle est dans le mot.
     * Incrémente les erreurs si la lettre est fausse.
     * Termine la partie si victoire ou défaite.
     * @param lettre — la lettre proposée
     * @return true si la lettre est correcte, false sinon
     */
    public boolean jouerLettre(char lettre) {
        boolean correcte = mot.verifierLettre(lettre);
        if (!correcte) {
            erreurs++;  // Incrémenter les erreurs si lettre fausse
        }
        tour++; //incrémenter le tour à chaque lettre jouée
        // Vérifier si la partie est terminée
        if (erreurs >= maxErreurs || mot.estComplet()) {
            enCours = false;
        }
        return correcte;
    }
    
    /* estGagne
     * @return true si le mot est entièrement deviné (victoire)
     */

    /*estPerdu
     * @return true si le nombre max d'erreurs est atteint (défaite)
     */

    public boolean estGagne()      { return mot.estComplet(); }
    public boolean estPerdu()      { return erreurs >= maxErreurs; }
    // Getters
    public int getErreurs()        { return erreurs; }
    public int getMaxErreurs()     { return maxErreurs; }
    public boolean isEnCours()     { return enCours; }
    public Mot getMot()            { return mot; }
    public int getTour()           { return tour; } 
}