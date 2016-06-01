package ru.nsu.fit.g13204.Khenkina.surface;

import ru.nsu.fit.g13204.Khenkina.matrix.Vector;
import ru.nsu.fit.g13204.Khenkina.tracing.Ray;
import ru.nsu.fit.g13204.Khenkina.tracing.Intersection;

/**
 * Created by dima on 31.05.16.
 */
public class Quadrangle extends Polygon {
    private Vector[] triangle1 = new Vector[3];
    private Vector[] triangle2 = new Vector[3];

    private double area1;
    private double area2;

    public Quadrangle(Vector[] vert, OpticalCharacteristics oCh){
        super(vert, oCh);
        init();
    }

    protected void init(){
        super.init();

        for(int i = 0; i < triangle1.length; ++i){
            triangle1[i] = vertices[i];
        }
        triangle2[0] = vertices[0];
        triangle2[1] = vertices[2];
        triangle2[2] = vertices[3];

        area1 = countTriangleArea(triangle1[0], triangle1[1], triangle1[2]);
        area2 = countTriangleArea(triangle2[0], triangle2[1], triangle2[2]);
    }

    @Override
    public Intersection intersect(Ray ray) {
        Intersection intersection = intersectTriangle(ray, triangle1, area1);
        if(intersection == null){
            intersection = intersectTriangle(ray, triangle2, area2);
        }
        return intersection;
    }
}
