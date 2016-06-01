package ru.nsu.fit.g13204.Khenkina.world;

import ru.nsu.fit.g13204.Khenkina.matrix.Vector;

import java.awt.*;

/**
 * Created by Natalia on 29.05.16.
 */
public class LightSource {
    private Vector position;
    private Color color;
    private double[] intensity = new double[3];

    public LightSource(Vector p, Color c){
        position = p;
        color = c;

        intensity[0] = (double)color.getRed() / 255;
        intensity[1] = (double)color.getGreen() / 255;
        intensity[2] = (double)color.getBlue() / 255;
    }

    public Vector getPosition(){
        return position;
    }

    public Color getColor(){
        return color;
    }

    public double[] getIntensity(){
        return intensity;
    }
}
