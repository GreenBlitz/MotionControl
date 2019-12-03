package org.greenblitz.motion.profiling.curve.spline;

import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.State;

import java.util.Stack;

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
        double kS, kE;
        if (start.getLinearVelocity() == 0){
            kS = start.getAngularVelocity() / 0.00001;
        } else {
            kS = start.getAngularVelocity() / start.getLinearVelocity();
        }
        if (end.getLinearVelocity() == 0){
            kE = end.getAngularVelocity() / 0.00001;
        } else {
            kE = end.getAngularVelocity() / end.getLinearVelocity();
        }
        PolynomialCurve ret = new PolynomialCurve(5,
                getParams(start.getX(), end.getX(), Math.sin(angS), Math.sin(angE), kS*Math.cos(angS),kE*Math.cos(angE), t),
                getParams(start.getY(), end.getY(), Math.cos(angS), Math.cos(angE), -kS*Math.sin(angS),-kE*Math.sin(angE), t), 0, 1, t
        );
//        if(Point.subtract(ret.getLocation(1), end).norm() > 0.01){
//            throw new RuntimeException("What");
//        }
        return ret;
    }

    public static PolynomialCurve generateSplineDervApprox(State start, State end, State bStart, State aftEnd, double t){
        double angS = start.getAngle();
        double angE = end.getAngle();
        double dx2s = (Math.sin(bStart.getAngle()) - Math.sin(angE)) / Point.dist(end, bStart);
        double dx2e = (Math.sin(angS) - Math.sin(aftEnd.getAngle())) / Point.dist(start, aftEnd);
        double dy2s = (Math.cos(bStart.getAngle()) - Math.cos(angE)) / Point.dist(end, bStart);
        double dy2e = (Math.cos(angS) - Math.cos(aftEnd.getAngle())) / Point.dist(start, aftEnd);

        PolynomialCurve ret = new PolynomialCurve(5,
                getParams(start.getX(), end.getX(), Math.sin(angS), Math.sin(angE), dx2s, dx2e, t),
                getParams(start.getY(), end.getY(), Math.cos(angS), Math.cos(angE), dy2s, dy2e, t), 0, 1, t
        );
//        if(Point.subtract(ret.getLocation(1), end).norm() > 0.01){
//            throw new RuntimeException("What");
//        }
        return ret;
    }

    public static PolynomialCurve generateSplineForStartOrEnd(State start, State end, State other, double t, boolean forStart){
        double angS, angE, dx2s, dx2e, dy2s, dy2e;
        if (forStart) {
            angS = start.getAngle();
            angE = end.getAngle();
            dx2s = 0;
            dx2e = (Math.sin(angS) - Math.sin(other.getAngle())) / Point.dist(start, other);
            dy2s = 0;
            dy2e = (Math.cos(angS) - Math.cos(other.getAngle())) / Point.dist(start, other);
        } else {
            angS = start.getAngle();
            angE = end.getAngle();
            dx2s = (Math.sin(other.getAngle()) - Math.sin(angE)) / Point.dist(end, other);
            dx2e = 0;
            dy2s = (Math.cos(other.getAngle()) - Math.cos(angE)) / Point.dist(end, other);
            dy2e = 0;
        }

        PolynomialCurve ret = new PolynomialCurve(5,
                getParams(start.getX(), end.getX(), Math.sin(angS), Math.sin(angE), dx2s, dx2e, t),
                getParams(start.getY(), end.getY(), Math.cos(angS), Math.cos(angE), dy2s, dy2e, t), 0, 1, t
        );
//        if(Point.subtract(ret.getLocation(1), end).norm() > 0.01){
//            throw new RuntimeException("What");
//        }
        return ret;
    }

    public static PolynomialCurve generateForStartAndEnd(State start, State end, double t){
        double angS = start.getAngle();
        double angE = end.getAngle();
        return new PolynomialCurve(5,
                getParams(start.getX(), end.getX(), Math.sin(angS), Math.sin(angE), 0, 0, t),
                getParams(start.getY(), end.getY(), Math.cos(angS), Math.cos(angE), 0, 0, t), 0, 1, t
        );
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

//    protected static double[] getParamsCustom(double x0, double x1, double d0, double d1, double D0,double D1, double u, double t){
//        double v = 2.0 * Math.pow(t, 3) - 6.0 * Math.pow(t, 2) * u + 6.0 * t * Math.pow(u, 2) - 2.0 * Math.pow(u, 3);
//        double v1 = 1.0 * Math.pow(t, 2) - 2.0 * t * u + 1.0 * Math.pow(u, 2);
//        double v2 = 2.0 * Math.pow(t, 4) - 8.0 * Math.pow(t, 3) * u + 12.0 * Math.pow(t, 2) * Math.pow(u, 2) - 8.0 * t * Math.pow(u, 3) + 2.0 * Math.pow(u, 4);
//        return new double[] {
//                (1.0*D0*Math.pow(t, 5)*Math.pow(u, 2) - 2.0*D0*Math.pow(t, 4)*Math.pow(u, 3) + 1.0*D0*Math.pow(t, 3)*Math.pow(u, 4) - 1.0*D1*Math.pow(t, 4)*Math.pow(u, 3) + 2.0*D1*Math.pow(t, 3)*Math.pow(u, 4) - 1.0*D1*Math.pow(t, 2)*Math.pow(u, 5) - 2.0*d0*Math.pow(t, 5)*u + 10.0*d0*Math.pow(t, 4)*Math.pow(u, 2) - 8.0*d0*Math.pow(t, 3)*Math.pow(u, 3) + 8.0*d1*Math.pow(t, 3)*Math.pow(u, 3) - 10.0*d1*Math.pow(t, 2)*Math.pow(u, 4) + 2.0*d1*t*Math.pow(u, 5) + 2.0*Math.pow(t, 5)*x0 - 10.0*Math.pow(t, 4)*u*x0 + 20.0*Math.pow(t, 3)*Math.pow(u, 2)*x0 - 20.0*Math.pow(t, 2)*Math.pow(u, 3)*x1 + 10.0*t*Math.pow(u, 4)*x1 - 2.0*Math.pow(u, 5)*x1)/(v1 * v)
//                ,
//                (-4.0*D0*Math.pow(t, 5)*u + 2.0*D0*Math.pow(t, 4)*Math.pow(u, 2) + 8.0*D0*Math.pow(t, 3)*Math.pow(u, 3) - 6.0*D0*Math.pow(t, 2)*Math.pow(u, 4) + 6.0*D1*Math.pow(t, 4)*Math.pow(u, 2) - 8.0*D1*Math.pow(t, 3)*Math.pow(u, 3) - 2.0*D1*Math.pow(t, 2)*Math.pow(u, 4) + 4.0*D1*t*Math.pow(u, 5) + 4.0*d0*Math.pow(t, 5) - 20.0*d0*Math.pow(t, 4)*u - 32.0*d0*Math.pow(t, 3)*Math.pow(u, 2) + 48.0*d0*Math.pow(t, 2)*Math.pow(u, 3) - 48.0*d1*Math.pow(t, 3)*Math.pow(u, 2) + 32.0*d1*Math.pow(t, 2)*Math.pow(u, 3) + 20.0*d1*t*Math.pow(u, 4) - 4.0*d1*Math.pow(u, 5) - 120.0*Math.pow(t, 2)*Math.pow(u, 2)*x0 + 120.0*Math.pow(t, 2)*Math.pow(u, 2)*x1)/((2.0*Math.pow(t, 2) - 4.0*t*u + 2.0*Math.pow(u, 2))* v),
//                1.0*(2.0*D0*Math.pow(t, 5) + 8.0*D0*Math.pow(t, 4)*u - 16.0*D0*Math.pow(t, 3)*Math.pow(u, 2) + 6.0*D0*t*Math.pow(u, 4) - 6.0*D1*Math.pow(t, 4)*u + 16.0*D1*Math.pow(t, 2)*Math.pow(u, 3) - 8.0*D1*t*Math.pow(u, 4) - 2.0*D1*Math.pow(u, 5) + 72.0*d0*Math.pow(t, 3)*u - 24.0*d0*Math.pow(t, 2)*Math.pow(u, 2) - 48.0*d0*t*Math.pow(u, 3) + 48.0*d1*Math.pow(t, 3)*u + 24.0*d1*Math.pow(t, 2)*Math.pow(u, 2) - 72.0*d1*t*Math.pow(u, 3) + 120.0*Math.pow(t, 2)*u*x0 - 120.0*Math.pow(t, 2)*u*x1 + 120.0*t*Math.pow(u, 2)*x0 - 120.0*t*Math.pow(u, 2)*x1)/(v1 *(4.0*Math.pow(t, 3) - 12.0*Math.pow(t, 2)*u + 12.0*t*Math.pow(u, 2) - 4.0*Math.pow(u, 3)))
//                ,(-3.0*D0*Math.pow(t, 4) + 8.0*D0*Math.pow(t, 2)*Math.pow(u, 2) - 4.0*D0*t*Math.pow(u, 3) - 1.0*D0*Math.pow(u, 4) + 1.0*D1*Math.pow(t, 4) + 4.0*D1*Math.pow(t, 3)*u - 8.0*D1*Math.pow(t, 2)*Math.pow(u, 2) + 3.0*D1*Math.pow(u, 4) - 12.0*d0*Math.pow(t, 3) - 28.0*d0*Math.pow(t, 2)*u + 32.0*d0*t*Math.pow(u, 2) + 8.0*d0*Math.pow(u, 3) - 8.0*d1*Math.pow(t, 3) - 32.0*d1*Math.pow(t, 2)*u + 28.0*d1*t*Math.pow(u, 2) + 12.0*d1*Math.pow(u, 3) - 20.0*Math.pow(t, 2)*x0 + 20.0*Math.pow(t, 2)*x1 - 80.0*t*u*x0 + 80.0*t*u*x1 - 20.0*Math.pow(u, 2)*x0 + 20.0*Math.pow(u, 2)*x1)/((t - u)* v2)
//
//                ,(3.0*D0*Math.pow(t, 3) - 4.0*D0*Math.pow(t, 2)*u - 1.0*D0*t*Math.pow(u, 2) + 2.0*D0*Math.pow(u, 3) - 2.0*D1*Math.pow(t, 3) + 1.0*D1*Math.pow(t, 2)*u + 4.0*D1*t*Math.pow(u, 2) - 3.0*D1*Math.pow(u, 3) + 16.0*d0*Math.pow(t, 2) - 2.0*d0*t*u - 14.0*d0*Math.pow(u, 2) + 14.0*d1*Math.pow(t, 2) + 2.0*d1*t*u - 16.0*d1*Math.pow(u, 2) + 30.0*t*x0 - 30.0*t*x1 + 30.0*u*x0 - 30.0*u*x1)/((1.0*t - u)* v2)
//
//                ,(-2.0*D0*Math.pow(t, 2) + 4.0*D0*t*u - 2.0*D0*Math.pow(u, 2) + 2.0*D1*Math.pow(t, 2) - 4.0*D1*t*u + 2.0*D1*Math.pow(u, 2) - 12.0*d0*t + 12.0*d0*u - 12.0*d1*t + 12.0*d1*u - 24.0*x0 + 24.0*x1)/(4.0*Math.pow(t, 5) - 20.0*Math.pow(t, 4)*u + 40.0*Math.pow(t, 3)*Math.pow(u, 2) - 40.0*Math.pow(t, 2)*Math.pow(u, 3) + 20.0*t*Math.pow(u, 4) - 4.0*Math.pow(u, 5))
//        };
//    }
}
