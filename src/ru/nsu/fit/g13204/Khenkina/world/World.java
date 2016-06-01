package ru.nsu.fit.g13204.Khenkina.world;

import ru.nsu.fit.g13204.Khenkina.surface.Dimensions;
import ru.nsu.fit.g13204.Khenkina.surface.Wireframe;
import ru.nsu.fit.g13204.Khenkina.matrix.Matrix;
import ru.nsu.fit.g13204.Khenkina.matrix.Vector;
import ru.nsu.fit.g13204.Khenkina.surface.Surface;
import ru.nsu.fit.g13204.Khenkina.tracing.Ray;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class World extends Observable {
    private List<Surface> items;
    private List<LightSource> lightSources;
    private Color diffusedLight;
    private double[] diffusedIntensity = new double[3];
    private Axes axes = new Axes();
    private Matrix boxMatrix;

    public World(List<Surface> surf, List<LightSource> s, Color dLight){
        items = surf;
        lightSources = s;
        diffusedLight = dLight;

        diffusedIntensity[0] = (double)diffusedLight.getRed() / 255;
        diffusedIntensity[1] = (double)diffusedLight.getGreen() / 255;
        diffusedIntensity[2] = (double)diffusedLight.getBlue() / 255;
        //scale();
    }

    public void reinit(List<Surface> surf, List<LightSource> s, Color dLight){
        items = surf;
        lightSources = s;
        diffusedLight = dLight;

        diffusedIntensity[0] = (double)diffusedLight.getRed() / 255;
        diffusedIntensity[1] = (double)diffusedLight.getGreen() / 255;
        diffusedIntensity[2] = (double)diffusedLight.getBlue() / 255;

        setChanged();
        notifyObservers();
    }

    public void init(List<Surface> worldItems, List<LightSource> s, Matrix m){
        items = worldItems;
        lightSources = s;
        //scale();
        setChanged();
        notifyObservers();
    }
    public List<Surface> getSurfaces(){
        return items;
    }

    public List<LightSource> getLightSources(){
        return lightSources;
    }

    public Color getDiffusedLight(){
        return diffusedLight;
    }

    public double[] getDiffusedIntensity(){
        return diffusedIntensity;
    }

    public boolean isIntersect(Ray ray, Surface surface, double length){
        for(Surface s : items){
            if(s == surface){
                continue;
            }
            if(s.isIntersect(ray, length)){
                return true;
            }
        }
        return false;
    }

    public List<Wireframe> getWireframes(Matrix transform){
        List<Wireframe> wireframes = new ArrayList<>(items.size());
        for(Surface surface : items){
            Wireframe w = surface.getWireframe();

                Matrix resultMatrix = transform;
                w = w.countNewWireframe(resultMatrix);
            wireframes.add(w);
        }
        return wireframes;
    }

    public Axes getAxes(Matrix transform){
        Matrix resultMatrix = transform;
        Axes a = axes.recount(resultMatrix);
        return a;
    }


    public void scale(){
        Dimensions dimensions = null;

            dimensions = countDimensions();

        double xMax = dimensions.max.getCoordinate(0);
        double yMax = dimensions.max.getCoordinate(1);
        double zMax = dimensions.max.getCoordinate(2);
        double xMin = dimensions.min.getCoordinate(0);
        double yMin = dimensions.min.getCoordinate(1);
        double zMin = dimensions.min.getCoordinate(2);

        double xOffset = (xMax + xMin) / 2;
        double yOffset = (yMax + yMin) / 2;
        double zOffset = (zMax + zMin) / 2;

        double[] coefficients = new double[3];

        coefficients[0] = (xMax - xMin) / 2;
        coefficients[1] = (yMax - yMin) / 2;
        coefficients[2] = (zMax - zMin) / 2;

        double coefficient = coefficients[0];
        for (int i = 1; i < coefficients.length; ++i) {
            if (coefficients[i] > coefficient) {
                coefficient = coefficients[i];
            }
        }

        initBoxMatrix(new Vector(new double[]{-xOffset, -yOffset, -zOffset}), coefficient);
    }

    private void initBoxMatrix(Vector newCenter, double coefficient){
        Matrix translateMatrix = Matrix.createTranslateMatrix(newCenter);
        Matrix scaleMatrix = Matrix.createScaleMatrix(1 / coefficient);
           boxMatrix = scaleMatrix.multiply(translateMatrix);
    }

    private Dimensions countDimensions()  {
        double xMax = 0;
        double yMax = 0;
        double zMax = 0;
        double xMin = 0;
        double yMin = 0;
        double zMin = 0;
        double x, y, z;

        for(int i = 0; i < items.size(); ++i){
            Surface s = items.get(i);
            Dimensions d = s.countDimensions();

            x = d.min.getCoordinate(0);
            y = d.min.getCoordinate(1);
            z = d.min.getCoordinate(2);
            if(i == 0){
                xMin = x;
                yMin = y;
                zMin = z;
            }
            else {
                if (x < xMin) {
                    xMin = x;
                }
                if (y < yMin) {
                    yMin = y;
                }
                if (z < zMin) {
                    zMin = z;
                }
            }

            x = d.max.getCoordinate(0);
            y = d.max.getCoordinate(1);
            z = d.max.getCoordinate(2);
            if(i == 0){
                xMax = x;
                yMax = y;
                zMax = z;
            }
            else {
                if(x > xMax){
                    xMax = x;
                }
                if(y > yMax){
                    yMax = y;
                }
                if(z > zMax){
                    zMax = z;
                }
            }
        }
        Vector max = new Vector(new double[]{xMax, yMax, zMax});
        Vector min = new Vector(new double[]{xMin, yMin, zMin});
        return new Dimensions(min, max);
    }
}
