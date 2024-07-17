/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sudoku;

/**
 *
 * @author SU24_CSD201_IA1802_GROUP4
 */
import javax.swing.*;
import java.awt.*;

public class ImagePanel extends JPanel {
    private ImageIcon imageIcon;

    public ImagePanel(String imagePath) {
        // Load image using ImageIcon
        imageIcon = new ImageIcon(getClass().getResource(imagePath));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw image
        if (imageIcon != null) {
            g.drawImage(imageIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
        }
    }
}