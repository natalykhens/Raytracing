package ru.nsu.fit.g13204.Khenkina.surface;

import ru.nsu.fit.g13204.Khenkina.matrix.Vector;
import ru.nsu.fit.g13204.Khenkina.tracing.Intersection;
import ru.nsu.fit.g13204.Khenkina.tracing.Ray;
import ru.nsu.fit.g13204.Khenkina.world.World;

import java.util.ArrayList;

/**
 * Created by Natalia on 31.05.16.
 */
public abstract class Polygon extends Surface {
    protected Vector[] vertices;
    protected Plane plane;
    protected int coord1 = 0;
    protected int coord2 = 1;
    protected Wireframe wireframe;

    public Polygon(Vector[] vert, OpticalCharacteristics oCh){
        super(oCh);
        vertices = vert;
    }

    public Vector[] getVertices(){
        return vertices;
    }

    protected void init(){
        initPlane();
        initWireframe();

        boolean flag;

        for(int i = 0; i < 3; ++i){
            flag = true;
            for(int j = 0; j < vertices.length - 1; ++j){
                //TODO точность!!!
                if(vertices[j].getCoordinate(i) != vertices[j + 1].getCoordinate(i)){
                    flag = false;
                }
            }
            if(flag){           //все i-е координаты совпадают
                if(coord1 == i){
                    coord1 = 2;
                }
                else if(coord2 == i){
                    coord2 = 2;
                }
                break;
            }
        }
        //area = countTriangleArea(vertices[0], vertices[1], vertices[2]);
    }

    private void initPlane(){
        //TODO Проверить ориентацию треугольника
        Vector side1;
        Vector side2;
        Vector normal = null;

            side1 = vertices[1].subtract(vertices[0]);
            side2 = vertices[2].subtract(vertices[0]);
            //normal = side2.vectorProduct(side1);
            normal = side1.vectorProduct(side2);
            normal.normalize();


        double a = normal.getCoordinate(0);
        double b = normal.getCoordinate(1);
        double c = normal.getCoordinate(2);

        double d = - (a * vertices[0].getCoordinate(0) + b * vertices[0].getCoordinate(1) + c * vertices[0].getCoordinate(2));

        plane = new Plane(normal, d);
    }


    @Override
    public Wireframe getWireframe() {
        return wireframe;
    }

    @Override
    public Dimensions countDimensions() {
        return null;
    }

    protected boolean isLightSourceVisible(World world, Ray r, double d){
        if(plane.intersect(r) == null){             //источник с другой стороны
            return false;
        }
        if(world.isIntersect(r, this, d)){
            return false;
        }
        return true;
    }

    @Override
    public boolean isIntersect(Ray ray, double length) {
        //return false;
        Intersection intersection = intersect(ray);
        if(intersection == null){
            return false;
        }
        double d = Vector.distance(ray.getStart(), intersection.outRay.getStart());
        return d <= length - 0.00001;
    }


    protected double countTriangleArea(Vector v1, Vector v2, Vector v3){
        double x1 = v1.getCoordinate(coord1);
        double x2 = v2.getCoordinate(coord1);
        double x3 = v3.getCoordinate(coord1);

        double y1 = v1.getCoordinate(coord2);
        double y2 = v2.getCoordinate(coord2);
        double y3 = v3.getCoordinate(coord2);

        return Math.abs(0.5 * ((x2 - x1) * (y3 - y1) - (x3 - x1) * (y2 - y1)));
    }

    protected void initWireframe(){
        ArrayList<Vector> pList = new ArrayList<>(3);
        ArrayList<Pair> sList = new ArrayList<>(3);

        for(int i = 0; i < vertices.length; ++i){
            double x = vertices[i].getCoordinate(0);
            double y = vertices[i].getCoordinate(1);
            double z = vertices[i].getCoordinate(2);
            pList.add(new Vector(new double[]{x, y, z, 1}));
        }

        for(int i = 0; i < vertices.length - 1; ++i){
            sList.add(new Pair(i, i + 1, 0));
        }
        sList.add(new Pair(0, vertices.length - 1, 0));

        wireframe = new Wireframe(pList, sList, color);
    }


    protected Intersection intersectTriangle(Ray ray, Vector[] triangleVertices, double triangleArea) {
        Vector intersection = plane.intersect(ray);
        if(intersection == null){
            return null;
        }

        double area1 = countTriangleArea(triangleVertices[0], triangleVertices[1], intersection);
        double area2 = countTriangleArea(triangleVertices[0], triangleVertices[2], intersection);
        double area3 = countTriangleArea(triangleVertices[1], triangleVertices[2], intersection);

        double totalArea = area1 + area2 + area3;
        double epsilon = 0.000000001;

        if(totalArea >= (triangleArea - epsilon) && totalArea <= (triangleArea + epsilon)){
            //return intersection;
            Ray reflectedRay = null;

                reflectedRay = countReflectedRay(ray.getStart(), intersection, plane.getNormal());
            return new Intersection(this, ray, reflectedRay, plane.getNormal());
        }

        return null;
    }


}
