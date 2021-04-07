package org.greenblitz.motion.profiling.kinematics;

import org.greenblitz.motion.base.Vector2D;

/**
 * @author alexey
 */
public class ReverseLocalizerConverter implements IConverter {

    private double wheelDist;
    private boolean regularDirections;

    public ReverseLocalizerConverter(double d) {
        this(d, true);
    }

    public ReverseLocalizerConverter(double d, boolean regularDirections){
        wheelDist = d;
        this.regularDirections = regularDirections;
    }


    @Override
    public Vector2D convert(Vector2D byLinAng) {

        /*
        See:
        https://matrixcalc.org/en/slu.html#solve-using-Cramer%27s-rule%28%7B%7B1/2,1/2,0,0,v%7D,%7B1/d,-1/d,0,0,o%7D%7D%29
         */

        double fasterMotor =  (-wheelDist * byLinAng.getY() + 2 * byLinAng.getX()) / 2.0; //TODO for some reason faster and slower were the wrong way did swap should try and understand cause
        double slowerMotor = (wheelDist * byLinAng.getY() + 2 * byLinAng.getX()) / 2.0;

        if (byLinAng.getY() > 0 == regularDirections) {
            return new Vector2D(slowerMotor, fasterMotor);
        } else {
            return new Vector2D(fasterMotor, slowerMotor);

    }
}
}
