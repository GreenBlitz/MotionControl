package org.greenblitz.motion.profiling;

import org.greenblitz.motion.base.State;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class ChassisProfiler2DCSVGenerators {

    @Test
    void generate2DProfile(){
        List<State> states = new ArrayList<>();
        states.add(new State(0,0, 0, 0, 0));
        states.add(new State(3, 5, 0, 0, 0));

        MotionProfile2D brofile = ChassisProfiler2D.generateProfile(states, 0.0003, 0.0001, 5,
        Math.PI, 10*Math.PI, 5*10);

        System.out.println(brofile);

    }

}
