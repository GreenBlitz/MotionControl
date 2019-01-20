package org.greenblitz.motion;

import org.greenblitz.motion.profiling.ActuatorLocation;
import org.greenblitz.motion.profiling.MotionProfile;
import org.greenblitz.motion.profiling.Profiler1D;
import org.greenblitz.motion.profiling.exception.ProfilingException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class MotionProfilingTest {

    @Test
    void printTest(){
        List<ActuatorLocation> locs = new ArrayList<>();
        locs.add(new ActuatorLocation(0, 0));
        locs.add(new ActuatorLocation(-10, -6));
        locs.add(new ActuatorLocation(2, 3));
        locs.add(new ActuatorLocation(6, 6));
        locs.add(new ActuatorLocation(15, 10));
        MotionProfile prof;
        try {
             prof = Profiler1D.generateProfile(
                    locs,
                    12,
                    5,
                    -2
            );
        } catch (ProfilingException e){
            e.printStackTrace();
            return;
        }

        System.out.println(prof.generateCSV("./profile_graphs/profile.csv", 100));
    }

}
