package ru.nsu.fit.g13204.Khenkina.surface;

import ru.nsu.fit.g13204.Khenkina.matrix.Vector;
import ru.nsu.fit.g13204.Khenkina.tracing.Ray;
import ru.nsu.fit.g13204.Khenkina.world.World;
import ru.nsu.fit.g13204.Khenkina.tracing.Intersection;
import ru.nsu.fit.g13204.Khenkina.world.LightSource;

import java.awt.*;
import java.util.List;

public abstract class Surface {
    protected Color color;
    protected OpticalCharacteristics opticalCharacteristics;
    //protected double[] color = new double[3];


    protected Surface(OpticalCharacteristics oCh){

        opticalCharacteristics = oCh;
        double coefficient = 255.0;

        int red = (int)Math.round(oCh.kd[0] * coefficient);
        int green = (int)Math.round(oCh.kd[1] * coefficient);
        int blue = (int)Math.round(oCh.kd[2] * coefficient);

        color = new Color(red, green, blue);
    }

    protected Ray countReflectedRay(Vector r0, Vector intersectionPoint, Vector normal) {
        Vector l = r0.subtract(intersectionPoint);
        l.normalize();

        double coeff = 2 * normal.scalarProduct(l);
        Vector reflectedVector = normal.multiply(coeff).subtract(l);

        return new Ray(intersectionPoint, intersectionPoint.add(reflectedVector));
    }


    public double[] countIntensity(World world, Vector normal, Vector eye, Ray inRay, Ray outRay, double[] intensity, double distance) {

            List<LightSource> lightSources = world.getLightSources();

            Vector intersectionPoint = outRay.getStart();



            Vector l;
            double[] totalIntensity = new double[3];

            double[] diffusedIntensity = world.getDiffusedIntensity();
            //TODO переделать!!!!!
            double coeff = 1;//4 * Math.PI * Math.pow(distance, 2);

            //TODO Случай, когда distance = 0 ?

            for(int i = 0; i < totalIntensity.length; ++i){
                totalIntensity[i] = diffusedIntensity[i] * opticalCharacteristics.kd[i];// * color[i];
                totalIntensity[i] += intensity[i] / coeff * opticalCharacteristics.ks[i];
            }

            //Считаем источники

            for (LightSource source : lightSources) {
                Vector sourcePosition = source.getPosition();

                //Ray r = new Ray(intersectionPoint, sourcePosition);
                Ray r = new Ray(sourcePosition, intersectionPoint);
                double d = Vector.distance(intersectionPoint, sourcePosition);
                //if (world.isIntersect(r, this, d)) {
                if(!isLightSourceVisible(world, r, d)){
                    continue;
                }

                l = sourcePosition.subtract(intersectionPoint);
                l.normalize();

                Vector h = l.add(eye);
                h.normalize();

                countSourceIntensity(source, intersectionPoint, normal, l, h, totalIntensity);
            }

            return totalIntensity;

        }


    protected boolean isLightSourceVisible(World world, Ray r, double d){
        if(world.isIntersect(r, null, d)){
            return false;
        }
        return true;
    }

    protected void countSourceIntensity(LightSource source, Vector point, Vector n, Vector l, Vector h, double[] intensity){
        double distance = Vector.distance(source.getPosition(), point);
        double fatt = 1.0 / (1.0 + distance);

        double[] sourceIntensity = source.getIntensity();

        double diffuseReflection;
        double mirrorReflection;

        double nl;
        double nh;

            nl = n.scalarProduct(l);
            nh = Math.pow(n.scalarProduct(h), opticalCharacteristics.power);


        for(int i = 0; i < 3; ++i){
            diffuseReflection = opticalCharacteristics.kd[i] * nl;//* color[i]
            mirrorReflection = opticalCharacteristics.ks[i] * nh;

            intensity[i] += sourceIntensity[i] * fatt * (diffuseReflection + mirrorReflection);
        }
    }

    public abstract Wireframe getWireframe();
    public abstract Dimensions countDimensions();
    public OpticalCharacteristics getOpticalCharacteristics(){
        return opticalCharacteristics;
    }
    public abstract Intersection intersect(Ray ray);

    public abstract boolean isIntersect(Ray ray, double length);
}
