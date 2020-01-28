package org.greenblitz.motion.profiling.curve;

import org.greenblitz.motion.base.Point;

/**
 *
 * Provides a general framework in order to easily implement getSubCurve.
 * Just create a new curve, with the same params, and pass new uStart and uEnd to fit.
 * In order to allow scaling then you might need to do something similar to PolynomialCurve.
 * @author alexey
 */
public abstract class AbstractCurve implements ICurve {

    /**
     * Don't call non internal functions from internal functions, it would ruin things.
     * @see ICurve#getLocation(double)
     * @param u
     * @return
     */
    protected abstract Point getLocationInternal(double u);
    /**
     * Don't call non internal functions from internal functions, it would ruin things.
     * @see ICurve#getLength(double)
     * @param u
     * @return
     */
    protected abstract double getLengthInternal(double u);
    /**
     * Don't call non internal functions from internal functions, it would ruin things.
     * @see ICurve#getAngle(double)
     * @param u
     * @return
     */
    protected abstract double getAngleInternal(double u);
    /**
     * Don't call non internal functions from internal functions, it would ruin things.
     * @see ICurve#getCurvature(double)
     * @param u
     * @return
     */
    protected abstract double getCurvatureInternal(double u);

    protected double uStart, uEnd;

    /**
     *
     * @param u number in [0, 1]
     * @return the equiv number in [uStart, uEnd]
     */
    public double clamp(double u){
        return u * (uEnd - uStart) + uStart;
    }

    @Override
    public Point getLocation(double u) {
        return getLocationInternal(clamp(u));
    }

    @Override
    public double getLength(double u) {
        return getLengthInternal(clamp(u));
    }

    @Override
    public double getAngle(double u) {
        return getAngleInternal(clamp(u));
    }

    @Override
    public double getCurvature() {
        return (getCurvature(0) + getCurvature(0.5) + getCurvature(1)) / 3.0;
    }

    @Override
    public double getCurvature(double u) {
        return getCurvatureInternal(clamp(u));
    }

}
