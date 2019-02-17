package org.greenblitz.motion.base;

/**
 * Represents a curve. values given to it's function must be between 0 and 1.
 */
public interface ICurve {

    double getX(double u);
    double getY(double u);

    double getLinearVelocity(double u);
    double getAngularVelocity(double u);

    double getCurvature(double u);

    ICurve getSubCurve(double uStart, double uEnd);

}
