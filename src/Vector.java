class Vector {
    double x, y, direction, abs;
    int scale = Simulation.DISTANCE_SCALE;

    Vector(double x, double y){
        this.x = x;
        this.y = y;
    }

    int [] toPixelIndices(int yBound) {
        int[] vector = {(int) Math.round(scale * x), (int) Math.round(yBound - scale * y)};
        return vector;
    }

    double getDirection(){
        return Math.atan2(y, x);
    }

    double getAbs() {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }
}
