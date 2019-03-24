import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Simulation {

    private Movement movement = new Movement();
    private Timer timer;
    static double samplePeriod = 0.01; // s
    InputSignal inputSignal;
    SpringDamper springDamper1, springDamper2;
    static Mass mass;
    static final int DISTANCE_SCALE = 100; // 1 meter = 100 pixels
    static int xBound = 700; // cm
    static int yBound = 560; // cm
    private InputType[] inputTypes = {InputType.speed_bump, InputType.impulse, InputType.step, InputType.ramp, InputType.block, InputType.sinusoid,InputType.none};
    private JComboBox<InputType> inputSelector = new JComboBox<>(inputTypes);
    private InputType inputType = InputType.speed_bump;
    static MouseControl mouseControl;

    public static void main(String args[]) {
        new Simulation().makeUI();
    }

    private void makeUI(){
        JFrame frame = new JFrame("Shock absorber");
        frame.setVisible(true);
        frame.setSize(xBound, yBound);
        frame.setContentPane(movement);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1));

        inputSelector.setVisible(true);
        inputSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputSignal.inputType = (InputType) inputSelector.getSelectedItem();
            }
        });
        panel.add(inputSelector);
        panel.add(new Restart(this).makeButton());

        // Make the input points
        double nominalHeight = 1;
        inputSignal = new InputSignal(xBound, xBound / DISTANCE_SCALE, nominalHeight, 350, 590, inputType);

        // Make the springs
        double equilibriumLength = 1.8;
        double width = 2.0;
        double initialX = (inputSignal.attachPoint1.x + inputSignal.attachPoint2.x) / 2;
        double x1 = initialX - inputSignal.attachPoint1.x - 0.5 * width;
        double x2 = initialX - inputSignal.attachPoint2.x - 0.5 * width;
        double heightAboveGround = Math.sqrt(Math.pow(equilibriumLength, 2) - Math.pow(x1, 2));
        Vector attach1 = new Vector(x1, heightAboveGround);
        Vector attach2 = new Vector(x2, heightAboveGround);
        springDamper1 = new SpringDamper(equilibriumLength, 0.5, -10, -1, inputSignal.attachPoint1, attach1);
        springDamper2 = new SpringDamper(equilibriumLength, 0.5, -10, -1, inputSignal.attachPoint2, attach2);

        // Make the mass
        double height = 1.0;
        double initialY = nominalHeight + heightAboveGround - 0.5 * height;
        mass = new Mass(1, 1, height, width, springDamper1.points[10], springDamper2.points[10], new Vector(initialX, initialY));

        JPanel leftPanel = new JPanel();
        frame.getContentPane().add(BorderLayout.WEST, leftPanel);

        leftPanel.add(panel, BorderLayout.CENTER);
        mouseControl = new MouseControl(frame);

        timer = new Timer((int) (1000 * samplePeriod), update);
        startTimer();
    }

    private void startTimer() {
        timer.setRepeats(true);
        timer.start();
    }

    private ActionListener update = new ActionListener() {
        public void actionPerformed(ActionEvent evt) { // runs every 100 milliseconds
            // update the ground height
            inputSignal.update();

            // update the vectors of the spring points
            Vector force1 = springDamper1.updateForce(mass.points[2], mouseControl.controlling);
            Vector force2 = springDamper2.updateForce(mass.points[1], false);

            springDamper1.updatePoints();
            springDamper2.updatePoints();

            // Update the position of the mass
            mass.calculateMovement(force1, force2);
            mass.calculateRotation(force1, force2);

            mass.updatePoints();

            // Repaint
            movement.repaint();
        }
    };

    public Timer getTimer() {
        return timer;
    }

    class Movement extends JPanel {
        private static final long serialVersionUID = 1L;
        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g2d = (Graphics2D) graphics;

            // Draw the ground
            inputSignal.draw(g2d);

            // Draw the springs
            springDamper1.draw(g2d);
            springDamper2.draw(g2d);

            // Draw the mass
            mass.draw(g2d);
        }
    }
}
