package ru.nsu.fit.g13204.Khenkina;

import ru.nsu.fit.g13204.Khenkina.controller.Controller;
import ru.nsu.fit.g13204.Khenkina.view.MainWindow;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        Controller controller = new Controller();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainWindow window = new MainWindow(controller);
                window.setVisible(true);
            }
        });
    }
}
