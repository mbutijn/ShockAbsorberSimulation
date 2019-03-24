import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class Restart {
    private Simulation simulation;
    private JButton resetButton = new JButton("Restart");

    Restart(Simulation simulation) {
        this.simulation = simulation;
    }

    JButton makeButton() {
        resetButton.addActionListener(new Reset());
        resetButton.setSize(150, 50);
        return resetButton;
    }

    private class Reset implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Simulation.mass.reset();
            simulation.inputSignal.reset();
            simulation.springDamper1.reset();
            simulation.springDamper2.reset();
        }
    }
}