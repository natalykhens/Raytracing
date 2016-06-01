package ru.nsu.fit.g13204.Khenkina.matrix;

public class Vector extends Matrix {
    private double[] vector;

    public Vector(double[] v){
        vector = v;
    }

    public Vector(Vector v){
        vector = new double[v.vector.length];
        for(int i = 0; i < vector.length; ++i){
            vector[i] = v.vector[i];
        }
    }

    public double[] getVector(){
        return vector;
    }

    public int getLength(){
        return vector.length;
    }

    @Override
    public Vector getColumn(int columnNumber){
        return this;
    }

    public double[] getLine(int lineNumber){
        double[] line = new double[1];
        line[0] = vector[lineNumber];
        return line;
    }

    @Override
    public double[][] getMatrix() {
        double[][] m = new double[vector.length][];
        for(int i = 0; i < vector.length; ++i){
            m[i] = new double[1];
            m[i][0] = vector[i];
        }
        return m;
    }

    public double getCoordinate(int index){
        return vector[index];
    }

    public void setCoordinate(int index, double val){
        vector[index] = val;
    }

    public void addToCoordinate(int index, double val){
        vector[index] += val;
    }

    public Vector vectorProduct(Vector v)  {
        double[] vector2 = v.vector;

        double[] result = new double[vector.length];

        result[0] = vector[1] * vector2[2] - vector2[1] * vector[2];
        result[1] = -(vector[0] * vector2[2] - vector2[0] * vector[2]);
        result[2] = vector[0] * vector2[1] - vector2[0] * vector[1];

        return new Vector(result);
    }

    public void normalize(){
        double mod = 0;
        for(int i = 0; i < vector.length; ++i){
            mod += Math.pow(vector[i], 2);
        }
        mod = Math.sqrt(mod);

        for(int i = 0; i < vector.length; ++i){
            vector[i] = vector[i] / mod;
        }
    }


    public double scalarProduct(Vector v) {
        double[] vector2 = v.vector;
        double result = 0;
        for(int i = 0; i < vector.length; ++i){
            result += vector[i] * vector2[i];
        }
        return result;
    }

    public Vector multiply(double k){
        double[] v = new double[vector.length];
        for(int i = 0; i < vector.length; ++i){
            v[i] = vector[i] * k;
        }
        return new Vector(v);
    }

    public double module(){
        double mod = 0;
        for(int i = 0; i < vector.length; ++i){
            mod += Math.pow(vector[i], 2);
        }
        return Math.sqrt(mod);
    }

    public double module2(){
        double mod = 0;
        for(int i = 0; i < vector.length; ++i){
            mod += Math.pow(vector[i], 2);
        }
        return mod;
    }

    public Vector add(Vector v) {
        double[] vector2 = v.vector;
        double[] sum = new double[vector.length];
        for(int i = 0; i < vector.length; ++i){
            sum[i] = vector[i] + vector2[i];
        }
        return new Vector(sum);
    }

    public Vector subtract(Vector v) {
        double[] vector2 = v.vector;
        double[] sum = new double[vector.length];
        for(int i = 0; i < vector.length; ++i){
            sum[i] = vector[i] - vector2[i];
        }
        return new Vector(sum);
    }


    @Override
    public boolean equals(Object o){
        Vector v2 = (Vector)o;
        for(int i = 0; i < vector.length; ++i){
            if(vector[i] != v2.vector[i]){
                return false;
            }
        }
        return true;
    }

    public static double distance(Vector v1, Vector v2) {
        double distance = 0;
        for(int i = 0; i < v1.vector.length; ++i){
            distance += Math.pow((v1.vector[i] - v2.vector[i]), 2);
        }
        distance = Math.sqrt(distance);
        return distance;
    }
}
