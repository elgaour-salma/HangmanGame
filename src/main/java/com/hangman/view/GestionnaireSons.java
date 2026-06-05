package com.hangman.view;

import javax.sound.sampled.*;
/*
 * Classe GestionnaireSons - Centralise et gère la lecture des effets sonores du jeu.
 * Toutes les méthodes sont statiques (static), ce qui permet de les appeler directement
 * depuis n'importe où (ex: GestionnaireSons.sonCorrect()) sans instancier la classe.
 */
public class GestionnaireSons {

    /*
     * Méthode générique interne qui charge et joue un fichier audio de manière asynchrone.
     * @param fichier Le nom du fichier audio à lire (ex: "faux.wav")
     */
    public static void jouerSon(String fichier) {
        try {
            // 1. Récupération du fichier audio sous forme de flux (Stream) depuis le dossier 'resources'
            // getResourceAsStream cherche le dossier "/sounds/" configuré dans les dossiers sources du projet (Build Path)
            AudioInputStream audio = AudioSystem.getAudioInputStream(
                GestionnaireSons.class.getResourceAsStream("/sounds/" + fichier));
            // 2. Création d'un objet 'Clip'
            // Un Clip en Java est un conteneur audio pré-chargé en mémoire vive (RAM), 
            // idéal pour les sons courts (bruitages, clics) car la lecture est instantanée.
            Clip clip = AudioSystem.getClip();
            // 3. Ouverture du flux audio dans le conteneur Clip
            clip.open(audio);
            // 4. Démarre la lecture du son
            clip.start();
        } catch (Exception e) {
            // En cas de problème (fichier manquant, mauvais format .wav, etc.), 
            // on évite de faire planter le jeu : on affiche juste un avertissement dans la console.
            System.out.println("Son non trouvé : " + fichier);
        }
    }

    // --- ACCESSEURS PUBLICS (Raccourcis pour le reste de l'application) ---
    /** Joue le son de validation quand le joueur trouve une bonne lettre. */
    public static void sonCorrect()  { jouerSon("correcteVR2.wav");  }
    /** Joue le son d'erreur quand le joueur clique sur une mauvaise lettre. */
    public static void sonFaux()     { jouerSon("faux.wav");     }
    /** Joue la musique/le son de célébration lors d'un mot complètement découvert. */
    public static void sonVictoire() { jouerSon("victoire.wav"); }
    /** Joue le son de Game Over lorsque le pendu est complet (7 erreurs). */
    public static void sonDefaite()  { jouerSon("defaite.wav");  }
}
