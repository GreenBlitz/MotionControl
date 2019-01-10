import org.greenblitz.motion.motionprofiling.ActuatorLocation;
import org.greenblitz.motion.motionprofiling.MotionProfile;
import org.greenblitz.motion.motionprofiling.Profiler1D;
import org.greenblitz.motion.motionprofiling.exception.ProfilingException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class MotionProfilingTest {

    @Test
    void printTest(){
        List<ActuatorLocation> locs = new ArrayList<>();
        locs.add(new ActuatorLocation(0, 0));
        locs.add(new ActuatorLocation(-10, -6));
        MotionProfile prof;
        try {
             prof = Profiler1D.generateProfile(
                    locs,
                    5.1,
                    5,
                    -2
            );
        } catch (ProfilingException e){
            e.printStackTrace();
            return;
        }
        System.out.println(prof.getLocation(prof.getSegments().get(prof.getSegments().size() - 1).getTEnd()));
        System.out.println(prof.getVelocity(prof.getSegments().get(prof.getSegments().size() - 1).getTEnd()));
        System.out.println(prof);
    }

}
