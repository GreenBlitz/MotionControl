package org.greenblitz.motion.profiling.curve.spline;

import org.greenblitz.motion.base.State;

/**
 * @author alexey
 */
public class CubicSplineGenerator {

    /**
     * @param start The starting position of the quintic polynomial
     * @param end   The end position of the polynomial
     * @return An ICurve of the polynomial
     * @see CubicSplineGenerator#generateSpline(State, State, double)
     */
    public static PolynomialCurve generateSpline(State start, State end) {
        return generateSpline(start, end, 1);
    }

    /**
     * @param start The starting position of the quintic polynomial
     * @param end   The end position of the polynomial
     * @param t     The "time" of the polynomial. If the polynomial is
     *              it is gamma : [0, t] -> R^2 then this variable is t.
     * @return An ICurve of the polynomial
     */
    public static PolynomialCurve generateSpline(State start, State end, double t) {
        //gets and returns state by global standard but calculates with opposite angles //TODO: fix
        double angS = -start.getAngle();
        double angE = -end.getAngle();

        return new PolynomialCurve(3,
                getParams(start.getX(), end.getX(), Math.sin(angS), Math.sin(angE), t),
                getParams(start.getY(), end.getY(), Math.cos(angS), Math.cos(angE), t), 0, 1, t
        );
    }

    /**
     * See
     * https://matrixcalc.org/en/slu.html#solve-using-Gaussian-elimination%28%7B%7B0,0,0,1,0%7D,%7B1,1,1,1,x%7D,%7B0,0,1,0,d_0%7D,%7B3,2,1,0,d_1%7D%7D%29
     * or this
     * https://matrixcalc.org/en/slu.html#solve-using-Gaussian-elimination%28%7B%7B0,0,0,1,x_0%7D,%7B1,1,1,1,x_1%7D,%7B0,0,1,0,d_0%7D,%7B3,2,1,0,d_1%7D%7D%29
     * or this
     * https://matrixcalc.org/en/slu.html#solve-using-Gaussian-elimination%28%7B%7B0,0,0,1,x_0%7D,%7Bt%5E3,t%5E2,t,1,x_1%7D,%7B0,0,1,0,d_0%7D,%7B3%2At%5E2,2%2At,1,0,d_1%7D%7D%29
     * <p>
     * (the lower the equation, the more recent)
     *
     * @param dStart    the start x
     * @param dEnd      the end x
     * @param dervStart the start first derivative
     * @param dervEnd   the end first derivative
     * @return Coefficients for a third degree polynomial in the form [a, b, c, d]
     * *          such that the polynomial is  a + b*x + c*x^2 + d*x^3
     */
    protected static double[] getParams(double dStart, double dEnd, double dervStart, double dervEnd, double t) {
        double dx = dEnd - dStart;
        double tt = t * t;
        return new double[]{
                dStart, dervStart, (-2 * dervStart * t - dervEnd * t + 3 * dx) / tt, (dervStart * t + dervEnd * t - 2 * dx) / (t * tt)
        };
    }

}
