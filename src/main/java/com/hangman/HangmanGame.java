package com.hangman;

import com.hangman.view.EcranAccueil;
import javax.swing.SwingUtilities;

public class HangmanGame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EcranAccueil accueil = new EcranAccueil();
            accueil.setVisible(true);
        });
    }
}