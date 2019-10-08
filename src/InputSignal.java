import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class InputSignal extends Drawable{
    static private InputType[] inputTypes = {InputType.speed_bump, InputType.impulse, InputType.step, InputType.ramp, InputType.block, InputType.sinusoid,InputType.none};
    JComboBox<InputType> inputSelector = new JComboBox<>(inputTypes);
    private int index1, index2;
    private double spatialResolution, nominalHeight, length;
    Vector attachPoint1, attachPoint2;
    private InputType inputType;
    private int count = 0;

    InputSignal(int numberOfElements, int length, double nominalHeight, int index1, int index2, InputType inputType){
        super.numberOfElements = numberOfElements;
        this.length = length;
        this.spatialResolution = (double) length / numberOfElements;
        this.points = new Vector[numberOfElements];
        this.inputType = inputType;
        this.nominalHeight = nominalHeight;

        initialize();

        // The points where the springs are attached
        this.index1 = index1;
        this.index2 = index2;

        this.attachPoint1 = new Vector(points[index1].x, points[index1].y);
        this.attachPoint2 = new Vector(points[index2].x, points[index2].y);

        inputSelector.setVisible(true);
    }

    void selectInputSignal() {
        inputSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputType = (InputType) inputSelector.getSelectedItem();
                initialize();
            }
        });
    }

    void update(){
        if (inputType == InputType.ramp){
            double newHeight = points[2].y + 0.002;
            points[0].y = newHeight;
            points[1].y = newHeight;
        } else if (inputType == InputType.sinusoid){
            double frequency = 1; // cycle/frameWidth
            double amplitude = 0.5; // m
            double newHeight = nominalHeight + amplitude * Math.sin(2 * Math.PI * frequency * (count / (100 * length)));
            points[0].y = newHeight;
            points[1].y = newHeight;
            count++;
        }

        for (int i = numberOfElements - 1; i > 1; i--){
            points[i].y = points[i-1].y;

            // Update the height of the attachmentDown points
            if (i == index1){
                attachPoint1.y = points[i].y;
            } else if (i == index2){
                attachPoint2.y = points[i].y;
            }
        }
    }

    private void initialize() {
        // Initialize points
        for (int i = 0; i < numberOfElements; i++){
            points[i] = new Vector(i * spatialResolution, nominalHeight);
        }

        // Make the signal
        int begin = 5; // index
        int width = 250;
        double height = 0.8;
        if (inputType == InputType.impulse){
            points[begin].y = 2.5;
        } else if (inputType == InputType.step) {
            for (int i = 0; i < begin; i++) {
                points[i].y = nominalHeight + height;
            }
        } else if (inputType == InputType.block){
            for (int i = begin; i < width; i++){
                points[i].y = nominalHeight + height;
            }
        } else if(inputType == InputType.sinusoid) {
            count = 0;
        } else if (inputType == InputType.speed_bump){
            int slopeWidth = 100;
            double slope = height/slopeWidth;
            for (int i = begin; i < width; i++){
                if (i < begin + slopeWidth) {
                    points[i].y = nominalHeight + (i - (begin - 1)) * slope;
                } else if (i < (width - slopeWidth)){
                    points[i].y = nominalHeight + height;
                } else {
                    points[i].y = nominalHeight + height - (i - (width - slopeWidth - 1)) * slope;
                }
            }
        }
    }

    public void reset(){
        initialize();
    }
}
