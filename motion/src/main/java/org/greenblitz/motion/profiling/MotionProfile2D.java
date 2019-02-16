package org.greenblitz.motion.profiling;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.State;
import org.greenblitz.motion.base.TwoTuple;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

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
     * @return the acceleration at that time
     */
    public TwoTuple<Double, Double> getAcceleration(double t) {
        return new TwoTuple<>(firstProfile.getAcceleration(t), secondProfile.getAcceleration(t));
    }

    /**
     * @param t point in time (in seconds)
     * @return the velocity at that time
     */
    public TwoTuple<Double, Double> getVelocity(double t) {
        return new TwoTuple<>(firstProfile.getVelocity(t), secondProfile.getVelocity(t));
    }

    /**
     * @param t point in time (in seconds)
     * @return the location at that time
     */
    public TwoTuple<Double, Double> getLocation(double t) {
        return new TwoTuple<>(firstProfile.getLocation(t), secondProfile.getLocation(t));
    }

    /**
     * Removes all segments with time length less then a milisecond.
     */
    public void removeBugSegments() {
        firstProfile.removeBugSegments();
        secondProfile.removeBugSegments();
    }


}
