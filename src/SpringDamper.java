class SpringDamper extends Drawable{
    private double equilibriumLength, width, stiffness, damping; // these properties do not change throughout simulation
    private double currentLength, oldLength; // these properties change throughout simulation
    private Vector initialAttachUp;
    private Vector attachmentDown;
    private Vector vector, force;
    private Vector[] rotatedPoints;
    private double[] widthPercentages  = {0, 0,    0.5, -0.5, 0.5, -0.5, 0.5, -0.5, 0.5, 0,    0};
    private double[] heightPercentages = {0, 0.15, 0.2,  0.3, 0.4,  0.5, 0.6,  0.7, 0.8, 0.85, 1.0};
    private boolean reset = false;

    SpringDamper(double equilibriumLength, double width, double stiffness, double damping, Vector attachmentDown, Vector initialAttachUp) {
        this.equilibriumLength = equilibriumLength;
        this.width = width;
        this.stiffness = stiffness;
        this.damping = damping;
        this.initialAttachUp = initialAttachUp;
        this.attachmentDown = attachmentDown;
        this.numberOfElements = heightPercentages.length;
        this.rotatedPoints = new Vector[numberOfElements];
        this.points = new Vector[numberOfElements];
        force = new Vector(0,0);
        vector = new Vector(0,0);
        initialize();
        updatePoints();
    }

    private void initialize() {
        currentLength = equilibriumLength;
        oldLength = equilibriumLength;

        vector.x = initialAttachUp.x - attachmentDown.x;
        vector.y = initialAttachUp.y - attachmentDown.y;

        vector.abs = vector.getAbs();
        vector.direction = vector.getDirection();

        //points[0] = attachmentDown;

        for (int i = 0; i < numberOfElements; i++){
            this.points[i] = new Vector(attachmentDown.x + widthPercentages[i] * width, attachmentDown.y + heightPercentages[i] * equilibriumLength);
            this.rotatedPoints[i] = new Vector(heightPercentages[i] * equilibriumLength, widthPercentages[i] * width);

            points[i].x = points[0].x + Math.cos(rotatedPoints[i].getDirection() + vector.direction) * rotatedPoints[i].getAbs();
            points[i].y = points[0].y + Math.sin(rotatedPoints[i].getDirection() + vector.direction) * rotatedPoints[i].getAbs();
        }

    }

    Vector updateForce(Vector attachmentUp, boolean mouseControl) {
        if (reset){
            force.x = 0;
            force.y = 0;
            force.abs = 0;
            reset = false;
        } else {
            if (mouseControl){
                // user input
                points[0].x = Simulation.mouseControl.getMouseX();
                points[0].y = Simulation.mouseControl.getMouseY();
            } else {
                // updateForce the height of the attachmentDown point
                points[0].y = attachmentDown.y;
                points[0].x = attachmentDown.x;
            }

            // the point where spring is connected to the mass
            points[numberOfElements - 1].x = attachmentUp.x;
            points[numberOfElements - 1].y = attachmentUp.y;

            // calculate the difference vector
            vector.x = attachmentUp.x - points[0].x;
            vector.y = attachmentUp.y - points[0].y;
            vector.direction = vector.getDirection();

            oldLength = currentLength;
            currentLength = vector.getAbs();

            double velocity = (currentLength - oldLength) / Simulation.samplePeriod;
            double displacement = (currentLength - equilibriumLength);

            double force_abs = displacement * stiffness + velocity * damping;
            force.x = force_abs * Math.cos(vector.direction);
            force.y = force_abs * Math.sin(vector.direction);
            force.abs = force_abs;
            force.direction = vector.direction;
        }
        return force;
    }

    void updatePoints() {
        for (int i = 1; i < numberOfElements; i++){
            rotatedPoints[i].x = heightPercentages[i] * currentLength;

            points[i].x = points[0].x + Math.cos(rotatedPoints[i].getDirection() + vector.direction) * rotatedPoints[i].getAbs();
            points[i].y = points[0].y + Math.sin(rotatedPoints[i].getDirection() + vector.direction) * rotatedPoints[i].getAbs();
        }
    }

    void reset() {
        try {
            stiffness = Double.parseDouble(Simulation.stiffnessInput.getText());
        } catch (Exception ex) {
            System.out.println("No numeric value entered for stiffness");
        }
        try {
            damping = Double.parseDouble(Simulation.dampingInput.getText());
        } catch (Exception ex) {
            System.out.println("No numeric value entered for damping");
        }
        initialize();
        reset = true;
    }
}
