import org.greenblitz.motion.AdaptivePurePursuitController;
import org.greenblitz.motion.base.*;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class APPCTest {

    static final double EPSILON = 1E-6;

    @Test
    void driveValuesTest1(){
        double[] values = AdaptivePurePursuitController.driveValuesTo(new Position(-1,1,Math.PI/2), new Point(1,1), 1);
        assertEquals(values[0], 1, EPSILON);
        assertEquals(values[1], 1, EPSILON);
    }

    @Test
    void driveValuesTest2(){
        double[] values = AdaptivePurePursuitController.driveValuesTo(new Position(3,-1,-Math.PI/2), new Point(1,-3), 1);
        assertEquals(values[0], 1, EPSILON);
        assertEquals(values[1], 0.6, EPSILON);
    }

    @Test
    void driveValuesTest3(){
        double[] values = AdaptivePurePursuitController.driveValuesTo(new Position(4,20,Math.PI), new Point(0.5,20-(7*Math.sqrt(3)/2)), 3);
        assertEquals(values[0], 5.5/8.5, EPSILON);
        assertEquals(values[1], 1, EPSILON);
    }
}
