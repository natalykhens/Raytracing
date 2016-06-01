package ru.nsu.fit.g13204.Khenkina.matrix;

public class IntMatrix extends Matrix {
    private int[][] matrix;
    private int coefficient;

    public IntMatrix(int[][] m, int coeff){
        matrix = m;
        coefficient = coeff;
    }

    @Override
    public double[][] getMatrix() {
        double[][] m = new double[matrix.length][];
        for(int i = 0; i < matrix.length; ++i){
            m[i] = new double[matrix[i].length];
            for(int j = 0; j < matrix[i].length; ++j){
                m[i][j] = (double)matrix[i][j] / coefficient;
            }
        }
        return m;
    }


    public double[] getLine(int lineNumber){
        double[] line = new double[matrix[lineNumber].length];
        for(int i = 0; i < matrix[lineNumber].length; ++i){
            line[i] = matrix[lineNumber][i] / coefficient;
        }
        return line;
    }

    @Override
    public Vector getColumn(int columnNumber){
        double[] column = new double[matrix.length];
        for(int i = 0; i < column.length; ++i){
            column[i] = matrix[i][columnNumber] / coefficient;
        }
        return new Vector(column);
    }

    public IntMatrix multiply(IntMatrix m2)  {
        int[][] matrix2 = m2.matrix;

        if(matrix[0].length != matrix2.length){

        }

        int[][] resultMatrix;
        resultMatrix = new int[matrix.length][];

        for(int i = 0; i < resultMatrix.length; ++i){
            resultMatrix[i] = new int[matrix2[0].length];
            for(int j = 0; j < resultMatrix[i].length; ++j){
                resultMatrix[i][j] = multiplyVectors(matrix[i], matrix2, j);
            }
        }

        return new IntMatrix(resultMatrix, coefficient * m2.coefficient);
    }

    private int multiplyVectors(int[] line, int[][] column, int columnNumber){
        int result = 0;
        for(int i = 0; i < line.length; ++i){
            result += line[i] * column[i][columnNumber];
        }
        return result;
    }
}
