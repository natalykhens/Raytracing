package ru.nsu.fit.g13204.Khenkina;

import ru.nsu.fit.g13204.Khenkina.matrix.DoubleMatrix;
import ru.nsu.fit.g13204.Khenkina.matrix.Matrix;
import ru.nsu.fit.g13204.Khenkina.matrix.Vector;

import java.util.Observable;

public class Camera extends Observable{
    private Matrix cameraMatrix;
    private Matrix projectionMatrix;
    private Vector cameraPosition;
    private Vector viewPoint;
    private Vector upVector;
    private double zb;
    private double zf;
    private double sw;
    private double sh;

    private Vector n;
    private Vector v;
    private Vector u;

    private boolean tmpFlag = false;

    public Camera(Vector r, Vector view, Vector up, double f, double w, double h) {
        cameraPosition = r;
        viewPoint = view;
        upVector = up;

        zf = f;
        //zb = 2 * zf;
        zb = 1000 * zf;
        sw = w;
        sh = h;

        initCameraMatrix();
        initProjectionMatrix();
    }

    public void reinit(Vector r, Vector view, Vector up, double f, double b, double w, double h) {
        cameraPosition = r;
        viewPoint = view;
        upVector = up;

        zf = f;
        zb = b;
        sw = w;
        sh = h;

        initCameraMatrix();
        initProjectionMatrix();

        setChanged();
        notifyObservers();
    }

    public void setParameters(double f, double b, double w, double h){
        zb = b;
        zf = f;
        sw = w;
        sh = h;

        setChanged();
        notifyObservers();
    }


    public Vector getCameraPosition(){
        return cameraPosition;
    }

    public Vector getNormal(){
        return n;
    }

    public Vector getV(){
        return v;
    }

    public Vector getU(){
        return u;
    }

    public void changeFocus(double dz){
        zf *= dz;
        zb = 1000 * zf;
        initProjectionMatrix();

        setChanged();
        notifyObservers();
    }


    public void up(double dx){
                    Vector offset = v.multiply(dx);

            cameraPosition = cameraPosition.add(offset);
            viewPoint = viewPoint.add(offset);

            initCameraMatrix();

            setChanged();
            notifyObservers();

    }

    public void right(double dx){

            Vector offset = u.multiply(dx);

            cameraPosition = cameraPosition.add(offset);
            viewPoint = viewPoint.add(offset);

            initCameraMatrix();

            setChanged();
            notifyObservers();
    }

    public void rotate(double alpha, double beta){

            double x = cameraPosition.getCoordinate(0);
            double y = cameraPosition.getCoordinate(1);
            double z = cameraPosition.getCoordinate(2);

            boolean xFlag = false, yFlag = false, zFlag = false;

            if(x < 0) {
                xFlag = true;
                x = Math.abs(x);
            }
            if(y < 0) {
                yFlag = true;
                y = Math.abs(y);
            }

            double r = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
            //double theta = Math.atan(Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)) / z);
            double theta = Math.acos(z / r);
            double phi = Math.atan(y / x);

            //System.out.println("1.) theta =" + theta + " phi = " + phi);


            if(xFlag && yFlag){
                phi += Math.PI;
            }
            else if(xFlag){
                phi = Math.PI - phi;
            }
            else if(yFlag){
                phi = 2 * Math.PI - phi;
            }

            theta += beta;
            phi += alpha;


            xFlag = false;
            yFlag = false;
            zFlag = false;

            if(theta > Math.PI){
                theta = 2 * Math.PI - theta;
                xFlag = true;
                yFlag = true;
                tmpFlag = true;
            }


            x = r * Math.sin(theta) * Math.cos(phi);
            y = r * Math.sin(theta) * Math.sin(phi);
            z = r * Math.cos(theta);



            if(xFlag){
                x = -x;
            }
            if(yFlag){
                y = -y;
            }

            cameraPosition = new Vector(new double[]{x, y, z});

            initCameraMatrix();

            //System.out.println("L = " + viewPoint.subtract(cameraPosition).module());
            //System.out.println("norm = " + n.getCoordinate(0) + " " + n.getCoordinate(1) + " " + n.getCoordinate(2));
            System.out.println("Camera position = " + cameraPosition.getCoordinate(0) + " " +
                    cameraPosition.getCoordinate(1) + " " + cameraPosition.getCoordinate(2));
            System.out.println("View point = " + viewPoint.getCoordinate(0) + " " +
                    viewPoint.getCoordinate(1) + " " + viewPoint.getCoordinate(2));


            setChanged();
            notifyObservers();
    }


    public double getFocus(){
        return zf;
    }

    public double getZb(){
        return zb;
    }

    public Matrix getCameraMatrix(){
        Matrix matrix = null;

            matrix = projectionMatrix.multiply(cameraMatrix);
            //matrix = cameraMatrix;

        return matrix;
        //return cameraMatrix;
        //return projectionMatrix;
    }

    public double getWidth(){
        return sw;
    }

    public double getHeight(){
        return sh;
    }

    public CameraParameters getCameraParameters(){
        return new CameraParameters(zf, zb, sw, sh);
    }

    public void setCameraParameters(CameraParameters param){
        zf = param.zf;
        zb = param.zb;
        sw = param.sw;
        sh = param.sh;

        initProjectionMatrix();

        setChanged();
        notifyObservers();
    }

    public void resetParameters(){
        CameraParameters param = new CameraParameters();

        zf = param.zf;
        zb = param.zb;
        sw = param.sw;
        sh = param.sh;

        initProjectionMatrix();

        setChanged();
        notifyObservers();
    }

    private void initCameraMatrix()  {
        n = viewPoint.subtract(cameraPosition);
        n.normalize();

        v = upVector.subtract(n.multiply(upVector.scalarProduct(n)));        // v = up - (up, n) * n
        v.normalize();
        u = n.vectorProduct(v);      // u = n x v

        //upVector = v.multiply(1);

        System.out.println("n = " + n.getCoordinate(0) + " " + n.getCoordinate(1) + " " + n.getCoordinate(2));
        System.out.println("v = " + v.getCoordinate(0) + " " + v.getCoordinate(1) + " " + v.getCoordinate(2));
        System.out.println("u = " + u.getCoordinate(0) + " " + u.getCoordinate(1) + " " + u.getCoordinate(2));

        double[][] matrix = new double[4][];
        for(int i = 0; i < 4; ++i){
            matrix[i] = new double[4];
        }

        initLine(u, matrix, 0);
        initLine(v, matrix, 1);
        initLine(n, matrix, 2);

        for(int i = 0; i < 3; ++i){
            matrix[3][i] = 0;
        }
        matrix[3][3] = 1;

        cameraMatrix = new DoubleMatrix(matrix);

        double[][] matrix_2 = Matrix.identityMatrix(3);
        for(int i = 0; i < 3; ++i){
            matrix_2[i][3] = -cameraPosition.getCoordinate(i);
        }
        cameraMatrix = cameraMatrix.multiply(new DoubleMatrix(matrix_2));


    }

    private void initLine(Vector v, double[][] m, int lineNumber)  {
        for(int i = 0; i < 3; ++i){
            m[lineNumber][i] = v.getCoordinate(i);
        }
        //m[lineNumber][3] = - v.scalarProduct(cameraPosition);
    }

    private void initProjectionMatrix(){
        double[][] matrix = new double[4][];
        for(int i = 0; i < 4; ++i){
            matrix[i] = new double[4];
        }
        matrix[0][0] = 1;
        matrix[1][1] = 1;
        matrix[2][2] = 1;
        //matrix[3][3] = 1;
        matrix[3][2] = 1.0 / zf;

        projectionMatrix = new DoubleMatrix(matrix);
    }

}
