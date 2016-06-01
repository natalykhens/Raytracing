package ru.nsu.fit.g13204.Khenkina.tracing;

import ru.nsu.fit.g13204.Khenkina.matrix.Vector;


public class Ray {
    private Vector start;
    private Vector direction;

    public Ray(Vector st, Vector p){
        start = st;
            direction = p.subtract(start);
            direction.normalize();

    }

    public Vector getStart(){
        return start;
    }

    public Vector getDirection(){
        return direction;
    }
}
