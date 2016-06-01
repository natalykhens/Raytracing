package ru.nsu.fit.g13204.Khenkina.surface;

/**
 * Created by Natalia on 27.05.16.
 */
public class OpticalCharacteristics {
    public double[] kd;
    public double[] ks;
    public double power;

    public OpticalCharacteristics(double[] d, double[] s, double pow){
        kd = d;
        ks = s;
        power = pow;
    }
}
