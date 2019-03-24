import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class MouseControl {
    private JFrame frame;
    boolean controlling = false;

    MouseControl(JFrame frame) {
        this.frame = frame;
        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                controlling = !controlling;
                System.out.println("controlling with mouse: " + controlling);
            }
        });
    }

    double getMouseX(){
        double mousePositionX = getPoint().getX() - frame.getX();
        if(mousePositionX < 0) {
            mousePositionX = 0;
        } else if (mousePositionX > Simulation.xBound){
            mousePositionX = Simulation.xBound;
        }
        return mousePositionX / 100;
    }

    double getMouseY(){
        double mousePositionY = getPoint().getY() - frame.getY();
        if(mousePositionY < 0) {
            mousePositionY = 0;
        } else if (mousePositionY > Simulation.yBound){
            mousePositionY = Simulation.yBound;
        }
        return (Simulation.yBound + 30 - mousePositionY) / 100;
    }

    private Point getPoint(){
        return MouseInfo.getPointerInfo().getLocation();
    }
}
