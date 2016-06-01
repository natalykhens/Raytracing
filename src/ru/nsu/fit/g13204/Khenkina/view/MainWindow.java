package ru.nsu.fit.g13204.Khenkina.view;

import ru.nsu.cg.MainFrame;
import ru.nsu.fit.g13204.Khenkina.CameraParameters;
import ru.nsu.fit.g13204.Khenkina.controller.Mode;
import ru.nsu.fit.g13204.Khenkina.controller.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public class MainWindow extends MainFrame{
    private Controller controller;
    private StatusBar statusBar = new StatusBar(this);
    private WireframePanel wireframePanel;
    private RenderPanel renderPanel;

    private boolean settingsFlag = false;

    private Mode renderMode = Mode.WIREFRAME;

    public MainWindow(Controller control){
        super(1100, 800, "Wireframe");
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        controller = control;

        initWorld();

        add(statusBar, BorderLayout.SOUTH);
        try {
            initMenu();
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }

        add(wireframePanel, BorderLayout.CENTER);
        controller.registerWindow(this);
    }

    public void changeWorldColors(List<Color> colors){
        wireframePanel.changeColors(colors);
    }

    public void setBackgroundColor(Color c){
        wireframePanel.setBackgroundColor(c);
    }

    public void reinitWorld(){
        wireframePanel.reinit();
    }

    private void initWorld(){
        wireframePanel = new WireframePanel(controller, this);
        controller.addWorldObserver(wireframePanel);
    }

    private void initMenu() throws NoSuchMethodException {
        addSubMenu("File", KeyEvent.VK_F);
        addMenuItem("File/Open file", "Open a file", KeyEvent.VK_O, "Open.gif", null, "onFileOpen", statusBar);
        addMenuItem("File/Save", "Save file", KeyEvent.VK_S, "Save.gif", null, "onFileSaveAs", statusBar);
        addMenuItem("File/Save image", "Save image", KeyEvent.VK_S, "Save.gif", null, "onSaveImage", statusBar);
        addMenuSeparator("File");
        addMenuItem("File/Exit", "Exit application", KeyEvent.VK_X, "Exit.gif", null, "onExit", statusBar);

        addSubMenu("Model", KeyEvent.VK_V);
        addMenuItem("Model/Render", "Render image", KeyEvent.VK_R, "Play.gif", controller, "render", statusBar);
        addMenuItem("Model/Wireframe mode", "Wireframe mode", KeyEvent.VK_W, "About.gif", null, "onWireframeMode", statusBar);
        addMenuItem("Model/Settings", "Edit settings", KeyEvent.VK_T, "Wrench.gif", null, "onSettings", statusBar);
        addMenuItem("Model/Camera", "Edit camera settings", KeyEvent.VK_C, "Camera.gif", null, "onCameraSettings", statusBar);
        addMenuItem("Model/Init", "Init camera", KeyEvent.VK_I, "Init.gif", controller, "onInit", statusBar);


        addSubMenu("Help", KeyEvent.VK_H);
        addMenuItem("Help/About...", "Shows program version and copyright information", KeyEvent.VK_F1, "About.gif", null, "onAbout", statusBar);

        addToolBarButton("File/Open file", statusBar);
        addToolBarButton("File/Save", statusBar);
        addToolBarButton("File/Save image", statusBar);
        addToolBarSeparator();
        addToolBarButton("Model/Render", statusBar);
        addToolBarButton("Model/Wireframe mode", statusBar);
        addToolBarButton("Model/Settings", statusBar);
        addToolBarButton("Model/Camera", statusBar);
        addToolBarButton("Model/Init", statusBar);
        addToolBarSeparator();
        addToolBarButton("Help/About...", statusBar);
        addToolBarSeparator();
        addToolBarButton("File/Exit", statusBar);
    }

    @Override
    public void repaint(){
        super.repaint();
        if(renderPanel != null) {
            renderPanel.repaint();
        }
    }

    public void writeToStatusBar(String text){
        statusBar.setStatus(text);
    }

    public StatusBar getStatusBar(){
        return statusBar;
    }

    public void changeRenderMode(Mode mode, BufferedImage image){
        if(renderMode != mode){
            renderMode = mode;
            switch(renderMode){
                case RENDER:
                    remove(wireframePanel);
                    renderPanel = new RenderPanel(image);
                    add(renderPanel, BorderLayout.CENTER);
                    break;
                case WIREFRAME:
                    remove(renderPanel);
                    add(wireframePanel, BorderLayout.CENTER);
                    break;
            }
        }
    }

    public int getImageWidth(){
        return wireframePanel.getImageWidth();
    }

    public int getImageHeight(){
        return wireframePanel.getImageHeight();
    }
    public BufferedImage getWireframeImage(){
        return wireframePanel.getImage();
    }

    public void onWireframeMode(){
        controller.setWireframeMode();
    }

    public void onSettings(){
        RenderingParameters param = controller.getRenderingParameters();
        RenderingDialog dialog = new RenderingDialog(this, param);
        dialog.setVisible(true);
        controller.setRenderingParameters(param);
    }

    public void onCameraSettings(){
        CameraParameters param = controller.getCameraParameters();
        CameraDialog dialog = new CameraDialog(this, controller, param);
        dialog.setVisible(true);
    }

    public void onFileOpen(){
        File file = getOpenFileName("scene", "file", "FIT_13205_Kondyrev_Dmitriy_Raytracing_Data");
        if(file != null) {
            controller.openFile(file);
        }
    }

    public void onFileSaveAs() {
        File file = getSaveFileName("txt", "file", "FIT_13205_Kondyrev_Dmitriy_Raytracing_Data");
        if(file != null) {
            controller.saveFile(file);
        }
    }

    public void onSaveImage() {
        File file = getSaveFileName("bmp", "file", "FIT_13205_Kondyrev_Dmitriy_Raytracing_Data");
        if(file != null) {
            controller.saveImage(file);
        }
    }

    /**
     * Help/About... - shows program version and copyright information
     */
    public void onAbout()
    {
        JOptionPane.showMessageDialog(this, "Raytracing, version 1.0\nCopyright © 2016", "About program", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * File/Exit - exits application
     */
    public void onExit()
    {
        System.exit(0);
    }

    public void showErrorMessage(String message)
    {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
