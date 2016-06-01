package ru.nsu.fit.g13204.Khenkina;

public class CameraParameters {
    public double zf = 8;
    public double zb = 16;
    public double sw = 3;
    public double sh = 2;

    public CameraParameters(){}

    public CameraParameters(double f, double b, double w, double h){
        zf = f;
        zb = b;
        sw = w;
        sh = h;
    }
}
