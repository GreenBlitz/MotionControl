package org.greenblitz.motion;

import org.greenblitz.motion.profiling.ActuatorLocation;
import org.greenblitz.motion.profiling.MotionProfile;
import org.greenblitz.motion.profiling.Profiler1D;
import org.greenblitz.motion.profiling.exception.ProfilingException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MotionProfilingTest {

    private static final double MAX_VEL = 1.5;
    private static final double MAX_ACCEL = 115 / 10.0;

    @Test
    void printTest(){
        List<ActuatorLocation> locs = new ArrayList<>();
        locs.add(new ActuatorLocation(0, 0));
        locs.add(new ActuatorLocation(1, 0));
        MotionProfile prof;
        try {
             prof = Profiler1D.generateProfile(
                    locs,
                    MAX_VEL,
                    MAX_ACCEL,
                    -MAX_ACCEL
            );
        } catch (ProfilingException e){
            e.printStackTrace();
            return;
        }
        System.out.println(prof);
        prof.generateCSV("elevatorGraph.csv", 500);
    }

}
