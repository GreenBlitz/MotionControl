package org.greenblitz.motion.profiling.kinematics;

import org.greenblitz.motion.base.Vector2D;

/**
 * @author alexey
 */
public class ReverseLocalizerConverter implements IConverter {

    private double wheelDist;

    public ReverseLocalizerConverter(double d) {
        wheelDist = d;
    }

    @Override
    public Vector2D convert(Vector2D byLinAng) {

        /*
        See:
        https://matrixcalc.org/en/slu.html#solve-using-Cramer%27s-rule%28%7B%7B1/2,1/2,0,0,v%7D,%7B1/d,-1/d,0,0,o%7D%7D%29
         */

        double leftMotorV = (wheelDist * byLinAng.getY() + 2 * byLinAng.getX()) / 2.0;
        double rightMotorV = (-wheelDist * byLinAng.getY() + 2 * byLinAng.getX()) / 2.0;

        return new Vector2D(leftMotorV, rightMotorV);
    }
}
