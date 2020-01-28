package org.greenblitz.motion.profiling.curve;

import org.greenblitz.motion.base.Point;

/**
 * Represents a curve. values given to it's function must be between 0 and 1.
 * The curve is on the form:<br>
 * <math>\gamma (t) : [0, 1] \to \mathbb{R}^2</math>
 * <br><br>
 * Helpful pages:<br>
 * https://en.wikipedia.org/wiki/Curve<br>
 * https://en.wikipedia.org/wiki/Curvature<br>
 * https://en.wikipedia.org/wiki/Parametric_equation<br>
 * https://math.stackexchange.com/questions/1697588/what-is-the-difference-between-a-function-and-a-curve<br>
 *
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
     * @return the length of the arc up to the given point
     */
    double getLength(double u);

    /**
     * @param u in the range [0, 1] representing the location on the curve.
     * @return the angle of the tangent line to the given point on the curve
     */
    double getAngle(double u);

    /**
     * Some curvature value representing the general curvature of the curve. Could be a sample on an
     * average.
     *
     * See:
     * https://en.wikipedia.org/wiki/Curvature
     *
     * @return The curve of the curve at that point
     */
    double getCurvature();

    /**
     *
     * See:
     * https://en.wikipedia.org/wiki/Curvature
     *
     * @param u in the range [0, 1] representing the location on the curve.
     * @return The curvature of the curve at that point
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
