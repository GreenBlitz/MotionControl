package org.greenblitz.motion.profiling.curve;

import org.greenblitz.motion.base.Point;

/**
 * Represents a curve. values given to it's function must be between 0 and 1.
 * @author Alexey
 */
public interface ICurve {

    /**
     * Gives the location of the curve in space
     * @param u in the range [0, 1] representing the location on the curve.
     * @return location of the curve at parameter u
     */
    Point getLocation(double u);

    /**
     *
     * @param u in the range [0, 1] representing the location on the curve.
     * @return The velocity element tangent to the given location on the curve.
     */
    double getLinearVelocity(double u);

    /**
     *
     * @param u in the range [0, 1] representing the location on the curve.
     * @return the rotational velocity of the curve at this location.
     */
    double getAngularVelocity(double u);

    /**
     *
     * pre the curve approximates an arc
     * @param u in the range [0, 1] representing the location on the curve.
     * @return the length of the arc up to the given point
     */
    double getLength(double u);

    /**
     *
     * @param u in the range [0, 1] representing the location on the curve.
     * @return the angle of the tangent line to the given point on the curve
     */
    double getAngle(double u);

    /**
     *
     * pre the curve approximates an arc
     * @return The curve of the curve at that point
     */
    double getCurvature();

    /**
     *
     * @param u in the range [0, 1] representing the location on the curve.
     * @return The curve of the curve at that point
     */
    double getCurvature(double u);

    /**
     *
     * @param uStart in the range [0, 1) representing the start location on the curve.
     * @param uEnd in the range (0, 1] representing the end location on the curve. uEnd bigger than uStart.
     * @return A new curve object, where the values at u=0 are the same as u=uStart in this curve and at u=1 the
     * same as u=uEnd on this curve.
     */
    ICurve getSubCurve(double uStart, double uEnd);

}
