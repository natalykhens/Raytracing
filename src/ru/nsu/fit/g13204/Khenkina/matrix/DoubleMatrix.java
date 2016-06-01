package ru.nsu.fit.g13204.Khenkina.matrix;

public class DoubleMatrix extends Matrix {
    private double[][] matrix;

    public DoubleMatrix(double[][] m){
        matrix = m;
    }

    public double[] getLine(int lineNumber){
        return matrix[lineNumber];
    }

    @Override
    public Vector getColumn(int columnNumber){
        double[] column = new double[matrix.length];
        for(int i = 0; i < column.length; ++i){
            column[i] = matrix[i][columnNumber];
        }
        return new Vector(column);
    }

    @Override
    public double[][] getMatrix() {
        return matrix;
    }
}
