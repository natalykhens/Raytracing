package ru.nsu.fit.g13204.Khenkina.view;

import ru.nsu.fit.g13204.Khenkina.tracing.RenderQuality;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Created by Natalia on 01.06.16.
 */
public class RenderingDialog extends JDialog implements ChangeListener {
    private RenderingParameters parameters;
    private JSpinner gammaSpinner;
    private JSpinner depthSpinner;
    private JComboBox<RenderQuality> qualityBox = new JComboBox<>(RenderQuality.values());

    private JSpinner mSpinner;
    private JSpinner nSpinner;
    private JSpinner kSpinner;
    private JSpinner redSpinner;
    private JSpinner greenSpinner;
    private JSpinner blueSpinner;

    private JSpinner aSpinner;
    private JSpinner bSpinner;
    private JSpinner cSpinner;
    private JSpinner dSpinner;

    private JSpinner cxSpinner;
    private JSpinner cySpinner;
    private JSpinner czSpinner;

    private JButton createButton;
    private JButton deleteButton;

    private int red;
    private int green;
    private int blue;

    public RenderingDialog(JFrame parent, RenderingParameters param){
        super(parent, "Rendering parameters", true);
        setSize(new Dimension(500, 200));
        setPreferredSize(new Dimension(1000, 1000));
        setLocationRelativeTo(parent);

        parameters = param;
        red = parameters.background.getRed();
        green = parameters.background.getGreen();
        blue = parameters.background.getBlue();


        initParametersPanel();
    }

    public void reinit(RenderingParameters param){
        parameters = param;
        red = parameters.background.getRed();
        green = parameters.background.getGreen();
        blue = parameters.background.getBlue();

        reinitParametersPanel();
    }

    private void reinitParametersPanel(){
        redSpinner.setValue(red);
        greenSpinner.setValue(green);
        blueSpinner.setValue(blue);
    }

    private void initParametersPanel(){
        setLayout(new GridLayout(3, 4, 15, 5));
        JPanel panel = (JPanel)getContentPane();
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //numberSpinner = initSpinner(index, "№", 0, numSurfaces - 1, 1);

        gammaSpinner = initSpinner(parameters.gamma, "Gamma", 0.0, 15.0, 0.1);
        redSpinner = initColorSpinner(red, "R");

        depthSpinner = initSpinner(parameters.depth, "Depth", 1, 15, 1);
        greenSpinner = initColorSpinner(green, "G");


        JLabel l = new JLabel("Quality", JLabel.TRAILING);
        add(l);
        l.setLabelFor(qualityBox);

        qualityBox.setMaximumRowCount(RenderQuality.values().length);
        qualityBox.setEditable(false);
        qualityBox.setSelectedItem(parameters.quality);

        qualityBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                RenderQuality quality = (RenderQuality) qualityBox.getSelectedItem();
                parameters.quality = quality;
            }
        });

        add(qualityBox);

        blueSpinner = initColorSpinner(blue, "B");


        /*mSpinner = initSpinner(grid.m, "m", 1, Grid.MAX_M, 1);
        nSpinner = initSpinner(grid.n, "n", 1, Grid.MAX_N, 1);
        kSpinner = initSpinner(grid.k, "k", 1, Grid.MAX_K, 1);

        aSpinner = initSpinner(grid.a, "a", Grid.MIN_AB, Grid.MAX_AB, 0.01);
        bSpinner = initSpinner(grid.b, "b", Grid.MIN_AB, Grid.MAX_AB, 0.01);
        cSpinner = initSpinner(grid.c, "c", Grid.MIN_CD, Grid.MAX_CD, 0.01);
        dSpinner = initSpinner(grid.d, "d", Grid.MIN_CD, Grid.MAX_CD, 0.01);

        cxSpinner = initSpinner(parameters.center.getCoordinate(0), "Cx", -1000, 1000, 0.5);
        cySpinner = initSpinner(parameters.center.getCoordinate(1), "Cy", -1000, 1000, 0.5);
        czSpinner = initSpinner(parameters.center.getCoordinate(2), "Cz", -1000, 1000, 0.5);*/


        /*add(new JLabel());
        ImageIcon i = new ImageIcon(getClass().getResource("resources/Add.gif"));
        createButton = new JButton(i);
        i = new ImageIcon(getClass().getResource("resources/Delete.gif"));
        deleteButton = new JButton(i);
        add(createButton);*/


        //add(new JLabel());
        //add(deleteButton);

        //initButtons();
    }

    private JSpinner initSpinner(int initValue, String label, int minValue, int maxValue, int step){
        SpinnerModel model = new SpinnerNumberModel(initValue, minValue, maxValue, step);
        return initSpinner(model, label);
    }

    private JSpinner initSpinner(double initValue, String label, double minValue, double maxValue, double step){
        SpinnerModel model = new SpinnerNumberModel(initValue, minValue, maxValue, step);
        return initSpinner(model, label);
    }

    private JSpinner initSpinner(SpinnerModel model, String label){
        JSpinner spinner = new JSpinner(model);
        spinner.addChangeListener(this);
        spinner.setName(label);

        JLabel l = new JLabel(label, JLabel.TRAILING);
        add(l);
        l.setLabelFor(spinner);
        add(spinner);
        return spinner;
    }

    private JSpinner initColorSpinner(int initValue, String label){
        return initSpinner(initValue, label, 0, 255, 1);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSpinner spinner = (JSpinner)e.getSource();
        switch(spinner.getName()){
            case "gamma":
                double gamma = (double)spinner.getValue();
                parameters.gamma = gamma;
                break;
            case "depth":
                int depth = (int)spinner.getValue();
                parameters.depth = depth;
                break;
            case "R":
                red = (int)spinner.getValue();
                parameters.background = new Color(red, green, blue);
                break;
            case "G":
                green = (int)spinner.getValue();
                parameters.background = new Color(red, green, blue);
                break;
            case "B":
                blue = (int)spinner.getValue();
                parameters.background = new Color(red, green, blue);
                break;
        }
    }
}