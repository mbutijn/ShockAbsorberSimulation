public class Mass extends Drawable{
    private int mass;
    private double momentOfInertia;
    private double width, height;
    private final Vector initialPosition;
    private double theta, thetadot;
    private Vector acceleration, velocity, position;

    Mass(int mass, double momentOfInertia, double height, double width, Vector leftUp, Vector rightUp, Vector initial){
        this.mass = mass;
        this.momentOfInertia = momentOfInertia;
        this.height = height;
        this.width = width;
        this.numberOfElements = 5;
        this.initialPosition = initial;
        Vector leftDown = new Vector(leftUp.x, leftUp.y - height);
        Vector rightDown = new Vector(rightUp.x, rightUp.y - height);

        points = new Vector[]{rightDown, rightUp, leftUp, leftDown, rightDown};
        position = new Vector(0,0);
        initializeVectors();
    }

    private void initializeVectors(){
        acceleration = new Vector(0, 0);
        velocity = new Vector(0, 0);
        position.x = initialPosition.x;
        position.y = initialPosition.y;
        theta = 0;
        thetadot = 0;
    }

    private double integrate(double output, double integrand){
        return output + integrand * Simulation.samplePeriod;
    }

    void updatePoints(){
        points[0].x = position.x + Math.cos(theta) * 0.5 * width + Math.sin(theta) * 0.5 * height;
        points[0].y = position.y + Math.sin(theta) * 0.5 * width - Math.cos(theta) * 0.5 * height;

        points[1].x = position.x + Math.cos(theta) * 0.5 * width - Math.sin(theta) * 0.5 * height;
        points[1].y = position.y + Math.sin(theta) * 0.5 * width + Math.cos(theta) * 0.5 * height;

        points[2].x = position.x - Math.cos(theta) * 0.5 * width - Math.sin(theta) * 0.5 * height;
        points[2].y = position.y - Math.sin(theta) * 0.5 * width + Math.cos(theta) * 0.5 * height;

        points[3].x = position.x - Math.cos(theta) * 0.5 * width + Math.sin(theta) * 0.5 * height;
        points[3].y = position.y - Math.sin(theta) * 0.5 * width - Math.cos(theta) * 0.5 * height;

        points[4] = points[0];
    }

    void calculateMovement(Vector force1, Vector force2){
        acceleration.x = (force1.x + force2.x) / mass;
        acceleration.y = (force1.y + force2.y) / mass;

        //acceleration.y -= 9.81;

        // Get the velocity
        velocity.x = integrate(velocity.x, acceleration.x);
        velocity.y = integrate(velocity.y, acceleration.y);

        // Get the position
        position.x = integrate(position.x, velocity.x);
        position.y = integrate(position.y, velocity.y);
    }

    void calculateRotation(Vector force1, Vector force2){
        double alpha1 = -Math.toRadians(90) - theta + force1.direction;
        double alpha2 = -Math.toRadians(90) - theta + force2.direction;

        double moment1 = -Math.cos(alpha1) * force1.abs * 0.5 * width;
        double moment2 = -Math.sin(alpha1) * force1.abs * 0.5 * height;
        double moment3 = Math.cos(alpha2) * force2.abs * 0.5 * width;
        double moment4 = -Math.sin(alpha2) * force2.abs * 0.5 * height;

        double stiffness = -5;
        double damping = -1;

        double moment5 = stiffness * theta + damping * thetadot;

        double totalMoment = moment1 + moment2 + moment3 + moment4 + moment5;
        double thetadotdot = totalMoment / momentOfInertia;

        thetadot = integrate(thetadot, thetadotdot);
        theta = integrate(theta, thetadot);
    }

    void reset() {
        initializeVectors();
    }
}
