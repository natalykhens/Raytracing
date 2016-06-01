package ru.nsu.fit.g13204.Khenkina.surface;

import ru.nsu.fit.g13204.Khenkina.matrix.Vector;
import ru.nsu.fit.g13204.Khenkina.tracing.Intersection;
import ru.nsu.fit.g13204.Khenkina.tracing.Ray;

/**
 * Created by Natalia on 30.05.16.
 */
public class Triangle extends Polygon {
    //private Vector[] vertices;
    //private Plane plane;
    //private int coord1 = 0;
    //private int coord2 = 1;
    private double area = 0;
    //private Wireframe wireframe;

    public Triangle(Vector[] vert, OpticalCharacteristics oCh){
        super(vert, oCh);
        //vertices = vert;
        init();
    }

    protected void init(){
        /*initPlane();
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
        }*/
        super.init();
        area = countTriangleArea(vertices[0], vertices[1], vertices[2]);
    }

    /*private void initPlane(){
        //TODO Проверить ориентацию треугольника
        Vector side1;
        Vector side2;
        Vector normal = null;
        try {
            side1 = vertices[1].subtract(vertices[0]);
            side2 = vertices[2].subtract(vertices[0]);
            normal = side2.vectorProduct(side1);
            //normal = new Vector(new double[]{-1, -1, -1});
            normal.normalize();
        } catch (MatrixArithmeticException e) {}


        double a = normal.getCoordinate(0);
        double b = normal.getCoordinate(1);
        double c = normal.getCoordinate(2);

        double d = - (a * vertices[0].getCoordinate(0) + b * vertices[0].getCoordinate(1) + c * vertices[0].getCoordinate(2));

        plane = new Plane(normal, d);
    }*/

    @Override
    public Intersection intersect(Ray ray) {
        /*Vector intersection = plane.intersect(ray);
        if(intersection == null){
            return null;
        }

        double area1 = countTriangleArea(vertices[0], vertices[1], intersection);
        double area2 = countTriangleArea(vertices[0], vertices[2], intersection);
        double area3 = countTriangleArea(vertices[1], vertices[2], intersection);

        double totalArea = area1 + area2 + area3;
        double epsilon = 0.000000001;

        if(totalArea >= (area - epsilon) && totalArea <= (area + epsilon)){
            //return intersection;
            Ray reflectedRay = null;
            try {
                reflectedRay = countReflectedRay(ray.getStart(), intersection, plane.getNormal());
            } catch (MatrixArithmeticException e) {}
            //return reflectedRay;
            return new Intersection(this, ray, reflectedRay, plane.getNormal());
        }

        return null;*/
        return intersectTriangle(ray, vertices, area);
    }

    /*protected boolean isLightSourceVisible(World world, Ray r, double d){
        if(plane.intersect(r) == null){             //источник с другой стороны
            return false;
        }
        if(world.isIntersect(r, this, d)){
            return false;
        }
        return true;
    }*/

    /*@Override
    public boolean isIntersect(Ray ray, double length) {
        //return false;
        Intersection intersection = intersect(ray);
        if(intersection == null){
            return false;
        }
        double d = Vector.distance(ray.getStart(), intersection.outRay.getStart());
        return d <= length - 0.00001;
    }*/

    /*private void initWireframe(){
        ArrayList<Vector> pList = new ArrayList<>(3);
        ArrayList<Pair> sList = new ArrayList<>(3);

        for(int i = 0; i < vertices.length; ++i){
            double x = vertices[i].getCoordinate(0);
            double y = vertices[i].getCoordinate(1);
            double z = vertices[i].getCoordinate(2);
            pList.add(new Vector(new double[]{x, y, z, 1}));
        }
        sList.add(new Pair(0, 1, 0));
        sList.add(new Pair(0, 2, 0));
        sList.add(new Pair(1, 2, 0));

        wireframe = new Wireframe(pList, sList);
        //try {
        //    wireframe = wireframe.countNewWireframe(Matrix.createTranslateMatrix(maxPoint.add(minPoint).multiply(1.0 / 2)));
        //} catch (MatrixArithmeticException e) {}
    }*/

}
