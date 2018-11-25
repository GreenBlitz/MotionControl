import org.greenblitz.motion.Localizer;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class LocalizerTest {


    @Test
    void calculateDiffTest(){
        double[] diff = Localizer.calculateMovement(3, 3, 5);
        assertTrue(Arrays.equals(diff, new double[]{0,3,0}));

        diff = Localizer.calculateMovement(3, -3, 5);
        assertTrue(Arrays.equals(diff, new double[]{-0.0,0,1.2}));

        diff = Localizer.calculateMovement(7*(Math.PI/2), 0, 7);
        assertTrue(Arrays.equals(diff, new double[]{3.5,3.5,Math.PI/2}));

        diff = Localizer.calculateMovement(0, 9*(Math.PI/6), 9);
        assertTrue(Arrays.equals(diff, new double[]{9*(1-(Math.sqrt(3)/2)),9*0.-Math.PI/6}));

        diff = Localizer.calculateMovement((3.7-2.5)*(-Math.PI/3), 2.5*(Math.PI/3), 3.7);
        assertTrue(Arrays.equals(diff, new double[]{(2.5-(3.7/2))*0.5, (2.5-(3.7/2))*(1-(Math.sqrt(3)/2)), -Math.PI/3}));
    }

}
