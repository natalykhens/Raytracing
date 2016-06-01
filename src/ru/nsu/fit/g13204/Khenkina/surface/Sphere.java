package ru.nsu.fit.g13204.Khenkina.surface;

import ru.nsu.fit.g13204.Khenkina.matrix.Matrix;
import ru.nsu.fit.g13204.Khenkina.matrix.Vector;
import ru.nsu.fit.g13204.Khenkina.tracing.Intersection;
import ru.nsu.fit.g13204.Khenkina.tracing.Ray;

import java.util.ArrayList;

public class Sphere extends Surface{
    private Vector center;
    private double radius;
    //private double[] color = new double[3];
    private Grid grid = new Grid(10, 10, 7);
    private Wireframe wireframe;
    //private OpticalCharacteristics opticalCharacteristics;

    public Sphere(Vector c, double r, OpticalCharacteristics oCh){
        super(oCh);
        center = c;
        radius = r;
        grid = new Grid();
        //color = col;
        initWireframe();
    }

    public Vector getCenter(){
        return center;
    }

    public double getRadius(){
        return radius;
    }

    @Override
    public Wireframe getWireframe(){
        return wireframe;
    }

    @Override
    public Dimensions countDimensions(){
        Dimensions dimensions = null;

            dimensions = wireframe.countDimansions();

        return dimensions;
    }

    @Override
    public Intersection intersect(Ray ray){
        //return new Ray(new Vector(new double[]{0, 0, 0}), new Vector(new double[]{0, 0, 0}));

            Vector oc = center.subtract(ray.getStart());
            double ocModule2 = oc.module2();
            double r2 = Math.pow(radius, 2);
            boolean inside = ocModule2 < r2;

            double tca = oc.scalarProduct(ray.getDirection());

            if (tca < 0 && !inside) {             //нет пересечений
                return null;
            }

            double tca2 = Math.pow(tca, 2);
            double d2 = oc.module2() - tca2;
            double thc2 = r2 - d2;

            if (thc2 < 0) {
                return null;
            }

            double t;
            double thc = Math.sqrt(thc2);
            if (!inside) {
                t = tca - thc;
            } else {
                t = tca + thc;
            }

            Vector intersectionPoint = ray.getStart().add(ray.getDirection().multiply(t));

            Vector normal = intersectionPoint.subtract(center);
            normal.normalize();

            Ray reflectedRay = countReflectedRay(ray.getStart(), intersectionPoint, normal);



            return new Intersection(this, ray, reflectedRay, normal);
    }

    @Override
    public boolean isIntersect(Ray ray, double length){

            Vector oc = center.subtract(ray.getStart());
            double ocModule2 = oc.module2();
            double r2 = Math.pow(radius, 2);
            boolean inside = ocModule2 < r2;

            double tca = oc.scalarProduct(ray.getDirection());

            if (tca < 0 && !inside) {             //нет пересечений
                return false;
            }

            double tca2 = Math.pow(tca, 2);
            double d2 = oc.module2() - tca2;
            double thc2 = r2 - d2;

            if (thc2 < 0) {
                return false;
            }

            double t;
            double thc = Math.sqrt(thc2);
            if (!inside) {
                t = tca - thc;
            } else {
                t = tca + thc;
            }

            Vector intersectionPoint = ray.getStart().add(ray.getDirection().multiply(t));
            double distance = Vector.distance(ray.getStart(), intersectionPoint);

            //TODO Сделать более точную проверки длины
            return distance <= length - 0.00001;

    }

    private void initWireframe(){
        int numPoints = (grid.n + 1) * (grid.m + 1);

        ArrayList<Vector> points = new ArrayList<>(numPoints);
        ArrayList<Pair> segments = new ArrayList<>(numPoints * grid.k);

        Coordinate[] arguments = splitCircle(grid.n * grid.k);
        double vStep = 2 * Math.PI / grid.m;
        Vector point, prevPoint = null;
        int pointIndex = 0;

        for(double v = 0; v <= 2 * Math.PI; v += vStep){
            for(int i = 0; i < arguments.length; ++i){
                //System.out.println(i + " " + arguments.length);
                point = r(arguments[i], v);
                points.add(pointIndex++, point);
                if(prevPoint != null){
                    segments.add(new Pair(pointIndex - 2, pointIndex - 1, 0));
                }
                prevPoint = point;
            }
            prevPoint = null;
        }


        vStep = 2 * Math.PI / (grid.m * grid.k);
        prevPoint = null;

        int counter = 0;
        int index, prevIndex = 0;

        int index1, index2;

        for(int i = 0; i < arguments.length; i += grid.k){
            index = i;

            for(double v = 0; v <= 2 * Math.PI; v += vStep){
                point = r(arguments[i], v);
                if(counter % grid.k == 0){
                    index1 = pointIndex - 1;
                    index2 = index;
                    prevIndex = index;
                    index += grid.n * grid.k + 1;
                }
                else if(counter % grid.k == 1 && counter != 1){
                    index1 = prevIndex;
                    index2 = pointIndex - 1;
                }
                else{
                    points.add(pointIndex++, point);
                    index1 = pointIndex - 2;
                    index2 = pointIndex - 1;
                    if(counter == 1){
                        index1 = prevIndex;
                    }
                }
                if(prevPoint != null){
                    segments.add(new Pair(index1, index2, 0));
                }
                prevPoint = point;
                ++counter;
            }
            prevPoint = null;
            counter = 0;
        }

        wireframe = new Wireframe(points, segments, color);


            wireframe = wireframe.countNewWireframe(Matrix.createTranslateMatrix(center));
        }

    private Vector r(Coordinate argument, double v){
        double[] coord = new double[4];
        Coordinate splinePoint = count(argument.x);

        coord[0] = splinePoint.y * Math.cos(v);
        coord[1] = splinePoint.y * Math.sin(v);
        coord[2] = splinePoint.x;
        coord[3] = 1;

        return new Vector(coord);
    }

    private Coordinate count(double x){
        double y = Math.sqrt(Math.pow(radius, 2) - Math.pow(x, 2));
        return new Coordinate(x, y);
    }

    private Coordinate[] splitCircle(int numParts){
        double l = 0, prevL;

        Coordinate[] parts = new Coordinate[numParts + 1];
        double circleLength = Math.PI * radius;
        double dx = 2 * radius / 100;

        Coordinate coordinate, prevCoordinate = null;
        double currentLength = 0;
        double step = circleLength / numParts;
        int index = 0;

        for(double x = -radius; x <= radius; x += dx){
            coordinate = count(x);
            prevL = l;
            if(prevCoordinate != null){
                l += distance(prevCoordinate, coordinate);
            }
            while(currentLength >= prevL && currentLength < l){
                if(currentLength - prevL < l - currentLength){
                    parts[index++] = prevCoordinate;
                }
                else{
                    parts[index++] = coordinate;
                }

                if(index >= parts.length){
                    break;
                }
                currentLength += step;
            }
            prevCoordinate = coordinate;
        }

        while(index < parts.length){
            parts[index++] = new Coordinate(radius, 0);
        }
        return parts;
    }

    private double distance(Coordinate coord1, Coordinate coord2){
        return Math.sqrt(Math.pow(coord1.x - coord2.x, 2) + Math.pow(coord1.y - coord2.y, 2));
    }

}
