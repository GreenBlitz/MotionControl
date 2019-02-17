package org.greenblitz.motion.profiling.curve;

import org.greenblitz.motion.base.Point;

/**
 * Represents a curve. values given to it's function must be between 0 and 1.
 */
public interface ICurve {

    Point getLocation(double u);

    double getLinearVelocity(double u);
    double getAngularVelocity(double u);

    double getLength(double u);
    double getAngle(double u);

    double getCurvature(double u);

    ICurve getSubCurve(double uStart, double uEnd);

}
