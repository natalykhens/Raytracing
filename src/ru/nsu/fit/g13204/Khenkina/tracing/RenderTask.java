package ru.nsu.fit.g13204.Khenkina.tracing;

import ru.nsu.fit.g13204.Khenkina.Camera;
import ru.nsu.fit.g13204.Khenkina.matrix.Vector;
import ru.nsu.fit.g13204.Khenkina.surface.Surface;

import java.util.List;

/**
 * Created by dima on 01.06.16.
 */
public class RenderTask implements Runnable {
    private int myNumber;

    private Render render;
    private int numThreads;
    private int width;
    private int height;
    private int numRays;
    private Vector init;
    private int depth;
    private Vector start;
    private Vector cu;
    private Vector cv;
    private List<Surface> surfaces;
    private double[][][] imageIntensity;
    private double[] maxIntensity;

    public RenderTask(int number, Render r, int numT, int w, int h, int numR, Vector i, int d,
                      Camera camera, List<Surface> surf, double[][][] imageInt, double[] maxInt){
        myNumber = number;
        render = r;
        numThreads = numT;
        width = w;
        height = h;
        numRays = numR;
        init = i;
        depth = d;

        start = camera.getCameraPosition();
        cu = camera.getU();
        cv = camera.getV();
        surfaces = surf;
        imageIntensity = imageInt;
        maxIntensity = maxInt;
    }

    @Override
    public void run() {
        System.out.println("Thread number " + myNumber + " started");
        double myMaxIntensity = 0;

        int linesCount = 0;

        for(int i = myNumber; i < width; i += numThreads){              //считаем интенсивность по всем пикселям
            //currentPercent = (int)Math.round(percentCoefficient * i);
            //controller.writeToStatusBar(Integer.toString(currentPercent));

            //render.percentage(linesCount);
            if(linesCount == 10){
                render.percentage(linesCount);
                linesCount = 0;
            }


            for(int j = 0; j < height; ++j){
                Ray[] ray = render.countRay(numRays, start, init, j, i, cu, cv);
                double[] intensity = render.countPixel(ray, depth, surfaces);
                for(int k = 0; k < intensity.length; ++k){              //параллельно ищем максимум интенсивности по всем цветам
                    if(intensity[k] > myMaxIntensity){
                        myMaxIntensity = intensity[k];
                    }
                }
                imageIntensity[i][j] = intensity;
            }
            linesCount++;
        }

        render.percentage(linesCount);
        maxIntensity[myNumber] = myMaxIntensity;

        System.out.println("Thread number " + myNumber + " finished");
    }
}
