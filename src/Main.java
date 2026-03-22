import controller.Controller3D;
import view.Window;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Window window = new Window(800, 600);
            new Controller3D(window.getPanel());
            window.showWindow();
        });
    }
}