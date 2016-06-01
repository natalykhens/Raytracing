package ru.nsu.fit.g13204.Khenkina.surface;

import ru.nsu.fit.g13204.Khenkina.matrix.Vector;
import ru.nsu.fit.g13204.Khenkina.tracing.Ray;

/**
 * Created by Natalia on 30.05.16.
 */
public class Plane {
    private Vector normal;
    private double distance;

    public Plane(Vector n, double d){
        normal = n;
        distance = d;
    }

    public Vector getNormal(){
        return normal;
    }


    public Vector intersect(Ray ray) {

            Vector intersectionPoint;
            Vector r0 = ray.getStart();
            Vector rd = ray.getDirection();

            double nrd = normal.scalarProduct(rd);

            if (nrd == 0) {
                intersectionPoint = null;
            } else if (nrd > 0) {               //нет пересечения, т.к. односторонняя поверхность
                intersectionPoint = null;
            } else {
                double nr0 = normal.scalarProduct(r0);
                double t = -(nr0 + distance) / nrd;
                if (t < 0) {
                    intersectionPoint = null;
                } else {
                    intersectionPoint = r0.add(rd.multiply(t));
                }
            }
            return intersectionPoint;


    }
}
