package ru.nsu.fit.g13204.Khenkina.view;

import ru.nsu.fit.g13204.Khenkina.CameraParameters;
import ru.nsu.fit.g13204.Khenkina.controller.Controller;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CameraDialog extends JDialog implements ChangeListener{
    private MainWindow window;
    private Controller controller;
    private CameraParameters parameters;
    private JButton okButton = new JButton("Ok");

    private JPanel parametersPanel = new JPanel();
    private JPanel buttonsPanel = new JPanel();

    private JSpinner zfSpinner;
    private JSpinner zbSpinner;
    private JSpinner swSpinner;
    private JSpinner shSpinner;

    public CameraDialog(MainWindow w, Controller control, CameraParameters p){
        super(w, "Camera parameters", true);

        controller = control;
        window = w;
        parameters = p;

        JPanel tmp = new JPanel();
        tmp.setLayout(new BoxLayout(tmp, BoxLayout.Y_AXIS));

        parametersPanel.setLayout(new GridLayout(4, 2, 15, 5));
        parametersPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        init();


        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
        setLayout(new BorderLayout());
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        setSize(250, 250);
        setLocationRelativeTo(window);
        setResizable(false);

        initButtons();

        tmp.add(parametersPanel);
        tmp.add(buttonsPanel);
        add(tmp);
    }

    private void init(){
        zfSpinner = initSpinner(parameters.zf, "Zf", -10000.0, 10000.0, 0.5);
        zbSpinner = initSpinner(parameters.zb, "Zb", -10000.0, 10000.0, 0.5);
        swSpinner = initSpinner(parameters.sw, "Sw", 0.1, 1000.0, 1);
        shSpinner = initSpinner(parameters.sh, "Sh", 0.1, 1000.0, 1);
    }

    private JSpinner initSpinner(double initValue, String label, double minValue, double maxValue, double step){
        SpinnerModel model = new SpinnerNumberModel(initValue, minValue, maxValue, step);
        return initSpinner(model, label);
    }

    private JSpinner initSpinner(SpinnerModel model, String label){
        JSpinner spinner = new JSpinner(model);
        spinner.addChangeListener(this);
        spinner.setName(label);

        JLabel l = new JLabel(label, JLabel.CENTER);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        parametersPanel.add(l);
        parametersPanel.add(spinner);
        return spinner;
    }

    private void initButtons() {
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });
        buttonsPanel.add(okButton);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSpinner spinner = (JSpinner)e.getSource();

        double value = (double)spinner.getValue();
        switch(spinner.getName()){
            case "Zf":
                parameters.zf = value;
                break;
            case "Zb":
                parameters.zb = value;
                break;
            case "Sw":
                parameters.sw = value;
                break;
            case "Sh":
                parameters.sh = value;
                break;
        }
        controller.setCameraParameters(parameters);
    }
}
