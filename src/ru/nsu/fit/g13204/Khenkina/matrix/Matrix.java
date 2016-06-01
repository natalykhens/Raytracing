package ru.nsu.fit.g13204.Khenkina.matrix;

public abstract class Matrix {

    public abstract double[][] getMatrix();


    public abstract Vector getColumn(int columnNumber);


    public Matrix multiply(Matrix m2) {
        double[][] matrix = getMatrix();
        double[][] matrix2 = m2.getMatrix();



        double[][] resultMatrix;
        resultMatrix = new double[matrix.length][];
        for(int i = 0; i < resultMatrix.length; ++i){
            resultMatrix[i] = new double[matrix2[0].length];
        }

        for(int i = 0; i < resultMatrix.length; ++i){
            for(int j = 0; j < resultMatrix[i].length; ++j){
                resultMatrix[i][j] = multiplyVectors(matrix[i], matrix2, j);
            }
        }

        return new DoubleMatrix(resultMatrix);
    }

    public Matrix inverse(){
        double[][] matrix = getMatrix();
        double determinant = countDeterminant(matrix);

        if(determinant == 0){
            return null;
        }

        double[][] m = new double[matrix.length][];
        for(int i = 0; i < m.length; ++i){
            m[i] = new double[matrix.length];
        }

        for(int i = 0; i < matrix.length; ++i){
            for(int j = 0; j < matrix.length; ++j){
                m[j][i] = countMinor(matrix, i, j) / determinant;
            }
        }

        return new DoubleMatrix(m);
    }

    public double get(int i, int j){
        double[][] matrix = getMatrix();
        return matrix[i][j];
    }

    private double countMinor(double[][] matrix, int line, int column){
        int[] columns = new int[matrix.length - 1];
        int[] lines = new int[matrix[0].length - 1];
        for(int i = 0; i < matrix.length; ++i){
            if(i < line){
                lines[i] = i;
            }
            else if(i > line){
                lines[i - 1] = i;
            }
        }
        for(int i = 0; i < matrix[0].length; ++i){
            if(i < column){
                columns[i] = i;
            }
            else if(i > column){
                columns[i - 1] = i;
            }
        }
        double m = countSubmatrixDeterminant(matrix, columns, lines);
        if((line + column) % 2 != 0){
            m = -m;
        }
        return m;
    }

    public double countDeterminant(double[][] matrix){
        double result = 0;

        int[] columns = new int[matrix.length - 1];
        int[] lines = new int[matrix[0].length - 1];
        for(int i = 1; i < matrix[0].length; ++i){
            lines[i - 1] = i;
        }

        for(int i = 0; i < matrix[0].length; ++i){
            for(int j = 0; j < matrix[0].length; ++j){
                if(j < i){
                    columns[j] = j;
                }
                else if(j > i){
                    columns[j - 1] = j;
                }
            }
            double submatrixDet = countSubmatrixDeterminant(matrix, columns, lines);
            double tmp = matrix[0][i] * submatrixDet;
            if(i % 2 == 0){
                result += tmp;
            }
            else{
                result -= tmp;
            }
        }
        return result;
    }

    private double countSubmatrixDeterminant(double[][] matrix, int[] columns, int[] lines){
        if(columns.length == 1){
            return matrix[lines[0]][columns[0]];
        }
        double result = 0;

        int[] newColumns = new int[columns.length - 1];
        int[] newLines = new int[lines.length - 1];
        for (int i = 1; i < lines.length; ++i) {
            newLines[i - 1] = lines[i];
        }

        for (int i = 0; i < columns.length; ++i) {
            for (int j = 0; j < columns.length; ++j) {
                if (columns[j] < columns[i]) {
                    newColumns[j] = columns[j];
                } else if (columns[j] > columns[i]) {
                    newColumns[j - 1] = columns[j];
                }
            }
            double submatrixDet = countSubmatrixDeterminant(matrix, newColumns, newLines);
            double tmp = matrix[lines[0]][columns[i]] * submatrixDet;
            if(i % 2 == 0){
                result += tmp;
            }
            else{
                result -= tmp;
            }
        }

        return result;
    }

    public static Matrix createIdentityMatrix(int dimension){
        return new DoubleMatrix(identityMatrix(dimension - 1));
    }

    public static Matrix createScaleMatrix(double scale){
        int dimension = 3;
        double[][] m = identityMatrix(dimension);

        for(int i = 0; i < m.length - 1; ++i){
            for(int j = 0; j < m[i].length - 1; ++j){
                if(i == j){
                    m[i][j] = scale;
                }
            }
        }
        return new DoubleMatrix(m);
    }

    public static Matrix createTranslateMatrix(Vector t){
        int dimension = t.getLength();
        double[][] m = identityMatrix(dimension);

        for(int i = 0; i < m.length; ++i){
            for(int j = 0; j < m[i].length; ++j){
                if(i < dimension && j == dimension){
                    m[i][j] = t.getCoordinate(i);
                }
            }
        }
        return new DoubleMatrix(m);
    }

    public static Matrix createRotationMatrix(double alpha, double beta, double gamma){
        Matrix xRotation = createXRotationMatrix(alpha);
        Matrix yRotation = createYRotationMatrix(beta);
        Matrix zRotation = createZRotationMatrix(gamma);

        Matrix result = null;

            result = xRotation.multiply(yRotation).multiply(zRotation);

        return result;

    }

    public static Matrix createXRotationMatrix(double theta){
        double[][] m = identityMatrix(3);
        double cosTheta = Math.cos(theta);
        double sinTheta = Math.sin(theta);

        m[1][1] = cosTheta;
        m[1][2] = -sinTheta;
        m[2][1] = sinTheta;
        m[2][2] = cosTheta;
        return new DoubleMatrix(m);
    }

    public static Matrix createYRotationMatrix(double theta){
        double[][] m = identityMatrix(3);
        double cosTheta = Math.cos(theta);
        double sinTheta = Math.sin(theta);

        m[0][0] = cosTheta;
        m[0][2] = sinTheta;
        m[2][0] = -sinTheta;
        m[2][2] = cosTheta;
        return new DoubleMatrix(m);
    }

    public static Matrix createZRotationMatrix(double theta){
        double[][] m = identityMatrix(3);
        double cosTheta = Math.cos(theta);
        double sinTheta = Math.sin(theta);

        m[0][0] = cosTheta;
        m[0][1] = -sinTheta;
        m[1][0] = sinTheta;
        m[1][1] = cosTheta;
        return new DoubleMatrix(m);
    }

    public static double[][] identityMatrix(int dimension){
        double[][] m = new double[dimension + 1][];
        for(int i = 0; i < m.length; ++i){
            m[i] = new double[dimension + 1];
        }
        for(int i = 0; i < m.length; ++i){
            for(int j = 0; j < m[i].length; ++j){
                if(i == j){
                    m[i][j] = 1.0;
                }
                else{
                    m[i][j] = 0.0;
                }
            }
        }
        return m;
    }

    private double multiplyVectors(double[] line, double[][] column, int columnNumber){
        double result = 0;
        for(int i = 0; i < line.length; ++i){
            result += line[i] * column[i][columnNumber];
        }
        return result;
    }
}
