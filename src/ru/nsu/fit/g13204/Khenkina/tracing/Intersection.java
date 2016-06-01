package ru.nsu.fit.g13204.Khenkina.tracing;

import ru.nsu.fit.g13204.Khenkina.matrix.Vector;
import ru.nsu.fit.g13204.Khenkina.surface.Surface;

/**
 * Created by Natalia on 30.05.16.
 */
public class Intersection{
    public Surface surface;
    public Ray inRay;
    public Ray outRay;
    public Vector normal;

    public Intersection(Surface s, Ray in, Ray out, Vector n){
        surface = s;
        inRay = in;
        outRay = out;
        normal = n;
    }
}