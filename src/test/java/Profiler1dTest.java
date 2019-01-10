import org.greenblitz.motion.motionprofiling.*;

import java.util.ArrayList;

import org.greenblitz.motion.motionprofiling.exception.ProfilingException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Profiler1dTest {

    static final double EPSILON = 1E-6;

    @Test
    void test1 (){
        ArrayList<ActuatorLocation> points = new ArrayList<>();
        points.add(new ActuatorLocation(0,0));
        points.add(new ActuatorLocation(10,0));
        try{
            MotionProfile p = Profiler1D.generateProfile(points, 1, 1, -1);

            assertEquals(p.getAcceleration(0.5), 1, EPSILON);
            assertEquals(p.getAcceleration(5), 0, EPSILON);
            assertEquals(p.getAcceleration(10.5), -1, EPSILON);

            assertEquals(p.getVelocity(0.5), 0.5, EPSILON);
            assertEquals(p.getVelocity(5), 1, EPSILON);
            assertEquals(p.getVelocity(10.5), 0.5, EPSILON);

            assertEquals(p.getLocation(0.5), 0.125, EPSILON);
            assertEquals(p.getLocation(5), 4.5, EPSILON);
            assertEquals(p.getLocation(10.5), 9.875, EPSILON);

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

            assertEquals(p.getAcceleration(0.25), 4, EPSILON);
            assertEquals(p.getAcceleration(1), 0, EPSILON);
            assertEquals(p.getAcceleration(4), -0.5, EPSILON);

            assertEquals(p.getVelocity(0.25), 1, EPSILON);
            assertEquals(p.getVelocity(1), 2, EPSILON);
            assertEquals(p.getVelocity(4), 1.625, EPSILON);

            assertEquals(p.getLocation(0.25), 0.125, EPSILON);
            assertEquals(p.getLocation(1), 1.5, EPSILON);
            assertEquals(p.getLocation(4), 6+3.625/2*0.75, EPSILON);

        }catch(ProfilingException e){
            System.out.println(e);
            assertEquals("good code", "shit code");
        }
    }

    @Test
    void test3 (){
        ArrayList<ActuatorLocation> points = new ArrayList<>();
        points.add(new ActuatorLocation(0,0));
        points.add(new ActuatorLocation(15, -1));
        points.add(new ActuatorLocation(10,0));
         try{
            MotionProfile p = Profiler1D.generateProfile(points, 2, 4, -0.5);

            assertEquals(p.getAcceleration(0.25), 4, EPSILON);
            assertEquals(p.getAcceleration(1), 0, EPSILON);
            assertEquals(p.getAcceleration(4), -0.5, EPSILON);

            assertEquals(p.getVelocity(0.25), 1, EPSILON);
            assertEquals(p.getVelocity(1), 2, EPSILON);
            assertEquals(p.getVelocity(4), 1.625, EPSILON);

            assertEquals(p.getLocation(0.25), 0.125, EPSILON);
            assertEquals(p.getLocation(1), 1.5, EPSILON);
            assertEquals(p.getLocation(4), 6+3.625/2*0.75, EPSILON);

        }catch(ProfilingException e){
            System.out.println(e);
            assertEquals("good code", "shit code");
        }
    }
}
