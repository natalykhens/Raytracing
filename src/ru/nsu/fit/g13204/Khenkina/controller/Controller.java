package ru.nsu.fit.g13204.Khenkina.controller;

import ru.nsu.fit.g13204.Khenkina.Camera;
import ru.nsu.fit.g13204.Khenkina.CameraParameters;
import ru.nsu.fit.g13204.Khenkina.file.ParseFileException;
import ru.nsu.fit.g13204.Khenkina.file.Parser;
import ru.nsu.fit.g13204.Khenkina.file.SceneFileData;
import ru.nsu.fit.g13204.Khenkina.surface.Surface;
import ru.nsu.fit.g13204.Khenkina.surface.Wireframe;
import ru.nsu.fit.g13204.Khenkina.tracing.Render;
import ru.nsu.fit.g13204.Khenkina.tracing.RenderQuality;
import ru.nsu.fit.g13204.Khenkina.view.MainWindow;
import ru.nsu.fit.g13204.Khenkina.world.Axes;
import ru.nsu.fit.g13204.Khenkina.world.World;
import ru.nsu.fit.g13204.Khenkina.file.RenderFileData;
import ru.nsu.fit.g13204.Khenkina.view.RenderingParameters;
import ru.nsu.fit.g13204.Khenkina.world.LightSource;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Controller {
    private MainWindow window;
    private World world;
    private Camera camera;
    private List<Color> colors = new ArrayList<>();
    private Mode mode = Mode.WIREFRAME;
    private BufferedImage lastRenderedImage = null;


    private Color backgroundColor = Color.BLACK;
    private double gamma = 1;
    private int depth = 3;
    private RenderQuality quality = RenderQuality.NORMAL;

    private Render render;

    public Controller(){
        init();

        ru.nsu.fit.g13204.Khenkina.matrix.Vector r = new ru.nsu.fit.g13204.Khenkina.matrix.Vector(new double[] {-50, 0, 0});
        ru.nsu.fit.g13204.Khenkina.matrix.Vector viewPoint = new ru.nsu.fit.g13204.Khenkina.matrix.Vector(new double[] {0, 0, 0});
        ru.nsu.fit.g13204.Khenkina.matrix.Vector up = new ru.nsu.fit.g13204.Khenkina.matrix.Vector(new double[] {0, 0, 1});


            camera = new Camera(r, viewPoint, up, 8, 3, 2);

        render = new Render(camera, world, this);
    }

    public List<Wireframe> getWireframes(){
        return world.getWireframes(camera.getCameraMatrix());
    }

    public Axes getWorldAxes(){
        return world.getAxes(camera.getCameraMatrix());
    }

    private void init(){
        List<Surface> itemList = new ArrayList<>();
        List<LightSource> lightSources = new LinkedList<>();
        Color diffusedLight = Color.WHITE;
        colors.add(Color.BLACK);
        world = new World(itemList, lightSources, diffusedLight);
    }

    public List<Color> getWorldColors(){
        return colors;
    }

    public Color getBackgroundColor(){
        return backgroundColor;
    }

    public void rotateCamera(double alpha, double beta){
        camera.rotate(alpha, beta);
    }

    public void changeCameraFocus(double direction){
        double dz = 1.03;
        if(direction < 0){
            dz = 1.0 / dz;
        }
        camera.changeFocus(dz);
    }


    public void changeXCameraPosition(double dx){
        double position = 0.4;
        if(dx < 0){
            position = -position;
        }
        camera.up(position);
    }

    public void changeYCameraPosition(double dx){
        double position = 0.4;
        if(dx < 0){
            position = -position;
        }
        camera.right(position);
    }

    public void registerWindow(MainWindow w){
        window = w;
    }

    public void addWorldObserver(Observer o){
        world.addObserver(o);
        camera.addObserver(o);
    }

    public World getWorld(){
        return world;
    }

    public double getWidth(){
        return camera.getWidth();
    }

    public double getHeight(){
        return camera.getHeight();
    }

    public double getCameraFocus(){
        return camera.getFocus();
    }

    public double getCameraBackground(){
        return camera.getZb();
    }

    public CameraParameters getCameraParameters(){
        return camera.getCameraParameters();
    }

    public void setCameraParameters(CameraParameters param){
        camera.setCameraParameters(param);
    }

    public void openFile(File file){
        try {
            String extension;
            String fileName = file.getName();
            if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
                extension = fileName.substring(fileName.lastIndexOf(".")+1);
            else extension = "";

            if(extension.equals("scene")){
                SceneFileData data = Parser.readScene(file);
                world.reinit(data.surfaces, data.sources, data.diffusedLight);
                window.repaint();
            }
            else{
                RenderFileData data = Parser.readRenderFile(file);

                backgroundColor = data.background;
                gamma = data.gamma;
                depth = data.depth;
                quality = data.quality;

                    camera.reinit(data.cameraPosition, data.viewPoint, data.up, data.zf, data.zb, data.sw, data.sh);


            }

        } catch (IOException e) {
            window.showErrorMessage("Can not read the file " + file.getName());
            return;
        } catch (ParseFileException e) {
            window.showErrorMessage("Incorrect file");
            return;
        }
    }

    public void saveFile(File file){
        try {
            String extension;
            String fileName = file.getName();
            if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
                extension = fileName.substring(fileName.lastIndexOf(".") + 1);
            }
            else{
                extension = "";
            }

            if(extension.equals("scene")){
                SceneFileData data = new SceneFileData(world.getDiffusedLight(), world.getLightSources(), world.getSurfaces());
                Parser.writeScene(data, file);
            }
            else{

            }

        } catch (IOException e) {
            window.showErrorMessage("Can not save the file " + file.getName());
            return;
        }
    }


    public void saveImage(File file){
        BufferedImage image = null;

        switch(mode){
            case RENDER:
                image = lastRenderedImage;
                break;
            case WIREFRAME:
                image = window.getWireframeImage();
                break;
        }

        try {
            if(image != null){
                ImageController.saveImage(image, file);
            }
        } catch (IOException e) {
            window.showErrorMessage("Can not write the file " + file.getName());
        }
    }



    public void onInit(){
        camera.resetParameters();
    }

    public void render(){
        if(mode == Mode.WIREFRAME) {
            render.init(window.getImageWidth(), window.getImageHeight(), depth, quality, backgroundColor, gamma);

            Thread renderThread = new Thread(render);
            renderThread.start();
        }
    }


    public void renderFinish(){
        mode = Mode.RENDER;
        lastRenderedImage = render.getImage();
        window.changeRenderMode(mode, lastRenderedImage);
    }

    public void writeToStatusBar(String text){
        window.writeToStatusBar(text);
    }

    public RenderingParameters getRenderingParameters(){
        RenderingParameters param = new RenderingParameters(backgroundColor, gamma, depth, quality);
        return param;
    }

    public void setRenderingParameters(RenderingParameters param){
        backgroundColor = param.background;
        gamma = param.gamma;
        depth = param.depth;
        quality = param.quality;
    }


    public void setWireframeMode(){
        if(mode != Mode.WIREFRAME) {
            mode = Mode.WIREFRAME;
            window.changeRenderMode(mode, null);
        }
    }
}
