package ru.nsu.fit.g13204.Khenkina.view;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;

public class StatusBar extends JPanel {
    private JLabel statusLabel;

    public StatusBar(JFrame owner){
        super();
        setBorder(new BevelBorder(BevelBorder.LOWERED));
        setPreferredSize(new Dimension(owner.getWidth(), 20));
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        statusLabel = new JLabel();
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        add(statusLabel);
    }

    public void setStatus(String status){
        statusLabel.setText(status);
    }

    public void clearStatus(){
        statusLabel.setText("");
    }
}
