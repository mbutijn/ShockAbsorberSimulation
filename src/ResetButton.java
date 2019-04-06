import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ResetButton {
    private Simulation simulation;
    private JButton resetButton = new JButton("Reset");

    public ResetButton(Simulation simulation) {
        this.simulation = simulation;
    }

    JButton makeButton() {
        resetButton.addActionListener(new Reset());
        resetButton.setSize(150, 50);
        return resetButton;
    }

    private class Reset implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            simulation.stopTimer();

            simulation.inputSignal.reset();
            simulation.inputSignal.update();

            simulation.springDamper1.reset();
            simulation.springDamper2.reset();

//            simulation.springDamper1.updatePoints();
//            simulation.springDamper2.updatePoints();

            Simulation.mass.reset();
            Simulation.mass.updatePoints();

            simulation.movement.repaint();
            simulation.pause.pauseButton.setText("Start");

            Simulation.massInput.setEnabled(true);
            Simulation.stiffnessInput.setEnabled(true);
            Simulation.dampingInput.setEnabled(true);
            simulation.inputSignal.inputSelector.setEnabled(true);
        }
    }
}
