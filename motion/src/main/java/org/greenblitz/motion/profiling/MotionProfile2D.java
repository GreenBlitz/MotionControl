package org.greenblitz.motion.profiling;

import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Position;
import org.greenblitz.motion.base.Vector2D;

/**
 * @author Alexey
 */
public class MotionProfile2D {

    private MotionProfile1D firstProfile, secondProfile;

    /**
     * This is package protected on purpose.
     *
     * @param firstProfile
     * @param secondProfile
     */
    MotionProfile2D(MotionProfile1D firstProfile, MotionProfile1D secondProfile) {
        if (!Point.isFuzzyEqual(firstProfile.getTEnd(), secondProfile.getTEnd(), 1E-3))
            throw new IllegalArgumentException("T end of first and second profile un-equal");
        this.firstProfile = firstProfile;
        this.secondProfile = secondProfile;
    }

    /**
     * @return The time in which the profile finishes
     */
    public double getTEnd() {
        return firstProfile.getTEnd();
    }

    /**
     * @param t point in time (in seconds)
     * @return whether or not the profile is finished by that time
     */
    public boolean isOver(double t) {
        return t >= getTEnd();
    }

    /**
     * @param t point in time (in seconds)
     * @return the acceleration vector (first acc, second acc) at that time
     */
    public Vector2D getAcceleration(double t) {
        return new Vector2D(firstProfile.getAcceleration(t), secondProfile.getAcceleration(t));
    }

    /**
     * @param t point in time (in seconds)
     * @return the velocity vector (first vel, second vel) at that time
     */
    public Vector2D getVelocity(double t) {
        return new Vector2D(firstProfile.getVelocity(t), secondProfile.getVelocity(t));
    }

    /**
     * @param t point in time (in seconds)
     * @return the location vector (first loc, second loc) at that time
     */
    public Vector2D getLocation(double t) {
        return new Vector2D(firstProfile.getLocation(t), secondProfile.getLocation(t));
    }

    /**
     * For testing purposes only! Don't use otherwise
     * Package protected in purpose.
     *
     * @param t
     * @param epsilon
     * @return
     */
    Position getActualLocation(double t, double epsilon) {
        return getActualLocation(t, new Position(0, 0, 0), 0, epsilon);
    }

    /**
     * For testing purposes only! Don't use otherwise
     * Package protected in purpose.
     *
     * @param t
     * @param prev
     * @param prevT
     * @param epsilon
     * @return
     */
    Position getActualLocation(double t, Position prev, double prevT, double epsilon) {
        if (prevT > t)
            throw new UnsupportedOperationException();
        Position ret = prev;
        final double dt = epsilon;
        for (double t2 = prevT; t2 < t; t2 += dt) {
            ret = ret.moveBy((firstProfile.getVelocity(t) + secondProfile.getVelocity(t))*0.5,
                    (secondProfile.getVelocity(t) - firstProfile.getVelocity(t))/0.55, dt);
        }
        return ret;
    }

    /**
     * Removes all segments with time length less then a milisecond.
     *
     * @see MotionProfile1D#removeBugSegments()
     */
    public void removeBugSegments() {
        firstProfile.removeBugSegments();
        secondProfile.removeBugSegments();
    }

    @Override
    public String toString() {
        return "MotionProfile2D{" +
                "firstProfile=" + firstProfile +
                "\n, secondProfile=" + secondProfile +
                '}';
    }
}
