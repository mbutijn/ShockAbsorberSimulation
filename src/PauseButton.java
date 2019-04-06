import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class PauseButton {
    private Simulation simulation;
    public JButton pauseButton = new JButton("Start");

    PauseButton(Simulation simulation) {
        this.simulation = simulation;
    }

    JButton makeButton() {
        pauseButton.addActionListener(new Pause());
        pauseButton.setSize(150, 50);
        return pauseButton;
    }

    private class Pause implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (pauseButton.getText().equals("Start") || pauseButton.getText().equals("Resume")) {
                simulation.inputSignal.update();
                pauseButton.setText("Pause");
                Simulation.massInput.setEnabled(false);
                Simulation.stiffnessInput.setEnabled(false);
                Simulation.dampingInput.setEnabled(false);
                simulation.inputSignal.inputSelector.setEnabled(false);
                simulation.startTimer();
            } else if (pauseButton.getText().equals("Pause")) {
                pauseButton.setText("Resume");
                simulation.stopTimer();
            }
        }
    }
}