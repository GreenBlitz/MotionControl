package org.greenblitz.motion.profiling.curve.spline;

import org.greenblitz.motion.base.State;

/**
 * @author alexey
 */
public class CubicSplineGenerator {

    /**
     * @param start
     * @param end
     * @return
     */
    public static ThirdDegreePolynomialCurve generateSpline(State start, State end){
        double angS = start.getAngle();
        double angE = end.getAngle();
        return new ThirdDegreePolynomialCurve(
                getParams(start.getX(), end.getX(), Math.sin(angS), Math.sin(angE)),
                getParams(start.getY(), end.getY(), Math.cos(angS), Math.cos(angE))
        );
    }

    /**
     * See
     * https://matrixcalc.org/en/slu.html#solve-using-Gaussian-elimination%28%7B%7B0,0,0,1,0%7D,%7B1,1,1,1,x%7D,%7B0,0,1,0,d_0%7D,%7B3,2,1,0,d_1%7D%7D%29
     * or this
     * https://matrixcalc.org/en/slu.html#solve-using-Gaussian-elimination%28%7B%7B0,0,0,1,x_0%7D,%7B1,1,1,1,x_1%7D,%7B0,0,1,0,d_0%7D,%7B3,2,1,0,d_1%7D%7D%29
     *
     * @param dervStart
     * @param dervEnd
     * @return
     */
    protected static double[] getParams(double dStart, double dEnd, double dervStart, double dervEnd){
        double dx = dEnd - dStart;
        return new double[] {
                dStart, dervStart, -2*dervStart - dervEnd + 3*dx, dervStart + dervEnd - 2*dx
        };
    }

}
