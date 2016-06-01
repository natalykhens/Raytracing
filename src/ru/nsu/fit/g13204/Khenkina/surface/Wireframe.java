package ru.nsu.fit.g13204.Khenkina.surface;

import ru.nsu.fit.g13204.Khenkina.matrix.Matrix;
import ru.nsu.fit.g13204.Khenkina.matrix.Vector;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Wireframe {
    private ArrayList<Vector> points;
    private ArrayList<Pair> segments;
    private Color color;


    public Wireframe(ArrayList<Vector> pList, ArrayList<Pair> sList, Color c){
        points = pList;
        segments = sList;
        color = c;
    }

    public Color getColor(){
        return color;
    }

    public void addSegment(Segment segment, int groupNumber){
        int index1 = -1;
        int index2 = -1;

        for(int i = 0; i < points.size(); ++i){
            Vector point = points.get(i);
            if(point.equals(segment.point1)){
                index1 = i;
            }
            if(point.equals(segment.point2)){
                index2 = i;
            }
        }

        if(index1 == -1){
            points.add(segment.point1);
            index1 = points.indexOf(segment.point1);
        }
        if(index2 == -1){
            points.add(segment.point2);
            index2 = points.indexOf(segment.point2);
        }

        addLink(index1, index2, groupNumber);
    }

    public void addWireframe(Wireframe w, Matrix rotationMatrix, Vector center, int group) {
        Matrix translateMatrix = Matrix.createTranslateMatrix(center);
        Matrix resultMatrix = translateMatrix;
        if(rotationMatrix != null){
            resultMatrix = translateMatrix.multiply(rotationMatrix);
        }

        ArrayList<Vector> pList = new ArrayList<>(w.points.size());
        for(Vector vector : w.points){
            Vector newPoint = resultMatrix.multiply(vector).getColumn(0);
            pList.add(newPoint);
        }

        for(Pair p : w.segments){
            Segment segment = new Segment(pList.get(p.index1), pList.get(p.index2), group);
            addSegment(segment, group);
        }
    }

    public Wireframe countNewWireframe(Matrix matrix) {
        ArrayList<Vector> pointsList = new ArrayList<>(points.size());
        ArrayList<Pair> segmentsList = new ArrayList<>(segments);

        for(int i = 0; i < points.size(); ++i){
            Vector newPoint = matrix.multiply(points.get(i)).getColumn(0);
            pointsList.add(i, newPoint);
        }

        return new Wireframe(pointsList, segmentsList, color);
    }

    public Dimensions countDimansions()  {
        Vector point = points.get(0);

        double xMax = point.getCoordinate(0);
        double yMax = point.getCoordinate(1);
        double zMax = point.getCoordinate(2);

        double xMin = xMax;
        double yMin = yMax;
        double zMin = zMax;

        double x, y, z;

        for(int i = 1; i < points.size(); ++i){
            point = points.get(i);
            x = point.getCoordinate(0);
            y = point.getCoordinate(1);
            z = point.getCoordinate(2);
            if(x > xMax){
                xMax = x;
            }
            if(x < xMin){
                xMin = x;
            }
            if(y > yMax){
                yMax = y;
            }
            if(y < yMin){
                yMin = y;
            }
            if(z > zMax){
                zMax = z;
            }
            if(z < zMin){
                zMin = z;
            }
        }
        Vector max = new Vector(new double[]{xMax, yMax, zMax});
        Vector min = new Vector(new double[]{xMin, yMin, zMin});
        return new Dimensions(min, max);
    }

    private void addPoints(ArrayList<Vector> newPoints, Pair p){
        Vector v1 = points.get(p.index1);
        Vector v2 = points.get(p.index2);

        int index1 = newPoints.indexOf(v1);
        int index2 = newPoints.indexOf(v2);

        if (index1 != -1){
            p.index1 = index1;
        }
        else{
            newPoints.add(v1);
            p.index1 = newPoints.size() - 1;
        }

        if (index2 != -1){
            p.index2 = index2;
        }
        else{
            newPoints.add(v2);
            p.index2 = newPoints.size() - 1;
        }
    }

    public List<Segment> getSegments(){
        List<Segment> list = new ArrayList<>(segments.size());
        for(Pair p : segments){
            Segment segment = new Segment(points.get(p.index1), points.get(p.index2), p.group);
            list.add(segment);
        }
        return list;
    }

    private void addLink(int point1, int point2, int group){
        segments.add(new Pair(point1, point2, group));
    }
}

class Pair {
    int index1;
    int index2;
    int group;

    Pair(int i1, int i2, int g){
        index1 = i1;
        index2 = i2;
        group = g;
    }
}
