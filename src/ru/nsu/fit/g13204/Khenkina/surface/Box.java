package ru.nsu.fit.g13204.Khenkina.surface;

import ru.nsu.fit.g13204.Khenkina.matrix.Vector;
import ru.nsu.fit.g13204.Khenkina.tracing.Intersection;
import ru.nsu.fit.g13204.Khenkina.tracing.Ray;

import java.util.ArrayList;


public class Box extends Surface {
    private Vector minPoint;
    private Vector maxPoint;
    private Plane[] planes;
    private Wireframe wireframe;

    public Box(Vector min, Vector max, OpticalCharacteristics oCh){
        super(oCh);
        minPoint = min;
        maxPoint = max;
        initWireframe();
        initPlanes();
    }

    public Vector getMinPoint(){
        return minPoint;
    }

    public Vector getMaxPoint(){
        return maxPoint;
    }

    @Override
    public Wireframe getWireframe() {
        return wireframe;
    }

    @Override
    public Dimensions countDimensions() {
        Dimensions dimensions = null;

            dimensions = wireframe.countDimansions();

        return dimensions;
    }

    @Override
    public Intersection intersect(Ray ray){
        if(!isIntersectBox(ray)){
            return null;
        }

        for(int i = 0; i < planes.length; ++i){
            Plane p = planes[i];
            Vector intersection = p.intersect(ray);
            if(intersection != null){
                Ray reflectedRay = null;
                    reflectedRay = countReflectedRay(ray.getStart(), intersection, p.getNormal());
                return new Intersection(this, ray, reflectedRay, p.getNormal());
            }
        }

        return null;
    }

    @Override
    public boolean isIntersect(Ray ray, double length){
        Intersection intersection = intersect(ray);
        if(intersection == null){
            return false;
        }
        double d = Vector.distance(ray.getStart(), intersection.outRay.getStart());
        return d <= length - 0.00001;
    }

    private boolean isIntersectBox(Ray ray){
        double[] rd = ray.getDirection().getVector();
        double[] r0 = ray.getStart().getVector();

        double tNear = 0;
        double tFar = 0;
        double t1, t2;
        double ti, th;

        for(int i = 0; i < 3; ++i){
            ti = minPoint.getCoordinate(i);
            th = maxPoint.getCoordinate(i);

            if(rd[i] == 0){
                if(r0[i] < ti || r0[i] > th){
                    return false;
                }
            }
            else{
                t1 = (ti - r0[i]) / rd[i];
                t2 = (th - r0[i]) / rd[i];

                if(t2 < t1){
                    double tmp = t1;
                    t1 = t2;
                    t2 = tmp;
                }
                if(i == 0){
                    tNear = t1;
                    tFar = t2;
                }
                else{
                    if(t1 > tNear){
                        tNear = t1;
                    }
                    if(t2 < tFar){
                        tFar = t2;
                    }
                }
                if(tNear > tFar || tFar < 0){
                    return false;
                }
            }
        }
        return true;
    }

    private void initWireframe(){
        ArrayList<Vector> pList = new ArrayList<>(8);
        ArrayList<Pair> sList = new ArrayList<>(12);

        Vector[] vectors = new Vector[]{minPoint, maxPoint};

        double x, y, z;

        for(int i = 0; i < 2; ++i){
            x = vectors[i].getCoordinate(0);
            for(int j = 0; j < 2; ++j){
                y = vectors[j].getCoordinate(1);
                for(int k = 0; k < 2; ++k){
                    z = vectors[k].getCoordinate(2);
                    pList.add(new Vector(new double[]{x, y, z, 1}));
                }
            }
        }
        sList.add(new Pair(0, 1, 0));
        sList.add(new Pair(0, 2, 0));
        sList.add(new Pair(0, 4, 0));
        sList.add(new Pair(1, 3, 0));
        sList.add(new Pair(1, 5, 0));
        sList.add(new Pair(2, 3, 0));
        sList.add(new Pair(2, 6, 0));
        sList.add(new Pair(3, 7, 0));
        sList.add(new Pair(4, 5, 0));
        sList.add(new Pair(4, 6, 0));
        sList.add(new Pair(5, 7, 0));
        sList.add(new Pair(6, 7, 0));

        wireframe = new Wireframe(pList, sList, color);
    }


    private void initPlanes(){
        planes = new Plane[6];

        double xMin = minPoint.getCoordinate(0);
        double yMin = minPoint.getCoordinate(1);
        double zMin = minPoint.getCoordinate(2);

        double xMax = maxPoint.getCoordinate(0);
        double yMax = maxPoint.getCoordinate(1);
        double zMax = maxPoint.getCoordinate(2);

        planes[0] = new Plane(new Vector(new double[]{-1, 0, 0}), xMin);
        planes[1] = new Plane(new Vector(new double[]{1, 0, 0}), xMax);

        planes[2] = new Plane(new Vector(new double[]{0, -1, 0}), yMin);
        planes[3] = new Plane(new Vector(new double[]{0, 1, 0}), yMax);

        planes[4] = new Plane(new Vector(new double[]{0, 0, -1}), zMin);
        planes[5] = new Plane(new Vector(new double[]{0, 0, 1}), zMax);
    }
}
