package org.greenblitz.motion.profiling.curve;

import org.greenblitz.motion.base.Point;

/**
 * @author alexey
 */
public abstract class AbstractCurve implements ICurve {

    protected abstract Point getLocationInternal(double u);
    @Deprecated
    protected abstract double getLinearVelocityInternal(double u);
    @Deprecated
    protected abstract double getAngularVelocityInternal(double u);
    protected abstract double getLengthInternal(double u);
    protected abstract double getAngleInternal(double u);
    protected abstract double getCurvatureInternal(double u);

    protected double uStart, uEnd;

    public double clamp(double u){
        return u * (uEnd - uStart) + uStart;
    }

    @Override
    public Point getLocation(double u) {
        return getLocationInternal(clamp(u));
    }

    @Override
    public double getLinearVelocity(double u) {
        return getLinearVelocityInternal(clamp(u));
    }

    @Override
    public double getAngularVelocity(double u) {
        return getAngularVelocityInternal(clamp(u));
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
        return getCurvature(0.5);
    }

    @Override
    public double getCurvature(double u) {
        return getCurvatureInternal(clamp(u));
    }

}
