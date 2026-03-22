package view;

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {

    private final Panel panel;

    public Window(int width, int height) {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("PGRF1 2024/2025");

        panel = new Panel(width, height);
        panel.setFocusable(true);
        add(panel);
        pack();
    }

    public void showWindow() {
        setVisible(true);
        panel.requestFocusInWindow();
    }

    public Panel getPanel() {
        return panel;
    }
}
