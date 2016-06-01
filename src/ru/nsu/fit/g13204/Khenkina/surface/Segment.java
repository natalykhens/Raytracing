package ru.nsu.fit.g13204.Khenkina.surface;

import ru.nsu.fit.g13204.Khenkina.matrix.Vector;

public class Segment {
    public Vector point1;
    public Vector point2;
    public int group;

    public Segment(Vector a, Vector b, int g){
        point1 = a;
        point2 = b;
        group = g;
    }
}
