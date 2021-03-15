package org.greenblitz.motion;

import org.greenblitz.motion.base.State;
import org.greenblitz.motion.profiling.curve.spline.PolynomialCurve;
import org.greenblitz.motion.profiling.curve.spline.QuinticSplineGenerator;

public class CurveTesting{
    public static void main(String[] args){
        State s1 = new State(0,0,0.325*Math.PI,1,2);
        State s2 = new State(0,2,0.125*Math.PI,1,5);
        PolynomialCurve curve = QuinticSplineGenerator.generateSpline(s1,s2);
        System.out.println(curve);
    }
}
