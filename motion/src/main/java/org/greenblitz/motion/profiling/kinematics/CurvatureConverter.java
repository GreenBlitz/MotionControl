package org.greenblitz.motion.profiling.kinematics;

import org.greenblitz.motion.base.Vector2D;

/**
 * @author alexey
 *
 * This converts using the curavature, and thus avoiding the angle formula.
 * We generally get that:
 *
 * alpha*R' = v1, alpha*(R' + d) = v2
 * where v2 is the faster motor.
 * thus:
 * ratio = v2/v1 = (R' + d)/(R')
 * and taking R as the ratio that matches the curavature we get:
 * ratio = (R + 0.5d)/(R - 0.5d)
 *
 * Exceptions:
 *
 * when the angular velocity is 0, the radius is "infinite" and this this algo can't be used.
 *
 * When the linear velocity is 0, the radius is 0, then we get a ration of -1. because of this the equation breaks
 * as you get the expression 0/0.
 *
 * When the slower motor moves at non positive speed (i.e. radius <= 0.5d) the ratio between wheel velocities is
 * not defined (negative ratio is meaningless). thus, this algo can't be used.
 *
 * Whenever an expetion occures, we opt to using ReverseLocalizerConverter which should be accurate in all of those
 * simple private cases.
 *
 */
public class CurvatureConverter implements IConverter {

    private double halfWheelDist;
    private boolean regularDirections;
    private ReverseLocalizerConverter emergencyConverter;

    public CurvatureConverter(double d){
        this(d, true);
    }

    public CurvatureConverter(double d, boolean regularDirections){
        halfWheelDist = d/2.0;
        emergencyConverter = new ReverseLocalizerConverter(d);
        this.regularDirections = regularDirections;
    }

    @Override
    public Vector2D convert(Vector2D byLinAng) {

        if (byLinAng.getY() == 0 || byLinAng.getX() == 0){ // The second check is here so later we have (ration != -1)
            return emergencyConverter.convert(byLinAng);
        }

        double radius = Math.abs(byLinAng.getX() / byLinAng.getY());

        if (radius - halfWheelDist <= 0){ // This conversion only works when both motors go forwards!
            return emergencyConverter.convert(byLinAng);
        }

        double ratio = (radius + halfWheelDist) / (radius - halfWheelDist);

        double slowerMotor = 2*byLinAng.getX() / (ratio + 1);
        double fasterMotor = ratio * slowerMotor;

        if (byLinAng.getY() > 0 == regularDirections){
            return new Vector2D(slowerMotor, fasterMotor);
        } else {
            return new Vector2D(fasterMotor, slowerMotor);
        }

    }
}
