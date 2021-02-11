package org.greenblitz.motion.profiling;

import org.greenblitz.motion.base.State;
import org.greenblitz.motion.profiling.curve.spline.QuinticSplineGenerator;
import org.junit.jupiter.api.Test;

public class CurveTest {


    @Test
    void printCurve(){
        State start = new State(2, 5, Math.PI/2, 3, Math.PI);
        State end = new State(8, 8, Math.PI/8, 8/8, Math.PI/8);
        System.out.println(QuinticSplineGenerator.generateSpline(start, end));
    }
}
