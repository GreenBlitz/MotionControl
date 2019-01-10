import org.greenblitz.motion.motionprofiling.*;

import java.util.ArrayList;

import org.greenblitz.motion.motionprofiling.exception.ProfilingException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Profiler1dTest {

    @Test
    void test1 (){
        ArrayList<ActuatorLocation> points = new ArrayList<>();
        points.add(new ActuatorLocation(0,0));
        points.add(new ActuatorLocation(10,0));
        try{
            MotionProfile p = Profiler1D.generateProfile(points, 1, 1, -1);
            assertEquals(p.getAcceleration(0.5), 1);
            assertEquals(p.getAcceleration(5), 0);
            assertEquals(p.getAcceleration(10.5), -1);

            assertEquals(p.getVelocity(0.5), 0.5);
            assertEquals(p.getVelocity(5), 1);
            assertEquals(p.getVelocity(10.5), 0.5);

            assertEquals(p.getLocation(0.5), 0.125);
            assertEquals(p.getLocation(5), 4.5);
            assertEquals(p.getLocation(10.5), 9.875);
        }catch(ProfilingException e){
            System.out.println(e);
            assertEquals("good code", "shit code");
        }
    }

    @Test
    void test2 (){
        ArrayList<ActuatorLocation> points = new ArrayList<>();
        points.add(new ActuatorLocation(0,0));
        points.add(new ActuatorLocation(10,0));
        try{
            MotionProfile p = Profiler1D.generateProfile(points, 2, 4, -0.5);
            assertEquals(p.getAcceleration(0.25), 4);
            assertEquals(p.getAcceleration(1), 0);
            assertEquals(p.getAcceleration(10.5), -1);

            assertEquals(p.getVelocity(0.5), 0.5);
            assertEquals(p.getVelocity(5), 1);
            assertEquals(p.getVelocity(10.5), 0.5);

            assertEquals(p.getLocation(0.5), 0.125);
            assertEquals(p.getLocation(5), 4.5);
            assertEquals(p.getLocation(10.5), 9.875);
        }catch(ProfilingException e){
            System.out.println(e);
            assertEquals("good code", "shit code");
        }
    }
}
