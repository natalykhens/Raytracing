package ru.nsu.fit.g13204.Khenkina.view;

import ru.nsu.fit.g13204.Khenkina.surface.Coordinate;
import ru.nsu.fit.g13204.Khenkina.controller.Controller;
import ru.nsu.fit.g13204.Khenkina.surface.Segment;
import ru.nsu.fit.g13204.Khenkina.surface.Wireframe;
import ru.nsu.fit.g13204.Khenkina.world.Axes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class WireframePanel extends JPanel implements Observer{
    private Controller controller;
    private MainWindow window;
    private List<Wireframe> wireframes;
    private BufferedImage image;
    private Color backgroundColor = Color.WHITE;
    private Color borderColor = Color.BLACK;
    private Color cubeColor = Color.BLUE;
    private List<Color> segmentsColors;

    private double cameraWidth;
    private double cameraHeight;

    private int width = 100;
    private int height = 100;

    //private int currentSurfaceIndex = -1;
    private Coordinate prevCoordinate;
    private double xRotate;
    private double yRotate;

    private int ind1 = 0;
    private int ind2 = 1;

    private double coefficient;

    public WireframePanel(Controller control, MainWindow w){
        controller = control;
        window = w;
        wireframes = controller.getWireframes();
        segmentsColors = controller.getWorldColors();

        backgroundColor = controller.getBackgroundColor();

        xRotate = (double)width / 200000;
        yRotate = (double)height / 100000;

        initListeners();
        init(width, height);
    }

    private void initListeners(){
        addKeyListener(new KeyListener() {

            public void keyPressed(KeyEvent e) {
                System.out.println("Pressed");
            }

            public void keyReleased(KeyEvent e) {
                System.out.println("Released");
            }

            public void keyTyped(KeyEvent e) {
                System.out.println("Typed");
            }
        });



        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if(e.isControlDown()){
                    controller.changeYCameraPosition(e.getUnitsToScroll());
                }
                else if(e.isShiftDown()){
                    controller.changeXCameraPosition(e.getUnitsToScroll());
                }
                else {
                    controller.changeCameraFocus(e.getUnitsToScroll());
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                prevCoordinate = new Coordinate(e.getX(), e.getY());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                prevCoordinate = null;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (prevCoordinate != null) {
                    processCoordinate(e.getX(), e.getY());
                } else {
                    prevCoordinate = new Coordinate(e.getX(), e.getY());
                }
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int newWidth = getWidth();
                int newHeight = getHeight();
                changeSize(newWidth, newHeight);
            }
        });
    }

    public int getImageWidth(){
        return image.getWidth();
    }

    public int getImageHeight(){
        return image.getHeight();
    }

    public BufferedImage getImage(){
        return image;
    }

    private void changeSize(int w, int h){
        if(w != width || h != height){
            width = w;
            height = h;
            recountImageParameters();
            drawWireframe();
            window.repaint();
        }
    }

    private void recountImageParameters(){
        cameraWidth = controller.getWidth();
        cameraHeight = controller.getHeight();

        double xCoefficient = cameraWidth / width;
        double yCoefficient = cameraHeight / height;

        double newCoefficient = (xCoefficient > yCoefficient) ? xCoefficient : yCoefficient;

        if(newCoefficient != coefficient){
            coefficient = newCoefficient;

            image = new BufferedImage((int)(cameraWidth / coefficient), (int)(cameraHeight / coefficient), BufferedImage.TYPE_INT_RGB);
            initImage();
        }
    }

    private void processCoordinate(int x, int y){
        double alpha = - xRotate * (x - prevCoordinate.x);
        double beta = - yRotate * (y - prevCoordinate.y);

        controller.rotateCamera(alpha, beta);

        prevCoordinate.x = x;
        prevCoordinate.y = y;
    }

    public void changeColors(List<Color> colors){
        segmentsColors = colors;
        drawWireframe();
    }

    public void setBackgroundColor(Color c){
        backgroundColor = c;
    }

    public void reinit(){
        recountImageParameters();
        wireframes = controller.getWireframes();
        segmentsColors = controller.getWorldColors();
        initImage();
        //Проверить!!!!!
        recountImageParameters();
        //countCoefficient();
        drawWireframe();
        window.repaint();
    }

    private void init(int width, int height){
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        initImage();
        //Проверить!!!!!
        recountImageParameters();
        //countCoefficient();
        drawWireframe();
    }

    private void initImage(){
        //image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int color = backgroundColor.getRGB();
        for(int i = 0; i < image.getHeight(); ++i){
            for(int j = 0; j < image.getWidth(); ++j){
                image.setRGB(j, i, color);
            }
        }
        drawBorder();
    }

    private void drawBorder(){
        Graphics2D g2d = (Graphics2D)image.getGraphics();
        g2d.setColor(borderColor);
        g2d.drawRect(0, 0, image.getWidth() - 1, image.getHeight() - 1);
    }

    private void drawWireframe(){
        Graphics2D g = (Graphics2D)image.getGraphics();

        Color currentColor = segmentsColors.get(0);
        g.setColor(currentColor);

        Axes axes = controller.getWorldAxes();
        drawAxes(g, axes);

        for(int i = 0; i < wireframes.size(); ++i) {
            Wireframe w = wireframes.get(i);
            List<Segment> segments = w.getSegments();
            Color newColor = w.getColor();
            if (!newColor.equals(currentColor)) {
                g.setColor(newColor);
                currentColor = newColor;
            }
            for (Segment s : segments) {
                //Color newColor = segmentsColors.get(i);
                //Color newColor = Color.RED;
                /*if (!newColor.equals(currentColor)) {
                    g.setColor(newColor);
                    currentColor = newColor;
                }*/
                drawSegment(g, s);
            }

            //axes = w.getAxes();
            //drawAxes(g, axes);
        }

        /*g.setColor(cubeColor);
        Wireframe cube = controller.getWorldCube();
        List<Segment> segments = cube.getSegments();
        for (Segment s : segments) {
            drawSegment(g, s);
        }*/
    }

    private void drawAxes(Graphics2D g, Axes axes){
        BasicStroke pen1 = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
        g.setStroke(pen1);

        Color c = g.getColor();
        g.setColor(Color.RED);
        drawSegment(g, axes.getXAxis());
        g.setColor(Color.GREEN);
        drawSegment(g, axes.getYAxis());
        g.setColor(Color.BLUE);
        drawSegment(g, axes.getZAxis());

        pen1 = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
        g.setStroke(pen1);
        g.setColor(c);
    }

    private void drawSegment(Graphics2D g, Segment s){
        double x1 = s.point1.getCoordinate(ind1);
        double y1 = s.point1.getCoordinate(ind2);
        double x2 = s.point2.getCoordinate(ind1);
        double y2 = s.point2.getCoordinate(ind2);

        /*Coordinate coord1 = new Coordinate(x1, y1);
        Coordinate coord2 = new Coordinate(x2, y2);

        coord1 = mapToScreen(coord1);
        coord2 = mapToScreen(coord2);
        g.drawLine((int) coord1.x, (int) coord1.y, (int) coord2.x, (int) coord2.y);*/



        double z1 = s.point1.getCoordinate(2);
        double z2 = s.point2.getCoordinate(2);

        double w1 = s.point1.getCoordinate(3);
        double w2 = s.point2.getCoordinate(3);

        if(w1 != 1.0){
            //System.out.println("W1 : " + w1 + " Z " + s.point1.getCoordinate(2) / w1);
            x1 /= w1;
            y1 /= w1;
            //z1 /= w1;
        }
        if(w2 != 1.0){
            x2 /= w2;
            y2 /= w2;
            //z2 /= w2;
        }

        double focus = controller.getCameraFocus();
        double zb = controller.getCameraBackground();

        double yMin = -cameraHeight / 2;
        double yMax = cameraHeight / 2;
        double xMin = -cameraWidth / 2;
        double xMax = cameraWidth / 2;


        boolean condition1 = (x1 >= xMin && x1 <= xMax && y1 >= yMin
                && y1 <= yMax && z1 >= focus && z1 <= zb);
        boolean condition2 = (x2 >= xMin && x2 <= xMax && y2 >= yMin
                && y2 <= yMax && z2 >= focus && z2 <= zb);

        Coordinate coord1 = new Coordinate(x1, y1);
        Coordinate coord2 = new Coordinate(x2, y2);

        if(condition1 && condition2) {
            coord1 = mapToScreen(coord1);
            coord2 = mapToScreen(coord2);

            g.drawLine((int) coord1.x, (int) coord1.y, (int) coord2.x, (int) coord2.y);
        }
        else if(condition1){
            coord2 = findCoordinate(x1, y1, z1, x2, y2, z2, focus, zb, yMin, yMax, xMin, xMax);

            coord1 = mapToScreen(coord1);
            coord2 = mapToScreen(coord2);
            g.drawLine((int) coord1.x, (int) coord1.y, (int) coord2.x, (int) coord2.y);
        }
        else if(condition2){
            coord1 = findCoordinate(x2, y2, z2, x1, y1, z1, focus, zb, yMin, yMax, xMin, xMax);

            coord1 = mapToScreen(coord1);
            coord2 = mapToScreen(coord2);
            g.drawLine((int) coord1.x, (int) coord1.y, (int) coord2.x, (int) coord2.y);
        }
    }

    private Coordinate findCoordinate(double x1, double y1, double z1, double x2, double y2, double z2,
                                      double zMin, double zMax, double yMin, double yMax, double xMin, double xMax){
        double x = 0, y = 0;
        if(z2 <= zMin || z2 >= zMax){
            double z;
            if(z2 <= zMin){
                z = zMin;
            }
            else{
                z = zMax;
            }
            double dz = z2 - z1;

            y = (z - z1) / dz * (y2 - y1) + y1;
            x = (z - z1) / dz * (x2 - x1) + x1;
        }
        else if(y2 <= yMin || y2 >= yMax){
            if(y2 <= yMin){
                y = yMin;
            }
            else{
                y = yMax;
            }
            x = (y - y1) / (y2 - y1) * (x2 - x1) + x1;
        }
        else if(x2 <= xMin || x2 >= xMax){
            if(x2 <= xMin){
                x = xMin;
            }
            else{
                x = xMax;
            }
            y = (x - x1) / (x2 - x1) * (y2 - y1) + y1;
        }
        return new Coordinate(x, y);
    }

    private Coordinate mapToScreen(Coordinate coord){
        int width = image.getWidth();
        int height = image.getHeight();
        int newX = (int)Math.round(coord.x / coefficient + width / 2);
        int newY = height - (int)Math.round(coord.y / coefficient + height / 2);
        return new Coordinate(newX, newY);
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        if(image != null) {
            g.drawImage(image, 0, 0, backgroundColor, null);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        reinit();
    }
}
