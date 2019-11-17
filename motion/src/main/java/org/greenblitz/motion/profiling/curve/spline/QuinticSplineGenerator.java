package org.greenblitz.motion.profiling.curve.spline;

import org.greenblitz.motion.base.State;

/**
 * @author peleg
 */

public class QuinticSplineGenerator {

    /**
     * @param start
     * @param end
     * @return
     */
    public static PolynomialCurve generateSpline(State start, State end){
        return generateSpline(start, end, 1);
    }

    /**
     * Mathematical explanation for the calculation of the first and second derivatives:
     * Let x and y be functions of the parameter b
     *
     * FIRST DERIVATIVE:
     * dy/dx = cot a (when a is the angle with the y axis)
     * Therefore (dy/db)*(db/dx) = cos(a)/sin(a) implies (dy/db)*sin(a) = (dx/db)*cos(a)
     * The values of the first derivatives doesn't matter because x(b) and y(b) aren't defined yet.
     *
     * So we chose these values according to the equation above
     * y' = dy/db = cos(a)
     * x' = dx/db = sin(a)
     *
     * SECOND DERIVATIVE:
     * x'' = (d/db)(dx/db) = (da/db)[(d/da) sin(a)] = (da/dt)*(dt/dx)*(dx/db)*[cos(a)] =
     *  = W*(1/(dx/dt))*(dx/db)*cos(a) = W*(1/(V*sin(a)))*sin(a)*cos(a) = (W/V)*cos(a) = K*cos(a)
     *
     * x'' = K*cos(a)
     * When: t - time, W - angular velocity, V - linear velocity, K - curvature = W/V
     *
     * Similar calculation with y results:
     * y'' = -K*sin(a)
     *
     * @param start
     * @param end
     * @param t
     * @return
     */
    public static PolynomialCurve generateSpline(State start, State end, double t){
        double angS = start.getAngle();
        double angE = end.getAngle();
        double kS = start.getAngularVelocity()/start.getLinearVelocity();
        double kE = end.getAngularVelocity()/end.getLinearVelocity();
        PolynomialCurve ret = new PolynomialCurve(5,
                getParams(start.getX(), end.getX(), Math.sin(angS), Math.sin(angE), kS*Math.cos(angS),kE*Math.cos(angE), t),
                getParams(start.getY(), end.getY(), Math.cos(angS), Math.cos(angE), -kS*Math.sin(angS),-kE*Math.sin(angE), t), 0, 1, t
        );
//        if(Point.subtract(ret.getLocation(1), end).norm() > 0.01){
//            throw new RuntimeException("What");
//        }
        return ret;
    }

    /**
     * See
     *
     * https://matrixcalc.org/en/slu.html#solve-using-Gaussian-elimination%28%7B%7B0,0,0,0,0,1,x_0%7D,%7Bt%5E5,t%5E4,t%5E3,t%5E2,t,1,x_1%7D,%7B0,0,0,0,1,0,d_0%7D,%7B5%2At%5E4,4%2At%5E3,3%2At%5E2,2%2At,1,0,d_1%7D,%7B0,0,0,2,0,0,D_0%7D,%7B20%2At%5E3,12%2At%5E2,6%2At,2,0,0,D_1%7D%7D%29
     *
     * @param dervS
     * @param dervE
     * @return
     */
    protected static double[] getParams(double dStart, double dEnd, double dervS, double dervE, double derv2S,double derv2E, double t){
        double dx = dEnd - dStart;
        double tt = t*t;
        return new double[] {
                dStart, dervS, derv2S/2
                ,(tt*(-3*derv2S + derv2E) - t*(12*dervS + 8*dervE) + 20*dx)/(2*tt*t)
                ,(tt*(3*derv2S - 2*derv2E) + t*(16*dervS + 14*dervE) - 30*dx)/(2*tt*tt)
                ,(tt*(-derv2S + derv2E) - 6*t*(dervS + dervE) + 12*dx)/(2*tt*tt*t)
        };
    }
}
