import java.awt.*;

class Drawable {

    int numberOfElements;
    Vector[] points;

    Drawable(){
        this.points = new Vector[numberOfElements];
    }

    void draw(Graphics2D g2d){

        for (int i = 0; i < this.numberOfElements - 1; i++){
            int[] coordinates1 = this.points[i].toPixelIndices(Simulation.yBound);
            int[] coordinates2 = this.points[i+1].toPixelIndices(Simulation.yBound);

            g2d.drawLine(coordinates1[0] , coordinates1[1], coordinates2[0] , coordinates2[1]);
        }
    }
}
