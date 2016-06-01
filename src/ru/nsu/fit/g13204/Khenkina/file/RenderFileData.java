package ru.nsu.fit.g13204.Khenkina.file;

import ru.nsu.fit.g13204.Khenkina.matrix.Vector;
import ru.nsu.fit.g13204.Khenkina.tracing.RenderQuality;

import java.awt.*;

/**
 * Created by Natalia on 31.05.16.
 */
public class RenderFileData {
    public Color background;
    public double gamma;
    public int depth;
    public RenderQuality quality;
    public Vector cameraPosition;
    public Vector viewPoint;
    public Vector up;

    public double zf;
    public double zb;
    public double sw;
    public double sh;


    public RenderFileData(Color bColor, double g, int d, RenderQuality q, Vector camera, Vector view, Vector u,
                          double f, double b, double w, double h){
        background = bColor;
        gamma = g;
        depth = d;
        quality = q;
        cameraPosition = camera;
        viewPoint = view;
        up = u;
        zf = f;
        zb = b;
        sw = w;
        sh = h;
    }
}
