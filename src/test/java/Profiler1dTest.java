import org.greenblitz.motion.motionprofiling.ActuatorLocation;
import org.greenblitz.motion.motionprofiling.MotionProfile;
import org.greenblitz.motion.motionprofiling.Profiler1D;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Profiler1dTest {

    private static final double EPSILON = 1E-6;

    @Test
    void test1() {
        ArrayList<ActuatorLocation> points = new ArrayList<>();
        points.add(new ActuatorLocation(0, 0));
        points.add(new ActuatorLocation(10, 0));

        var parr = new MotionProfile[1];
        assertDoesNotThrow(() -> {
            parr[0] = Profiler1D.generateProfile(points, 1, 1, -1);
        });
        var p = parr[0];


        assertEquals(p.getAcceleration(0.5), 1, EPSILON);
        assertEquals(p.getAcceleration(5), 0, EPSILON);
        assertEquals(p.getAcceleration(10.5), -1, EPSILON);

        assertEquals(p.getVelocity(0.5), 0.5, EPSILON);
        assertEquals(p.getVelocity(5), 1, EPSILON);
        assertEquals(p.getVelocity(10.5), 0.5, EPSILON);

        assertEquals(p.getLocation(0.5), 0.125, EPSILON);
        assertEquals(p.getLocation(5), 4.5, EPSILON);
        assertEquals(p.getLocation(10.5), 9.875, EPSILON);
    }

    @Test
    void test2() {
        ArrayList<ActuatorLocation> points = new ArrayList<>();
        points.add(new ActuatorLocation(0, 0));
        points.add(new ActuatorLocation(10, 0));
        var parr = new MotionProfile[1];
        assertDoesNotThrow(() -> {
            parr[0] = Profiler1D.generateProfile(points, 2, 4, -0.5);
        });
        var p = parr[0];
        assertEquals(p.getAcceleration(0.25), 4, EPSILON);
        assertEquals(p.getAcceleration(1), 0, EPSILON);
        assertEquals(p.getAcceleration(4), -0.5, EPSILON);

        assertEquals(p.getVelocity(0.25), 1, EPSILON);
        assertEquals(p.getVelocity(1), 2, EPSILON);
        assertEquals(p.getVelocity(4), 1.625, EPSILON);

        assertEquals(p.getLocation(0.25), 0.125, EPSILON);
        assertEquals(p.getLocation(1), 1.5, EPSILON);
        assertEquals(p.getLocation(4), 6 + 3.625 / 2 * 0.75, EPSILON);
    }

    @Test
    void test3() {
        ArrayList<ActuatorLocation> points = new ArrayList<>();
        points.add(new ActuatorLocation(0, 0));
        points.add(new ActuatorLocation(15, -1));
        points.add(new ActuatorLocation(10, 0));
        var parr = new MotionProfile[1];
        assertDoesNotThrow(() -> {
            parr[0] = Profiler1D.generateProfile(points, 2, 4, -0.5);
        });
        var p = parr[0];
        assertEquals(p.getAcceleration(0.25), 4, EPSILON);
        assertEquals(p.getAcceleration(1), 0, EPSILON);
        assertEquals(p.getAcceleration(0.5 + 5.75 + 0.3 * 6), -0.5, EPSILON);
        assertEquals(p.getAcceleration(12.25 + 0.25), -0.5, EPSILON);
        assertEquals(p.getAcceleration(12.25 + 2.5), 0, EPSILON);
        assertEquals(p.getAcceleration(12.25 + 3), 4, EPSILON);

        assertEquals(p.getVelocity(0.25), 1, EPSILON);
        assertEquals(p.getVelocity(1), 2, EPSILON);
        assertEquals(p.getVelocity(0.5 + 5.75 + 0.3 * 6), 0.7 * 2 + 0.3 * (-1), EPSILON);
        assertEquals(p.getVelocity(12.25 + 0.25), (1.75 * (-1) + 0.25 * (-2)) / 2, EPSILON);
        assertEquals(p.getVelocity(12.25 + 2.5), -2, EPSILON);
        assertEquals(p.getVelocity(12.25 + 3), (0.25 * (-2) + 0.25 * 0) * 2, EPSILON);

        assertEquals(p.getLocation(0.25), 0.125, EPSILON);
        assertEquals(p.getLocation(1), 1.5, EPSILON);
        assertEquals(p.getLocation(0.5 + 5.75 + 0.3 * 6), 12 + 2 * 1.8 - 0.5 * 1.8 * 1.8 / 2, EPSILON);
        assertEquals(p.getLocation(12.25 + 0.25), 15 - 0.25 - 0.5 * 0.25 * 0.25 / 2, EPSILON);
        assertEquals(p.getLocation(12.25 + 2.5), 12 - 2 * 0.5, EPSILON);
        assertEquals(p.getLocation(12.25 + 3), 10.5 - 2 * 0.25 + 4 * 0.25 * 0.25 / 2, EPSILON);

    }
}
