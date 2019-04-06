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
            if (pauseButton.getText().equals("Start")) {
                simulation.inputSignal.update();
                pauseButton.setText("Pause");

                Simulation.mass.reset();
                simulation.springDamper1.reset();
                simulation.springDamper2.reset();

                Simulation.massInput.setEnabled(false);
                Simulation.inertiaInput.setEnabled(false);
                Simulation.stiffnessInput.setEnabled(false);
                Simulation.dampingInput.setEnabled(false);
                simulation.inputSignal.inputSelector.setEnabled(false);
                simulation.startTimer();
            } else if (pauseButton.getText().equals("Resume")){
                simulation.startTimer();
                pauseButton.setText("Pause");
            } else if (pauseButton.getText().equals("Pause")) {
                pauseButton.setText("Resume");
                simulation.stopTimer();
            }
        }
    }
}