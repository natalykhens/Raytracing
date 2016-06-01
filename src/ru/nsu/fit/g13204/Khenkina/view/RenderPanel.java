package ru.nsu.fit.g13204.Khenkina.view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Natalia on 28.05.16.
 */
public class RenderPanel extends JPanel {
    private BufferedImage image;
    private Color backgroundColor = Color.WHITE;

    public RenderPanel(BufferedImage i){
        image = i;
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        if(image != null) {
            g.drawImage(image, 0, 0, backgroundColor, null);
        }
    }

}
