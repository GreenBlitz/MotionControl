package org.greenblitz.motion.profiling;

import org.greenblitz.motion.base.State;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;

public class ReturnProfiler2DTest {

    @Test
    void printTest(){
        State start = new State(2, 5, Math.PI/2, 3, Math.PI);
        State end = new State(8, 8, Math.PI/8, 8, 8*Math.PI);
        ArrayList<State> locations = new ArrayList<State>(2);
        locations.add(start);
        locations.add(end);

        MotionProfile2D profile = ChassisProfiler2D.generateProfile(locations, .001, 2, Math.PI/4,  15, Math.PI*15,
                1, Math.PI, 0, 1.5, 4);
        State currentStart = new State(5, 9, Math.PI, 5, Math.PI*2);
        System.out.println(ReturnProfiler2D.generateProfile(profile, currentStart, profile.quickGetIndex(0.3), profile.quickGetNode(0.3), .001,
                15, Math.PI*15, 1, Math.PI, 0.15, 1.5, 4));



    }
}
