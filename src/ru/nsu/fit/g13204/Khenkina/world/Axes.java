package ru.nsu.fit.g13204.Khenkina.world;

import ru.nsu.fit.g13204.Khenkina.surface.Segment;
import ru.nsu.fit.g13204.Khenkina.matrix.Matrix;
import ru.nsu.fit.g13204.Khenkina.matrix.Vector;

public class Axes {
    private Vector center;
    private Vector x_axis;
    private Vector y_axis;
    private Vector z_axis;

    public Axes(){
        center = new Vector(new double[]{0, 0, 0, 1});
        x_axis = new Vector(new double[]{1, 0, 0, 1});
        y_axis = new Vector(new double[]{0, 1, 0, 1});
        z_axis = new Vector(new double[]{0, 0, 1, 1});
    }

    public Axes(Vector c, Vector x, Vector y, Vector z){
        center = c;
        x_axis = x;
        y_axis = y;
        z_axis = z;
    }

    public Axes recount(Matrix matrix){
        Vector c = null;
        Vector x = null;
        Vector y = null;
        Vector z = null;

            c = matrix.multiply(center).getColumn(0);
            x = matrix.multiply(x_axis).getColumn(0);
            y = matrix.multiply(y_axis).getColumn(0);
            z = matrix.multiply(z_axis).getColumn(0);
        return new Axes(c, x, y, z);
    }

    public Segment getXAxis(){
        return new Segment(center, x_axis, 0);
    }

    public Segment getYAxis(){
        return new Segment(center, y_axis, 0);
    }

    public Segment getZAxis(){
        return new Segment(center, z_axis, 0);
    }
}
