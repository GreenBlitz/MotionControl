import org.greenblitz.motion.motionprofiling.ActuatorLocation;
import org.greenblitz.motion.motionprofiling.MotionProfile;
import org.greenblitz.motion.motionprofiling.OneDProfiler;
import org.greenblitz.motion.motionprofiling.PathfinderException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class MotionProfilingTest {

    @Test
    void printTest(){
        List<ActuatorLocation> locs = new ArrayList<>();
        locs.add(new ActuatorLocation(0, 0));
        locs.add(new ActuatorLocation(10, 8));
        MotionProfile prof;
        try {
             prof = OneDProfiler.generateProfile(
                    locs,
                    10,
                    2,
                    -2
            );
        } catch (PathfinderException e){
            e.printStackTrace();
            return;
        }
        System.out.println(prof.getLocation(prof.getSegments().get(1).getTEnd()));
        System.out.println(prof);
    }

}
