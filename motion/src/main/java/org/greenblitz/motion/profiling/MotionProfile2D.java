package org.greenblitz.motion.profiling;

import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Vector2D;

import java.util.function.Function;

public class MotionProfile2D {

    private MotionProfile1D firstProfile, secondProfile;

    public MotionProfile2D(MotionProfile1D firstProfile, MotionProfile1D secondProfile) {
        if (!Point.isFuzzyEqual(firstProfile.getTEnd(), secondProfile.getTEnd(), 1E-3))
            throw new IllegalArgumentException("T end of first end second profile un-equal");
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
     * Removes all segments with time length less then a milisecond.
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
                ", secondProfile=" + secondProfile +
                '}';
    }
}
