class Vector {
    double x, y, direction, abs;

    Vector(double x, double y){
        this.x = x;
        this.y = y;
    }

    int [] toPixelIndices(int yBound) {
        int[] vector = {(int) Math.round(100 * x), (int) Math.round(yBound - 100 * y)};
        return vector;
    }

    double getDirection(){
        return Math.atan2(y, x);
    }

    double getAbs() {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }
}
