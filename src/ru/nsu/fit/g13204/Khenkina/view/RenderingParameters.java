package ru.nsu.fit.g13204.Khenkina.view;

import ru.nsu.fit.g13204.Khenkina.tracing.RenderQuality;

import java.awt.*;

/**
 * Created by Natalia on 01.06.16.
 */
public class RenderingParameters {
    public Color background;
    public double gamma;
    public int depth;
    public RenderQuality quality;

    public RenderingParameters(Color b, double g, int d, RenderQuality q){
        background = b;
        gamma = g;
        depth = d;
        quality = q;
    }
}
