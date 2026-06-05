package com.hangman.view;

import javax.swing.*;
import java.awt.*;
/*
 * Classe DessinPendu - Un composant graphique personnalisé (JPanel) 
 * qui se charge de dessiner la potence et le bonhomme au fur et à mesure des erreurs.
 */
public class DessinPendu extends JPanel {
    // Compteur qui stocke le nombre d'erreurs actuelles (de 0 à 7)
    private int erreurs = 0;
    /*
     * Constructeur : Configure l'apparence visuelle par défaut du panneau de dessin.
     */
    public DessinPendu() {
        setBackground(new Color(245, 245, 255));
        setPreferredSize(new Dimension(200, 250));
    }
    /*
     * Met à jour le nombre d'erreurs et force le rafraîchissement visuel du dessin.
     * @param erreurs Le nouveau nombre d'erreurs de la partie.
     */
    public void setErreurs(int erreurs) {
        this.erreurs = erreurs;
        repaint(); // redessine automatiquement
    }
    /*
     * Méthode principale de dessin. Elle est appelée automatiquement par le système
     * lors de l'affichage initial et à chaque fois que repaint() est invoqué.
     */
    @Override
    protected void paintComponent(Graphics g) {
        // 1. Toujours appeler le composant parent pour nettoyer l'écran et dessiner le fond
        super.paintComponent(g);
        // 2. Conversion de l'objet Graphics classique en Graphics2D pour avoir accès à des outils plus précis
        Graphics2D g2 = (Graphics2D) g;
        // 3. Configuration du pinceau : épaisseur des traits fixée à 3 pixels
        g2.setStroke(new BasicStroke(3));
        // 4. Couleur marron pour la potence
        g2.setColor(new Color(80, 40, 10));

        // Potence (toujours visible)
        g2.drawLine(20, 230, 180, 230); // base
        g2.drawLine(60, 230, 60, 20);   // poteau
        g2.drawLine(60, 20, 130, 20);   // bras horizontal
        g2.drawLine(130, 20, 130, 50);  // corde
        
        // 5. Changement de couleur : gris foncé pour le corps du bonhomme
        g2.setColor(new Color(50, 50, 50));

        // --- DESSIN PROGRESSIF DU PENDU SELON LE NOMBRE D'ERREURS ---
        // Erreur 1 → tête
        if (erreurs >= 1) {
            g2.drawOval(110, 50, 40, 40);
        }
        // Erreur 2 → corps
        if (erreurs >= 2) {
            g2.drawLine(130, 90, 130, 150);
        }
        // Erreur 3 → bras gauche
        if (erreurs >= 3) {
            g2.drawLine(130, 100, 100, 130);
        }
        // Erreur 4 → bras droit
        if (erreurs >= 4) {
            g2.drawLine(130, 100, 160, 130);
        }
        // Erreur 5 → jambe gauche
        if (erreurs >= 5) {
            g2.drawLine(130, 150, 100, 190);
        }
        // Erreur 6 → jambe droite
        if (erreurs >= 6) {
            g2.drawLine(130, 150, 160, 190);
        }
        // Erreur 7 → visage triste (mort)
        if (erreurs >= 7) {
            g2.setColor(Color.RED);
            g2.drawLine(118, 62, 124, 68); // oeil gauche X
            g2.drawLine(124, 62, 118, 68);
            g2.drawLine(138, 62, 144, 68); // oeil droit X
            g2.drawLine(144, 62, 138, 68);
            // Bouche triste en forme d'arc inversé
            // drawArc(x, y, larg, haut, angleDebut, angleEtendue)
            // L'angle négatif (-180) dessine l'arc vers le bas (courbe vers le bas = tristesse)
            g2.drawArc(118, 75, 25, 12, 0, -180); // bouche triste
        }
    }
}
