import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Simulation {

    private Timer timer;
    private InputType inputType = InputType.speed_bump;
    static TextField massInput = new TextField("10",1);
    static TextField stiffnessInput = new TextField("-10",1);
    static TextField dampingInput = new TextField("-1",1);
    JComboBox<InputType> a = new JComboBox<>();
    static int xBound = 800; // cm
    static int yBound = 500; // cm
    static final int DISTANCE_SCALE = 100; // 1 meter = 100 pixels
    static double samplePeriod = 0.01; // s
    static Mass mass;
    static MouseControl mouseControl;
    Movement movement = new Movement();
    PauseButton pause;
    InputSignal inputSignal;
    SpringDamper springDamper1, springDamper2;

    public static void main(String args[]) {
        new Simulation().makeUI();
    }

    private void makeUI() {
        JFrame frame = new JFrame("Shock absorber");
        frame.setVisible(true);
        frame.setSize(xBound, yBound);
        frame.setContentPane(movement);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Make the input points
        double nominalHeight = 1.5;
        inputSignal = new InputSignal(xBound, xBound / DISTANCE_SCALE, nominalHeight, 380, 620, inputType);

        // Make the springs
        double equilibriumLength = 1.4;
        double width = 2.0;
        double initialX = (inputSignal.attachPoint1.x + inputSignal.attachPoint2.x) / 2;
        double x1 = initialX - inputSignal.attachPoint1.x - 0.5 * width;
        double x2 = inputSignal.attachPoint2.x - initialX - 0.5 * width;
        double attachHeight = Math.sqrt(Math.pow(equilibriumLength, 2) - Math.pow(x1, 2)) + nominalHeight;
        Vector attach1 = new Vector(inputSignal.attachPoint1.x + x1, attachHeight);
        Vector attach2 = new Vector(inputSignal.attachPoint2.x - x2, attachHeight);
        springDamper1 = new SpringDamper(equilibriumLength, 0.5, -10, -1, inputSignal.attachPoint1, attach1);
        springDamper2 = new SpringDamper(equilibriumLength, 0.5, -10, -1, inputSignal.attachPoint2, attach2);

        // Make the mass
        double height = 1.0;
        double initialY = attachHeight - 0.5 * height;
        mass = new Mass(10, 1, height, width, springDamper1.points[10], springDamper2.points[10], new Vector(initialX, initialY));

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1));

        inputSignal.selectInputSignal();

        JPanel panel_double = new JPanel();
        panel_double.setLayout(new GridLayout(3, 3));

        addUIComponent(panel_double, massInput, "Mass: ", "kg");
        addUIComponent(panel_double, stiffnessInput, "Stiffness: ", "N/m");
        addUIComponent(panel_double, dampingInput, "Damping: ", "kg/s");

        JPanel inputPanel = new JPanel();
        inputPanel.add(inputSignal.inputSelector);
        panel.add(inputPanel);
        panel.add(panel_double);
        panel.setPreferredSize(new Dimension(160, 450));

        JPanel leftPanel = new JPanel();
        leftPanel.add(panel);
        leftPanel.setPreferredSize(new Dimension(160, 450));
        JPanel southPanel = new JPanel();
        southPanel.add(new ResetButton(this).makeButton());
        pause = new PauseButton(this);
        southPanel.add(pause.makeButton());
        JPanel northPanel = new JPanel();
        frame.getContentPane().add(northPanel, BorderLayout.NORTH);
        frame.getContentPane().add(southPanel, BorderLayout.SOUTH);
        frame.getContentPane().add(leftPanel, BorderLayout.WEST);

        mouseControl = new MouseControl(frame);
        timer = new Timer((int) (1000 * samplePeriod), update);
        timer.setRepeats(true);
    }

    private void addUIComponent(JPanel panel_double, TextField field, String property, String unit){
        panel_double.add(new Label(property));
        JPanel smallPanel = new JPanel();
        smallPanel.add(field);
        panel_double.add(smallPanel);
        panel_double.add(new Label(unit));
    }

    void startTimer() {
        timer.start();
    }

    void stopTimer() {
        timer.stop();
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
