package ru.nsu.fit.g13204.Khenkina.tracing;

import ru.nsu.fit.g13204.Khenkina.Camera;
import ru.nsu.fit.g13204.Khenkina.controller.Controller;
import ru.nsu.fit.g13204.Khenkina.controller.ImageController;
import ru.nsu.fit.g13204.Khenkina.matrix.Vector;
import ru.nsu.fit.g13204.Khenkina.surface.Surface;
import ru.nsu.fit.g13204.Khenkina.world.World;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Render implements Runnable {
    private Camera camera;
    private World world;
    private BufferedImage image;
    private double coefficient;

    private Controller controller;
    private int depth;
    private RenderQuality quality;

    private Color backgroundColor = Color.BLACK;
    private double gamma;

    private int linesCount = 0;
    double percentCoefficient;


    public Render(Camera c, World w, Controller control){
        camera = c;
        world = w;
        controller = control;
    }

    @Override
    public void run() {
        if(quality == RenderQuality.ROUGH){
            roughRender();
        }
        else {
            int numRays = 1;
            switch (quality) {
                case NORMAL:
                    numRays = 1;
                    break;
                case FINE:
                    numRays = 2;
                    break;
            }
            render(numRays, 4);
        }

        if(gamma != 1.0){
            image = ImageController.gammaCorrection(image, gamma);
        }



        controller.renderFinish();
    }


    public void init(int width, int height, int d, RenderQuality q, Color bColor, double g){
        initImage(width, height);
        depth = d;
        quality = q;
        backgroundColor = bColor;
        gamma = g;
    }

    public BufferedImage getImage(){
        return image;
    }

    private void initImage(int width, int height){
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }



    private void roughRender(){
        List<Surface> surfaces = world.getSurfaces();

        Vector start = camera.getCameraPosition();
        Vector cu = camera.getU();
        Vector cv = camera.getV();
        Vector init = countInitVector(start, camera.getNormal(), cu, cv);

        countCoefficient();
        int width = image.getWidth();
        int height = image.getHeight();


        double[] intensity;
        double[][][] imageIntensity = new double[width][][];
        for(int i = 0; i < imageIntensity.length; ++i){
            imageIntensity[i] = new double[height][];
        }

        double maxIntensity = 0;

        double percentCoefficient = 100.0 / width;
        int currentPercent;

        for(int i = 0; i < width; i += 2){              //считаем интенсивность по всем пикселям
            currentPercent = (int)Math.round(percentCoefficient * i);
            controller.writeToStatusBar(Integer.toString(currentPercent));

            for(int j = 0; j < height; j += 2){

                if((i == width / 2) && (j == height / 2)){
                    System.out.println("vector");
                }
                if((i == width -1) && (j == height -1)){
                    System.out.println("vector");
                }

                Ray ray = countRoughRay(start, init, j, i, cu, cv);
                intensity = countPixel(new Ray[]{ray}, depth, surfaces);
                for(int k = 0; k < intensity.length; ++k){              //параллельно ищем максимум интенсивности по всем цветам
                    if(intensity[k] > maxIntensity){
                        maxIntensity = intensity[k];
                    }
                }

                for(int k = 0; k < 2; ++k){
                    for(int l = 0; l < 2; ++l){
                        if(i + k < width && j + l < height) {
                            imageIntensity[i + k][j + l] = intensity;
                        }
                    }
                }
            }
        }

        countImage(imageIntensity, maxIntensity);
    }

    public void render(int numRays, int numThreads){
        List<Surface> surfaces = world.getSurfaces();

        Vector start = camera.getCameraPosition();
        Vector cu = camera.getU();
        Vector cv = camera.getV();
        Vector init = countInitVector(start, camera.getNormal(), cu, cv);

        countCoefficient();
        int width = image.getWidth();
        int height = image.getHeight();


        //double[] intensity;
        double[][][] imageIntensity = new double[width][][];
        for(int i = 0; i < imageIntensity.length; ++i){
            imageIntensity[i] = new double[height][];
        }

        double[] maxIntensity = new double[numThreads];
        maxIntensity[0] = 0;

        linesCount = 0;

        percentCoefficient = 100.0 / width;
        //int currentPercent;

        RenderTask[] tasks = new RenderTask[numThreads];
        Thread[] threads = new Thread[numThreads];

        for(int t = 0; t < numThreads; ++t){
            tasks[t] = new RenderTask(t, this, numThreads, width, height, numRays, init, depth, camera, surfaces, imageIntensity, maxIntensity);
            threads[t] = new Thread(tasks[t]);
            threads[t].start();
        }

        //Дожидаемся завершения всех тредов

        for(int t = 0; t < numThreads; ++t) {
            try {
                threads[t].join();
            } catch (InterruptedException e) {}
        }

        double totalMaxIntensity = maxIntensity[0];
        for(int i = 1; i < numThreads; ++i){
            if(maxIntensity[i] > totalMaxIntensity){
                totalMaxIntensity = maxIntensity[i];
            }
        }

        countImage(imageIntensity, totalMaxIntensity);
    }

    public synchronized void percentage(int num){
        linesCount += num;
        int currentPercent = (int)Math.round(percentCoefficient * linesCount);
        controller.writeToStatusBar(Integer.toString(currentPercent));
    }

    private void countImage(double[][][] intensity, double maxIntensity){
        int red, green, blue;
        for(int i = 0; i < intensity.length; ++i){
            for(int j = 0; j < intensity[i].length; ++j){

                if(i == 600 && j == 0){
                    System.out.println("Point");
                }

                red = (int)Math.round(intensity[i][j][0]  / maxIntensity * 255);
                green = (int)Math.round(intensity[i][j][1] / maxIntensity * 255);
                blue = (int)Math.round(intensity[i][j][2] / maxIntensity * 255);

                if(red < 0 || red > 255 || green < 0 || green > 255 || blue < 0 || blue > 255){
                    System.out.println("Что-то не так!");
                    red = 255;
                    green = 0;
                    blue = 0;
                }

                image.setRGB(i, j, new Color(red, green, blue).getRGB());
            }
        }
    }


    public double[] countPixel(Ray[] startRay, int depth, List<Surface> surfaces){
        double[] summTotalIntensity = new double[]{0, 0, 0};

        for(int k = 0; k < startRay.length; ++k) {

            Ray ray = startRay[k];
            Intersection intersection;
            ArrayList<Intersection> list = new ArrayList<>(depth);

            int numberIntersection = 0;

            Surface prevSurface = null;

            for (int i = 0; i < depth; ++i) {
                intersection = traceRay(ray, surfaces, prevSurface);
                if (intersection == null) {
                    break;
                }
                list.add(intersection);
                ray = intersection.outRay;
                ++numberIntersection;

                prevSurface = intersection.surface;
            }

            double[] totalIntensity = new double[]{0, 0, 0};

            double[] prevIntensity;
            double[] intensity;

            Vector prevPoint = new Vector(new double[]{0, 0, 0});

            //TODO добавить обработку случая numberIntersection = 0

            for (int i = numberIntersection - 1; i >= 0; --i) {
                Intersection inter = list.get(i);

                double d = Vector.distance(prevPoint, inter.outRay.getStart());

                Vector eye = null;

                    eye = camera.getCameraPosition().subtract(inter.outRay.getStart());

                eye.normalize();

                totalIntensity = inter.surface.countIntensity(world, inter.normal, eye, inter.inRay, inter.outRay, totalIntensity, d);
                prevPoint = inter.outRay.getStart();
            }

            for(int i = 0; i < totalIntensity.length; ++i){
                summTotalIntensity[i] += totalIntensity[i];
            }
        }

        for(int i = 0; i < summTotalIntensity.length; ++i){
            summTotalIntensity[i] /= summTotalIntensity.length;
        }

        return summTotalIntensity;
    }

    private Intersection traceRay(Ray ray, List<Surface> surfaces, Surface prevSurface){
        Intersection intersection;
        Intersection nearestIntersection = null;
        //Ray r = null;
        //Surface surface = null;
        //Ray outRay = null;


        Vector start = ray.getStart();

        double currentDistance = -1;
        boolean firstFlag = true;

        for(Surface s : surfaces){
            if(s == prevSurface){
                continue;
            }
            intersection = s.intersect(ray);
            if(intersection != null){
                //ищем ближайшее пересечение
                if(firstFlag){
                    //surface = s;
                    nearestIntersection = intersection;
                    currentDistance = Vector.distance(start, intersection.outRay.getStart());
                    //outRay = r;
                    firstFlag = false;
                }
                else{
                    double newDistance = Vector.distance(start, intersection.outRay.getStart());
                    if(newDistance < currentDistance){
                        //surface = s;
                        nearestIntersection = intersection;
                        currentDistance = newDistance;
                        //outRay = r;
                    }
                }

            }
        }
        //if(!firstFlag){
        //    return new Intersection(surface, ray, outRay);
        //}
        //return null;
        return nearestIntersection;
    }


    private Vector countInitVector(Vector start, Vector cn, Vector cu, Vector cv){
        double sw = camera.getWidth();
        double sh = camera.getHeight();
        double focus = camera.getFocus();

        Vector initVector = null;
        Vector center;

            center = start.add(cn.multiply(focus));
            initVector = center.subtract(cu.multiply(sw / 2));
            initVector = initVector.add(cv.multiply(sh / 2));
        return initVector;
    }

    private void countCoefficient(){
        double sw = camera.getWidth();
        coefficient = (double)image.getWidth() / sw;
    }

    public Ray[] countRay(int numberRays, Vector start, Vector init, int x, int y, Vector cu, Vector cv){
        Ray[] rays = new Ray[numberRays * numberRays];

        double pixelOffset = 0.5 / numberRays;

        //double currentXOffset;
        //double currentYOffset = pixelOffset;

        for(int i = 0; i < numberRays; ++i){
            for(int j = 0; j < numberRays; ++j){
                Vector dx;
                Vector p = null;
                                    dx = init.add(cu.multiply((y + (i + 0.5) * pixelOffset) / coefficient));
                    p = dx.subtract(cv.multiply((x + (j + 0.5) * pixelOffset) / coefficient));

                rays[i * numberRays + j] = new Ray(start, p);
            }
        }

        return rays;
    }


    private Ray countRoughRay(Vector start, Vector init, int x, int y, Vector cu, Vector cv){
        Vector dx;
        Vector p = null;

            dx = init.add(cu.multiply((y + 1) / coefficient));
            p = dx.subtract(cv.multiply((x + 1) / coefficient));

        return new Ray(start, p);
    }

}
