package org.greenblitz.motion.profiling;

import org.greenblitz.motion.base.State;
import org.greenblitz.motion.profiling.curve.ICurve;
import org.greenblitz.motion.profiling.curve.spline.PolynomialCurve;
import org.greenblitz.motion.profiling.curve.spline.QuinticSplineGenerator;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;

public class ReturnProfiler2DTest {



    @Test
    void printTest(){
        State start = new State(2, 5, Math.PI/2, 3, Math.PI);
        State end = new State(8, 8, Math.PI/8, 8/8, Math.PI/8);
        ArrayList<State> locations = new ArrayList<State>(2);
        locations.add(start);
        locations.add(end);

        MotionProfile2D profile = ChassisProfiler2D.generateProfile(locations, .02, 2, Math.PI/4,  5, Math.PI,
                1, Math.PI, 0, 1.5, 4);
        State currentStart = new State(5, 6, Math.PI, 5, Math.PI*2);
        System.out.println(ReturnProfiler2D.generateProfile(profile, currentStart, profile.quickGetIndex(4), profile.quickGetNode(4), .02,
                15, Math.PI*15, 1, Math.PI, 0.15, 1.5, 4));




    }

}
