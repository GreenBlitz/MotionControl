package org.greenblitz.motion.profiling;

import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.State;
import org.greenblitz.motion.profiling.curve.ICurve;
import org.greenblitz.motion.profiling.curve.spline.QuinticSplineGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexey
 * @author Udi
 */
public class ChassisProfiler2D {

    public static final int SMOOTHING_TAIL_SIZE = 200;

    public static MotionProfile2D generateProfile(List<State> locations, double jump, double maxLinearVel,
                                                  double maxAngularVel, double maxLinearAcc, double maxAngularAcc) {
        return generateProfile(locations, jump, maxLinearVel, maxAngularVel, maxLinearAcc, maxAngularAcc, 0);
    }

    public static MotionProfile2D generateProfile(List<State> locations, double jump, ProfilingData data, double tStart) {
        return generateProfile(locations, jump, 0, 0, data.getMaxLinearVelocity(), data.getMaxAngularVelocity(),
                data.getMaxLinearAccel(), data.getMaxAngularAccel(), tStart, 1.0, SMOOTHING_TAIL_SIZE);
    }

    public static MotionProfile2D generateProfile(List<State> locations, double jump, double maxLinearVel,
                                                  double maxAngularVel, double maxLinearAcc, double maxAngularAcc, double tStart) {
        return generateProfile(locations, jump, 0, 0, maxLinearVel, maxAngularVel, maxLinearAcc, maxAngularAcc, tStart, 1.0, SMOOTHING_TAIL_SIZE);
    }

    public static MotionProfile2D generateProfile(List<State> locations, double jump, ProfilingData d, double tStart,
                                                  double tForCurve){
        return generateProfile(locations, jump, 0, 0, d.getMaxLinearVelocity(), d.getMaxAngularVelocity(),
                d.getMaxLinearAccel(), d.getMaxAngularAccel(), tStart, tForCurve, SMOOTHING_TAIL_SIZE);
    }

    public static MotionProfile2D generateProfile(List<State> locations, double jump, ProfilingData d, double tStart,
                                                  double tForCurve, int smoothingTail){
        return generateProfile(locations, jump, 0, 0, d.getMaxLinearVelocity(), d.getMaxAngularVelocity(),
                d.getMaxLinearAccel(), d.getMaxAngularAccel(), tStart, tForCurve, smoothingTail);
    }

    public static MotionProfile2D generateProfile(List<State> locations,
                                                  double jump,
                                                  double velocityStart, double velocityEnd,
                                                  ProfilingData d,
                                                  double tStart,
                                                  double tForCurve,
                                                  int smoothingTail) {
        return generateProfile(locations, jump, velocityStart, velocityEnd,
                d.getMaxLinearVelocity(), d.getMaxAngularVelocity(),
                d.getMaxLinearAccel(), d.getMaxAngularAccel(), tStart, tForCurve, smoothingTail);
    }

    /**
     * @param locations path with points
     * @param jump the jump in "polynomial time" between 0 and 1. should be around 0.001
     * @param velocityStart the start velocity of the robot
     * @param velocityEnd the end velocity. Double.POSITIVE_INFINITY to end moving as fast as possible.
     * @param maxLinearVel maximal linear velocity
     * @param maxAngularVel maximal angular velocity
     * @param maxLinearAcc maximal linear acceleration
     * @param maxAngularAcc maximal angular acceleration
     * @param tStart the start time of the profile
     * @param tForCurve the time range for the polynomials
     * @param smoothingTail the bigger the smoother the velocity graph will be, but a little slower
     * @return
     */
    public static MotionProfile2D generateProfile(List<State> locations,
                                                  double jump,
                                                  double velocityStart, double velocityEnd,
                                                  double maxLinearVel, double maxAngularVel, double maxLinearAcc, double maxAngularAcc,
                                                  double tStart,
                                                  double tForCurve,
                                                  int smoothingTail) {
        int capacity = ((int) ((locations.size() - 1) / jump)) + locations.size() + 1;
        MotionProfile1D linearProfile = new MotionProfile1D(capacity, new MotionProfile1D.Segment(0, 0,0,0, 0));
        MotionProfile1D angularProfile = new MotionProfile1D(capacity, new MotionProfile1D.Segment(0, 0,0,0, 0));
        MotionProfile1D.Segment linearSegment = new MotionProfile1D.Segment(0,1,0,0,0);
        MotionProfile1D.Segment angularSegment, prevAngularSegment = new MotionProfile1D.Segment(0,1,0,0,0);

        DiscreteVelocityGraph velByLoc;

        double t0 = tStart;
        /*
         * divides the path All sub-curves with kinda equal curve
         */
        List<ICurve> subCurves = dividePathToSubCurves(locations, jump, tForCurve, capacity);

        velByLoc = new DiscreteVelocityGraph(subCurves, velocityStart, velocityEnd, maxLinearVel, maxAngularVel, maxLinearAcc, maxAngularAcc, smoothingTail);
        double curvature = 0;

        for (int j = 0; j < subCurves.size(); j++) {

            curvature = subCurves.get(j).getCurvature();
            linearSegment = velByLoc.generateSegment(j, t0);

            t0 = linearSegment.getTEnd();

            angularSegment = linearSegment.clone();

            angularSegment.setStartVelocity(curvature * linearProfile.getVelocity(linearProfile.getTEnd()));
            angularSegment.setStartLocation(curvature * angularProfile.getLocation(angularProfile.getTEnd()));

            prevAngularSegment.setAccel((angularSegment.getStartVelocity() - prevAngularSegment.getStartVelocity())
                    / (prevAngularSegment.getTEnd() - prevAngularSegment.getTStart()));

            linearProfile.unsafeAddSegment(linearSegment);
            angularProfile.unsafeAddSegment(angularSegment);

            prevAngularSegment = angularSegment;

        }

        prevAngularSegment.setAccel(curvature * linearSegment.accel);

        return new MotionProfile2D(linearProfile, angularProfile);
    }

    private static List<ICurve> dividePathToSubCurves(List<State> locations, double jump, double tForCurve, int capacity){
        List<ICurve> subCurves = new ArrayList<>(capacity);
        State first, second;
        for (int i = 0; i < locations.size() - 1; i++) {

            first = locations.get(i);
            second = locations.get(i + 1);
            // This is arbitrary, but empirical evidence suggests this works well
            double tToUse = tForCurve * Point.dist(first, second);

            divideToEqualCurvatureSubcurves(subCurves, QuinticSplineGenerator.generateSpline(first, second,
                    tToUse
            ), jump);

        }
        return subCurves;
    }

    public static double getMaxVelocity(double maxLinearVel, double maxAngularVel, double curvature) {
        return 1.0 / (1.0 / maxLinearVel + Math.abs(curvature) / maxAngularVel);
    }

    public static double getMaxAcceleration(double maxLinearAcc, double maxAngularAcc, double curvature) {
        return 1.0 / (1.0 / maxLinearAcc + Math.abs(curvature) / maxAngularAcc);
    }

    /**
     * This function takes one curve, and stores it's subcurves in a list,
     * such as each subcurve continues the previous one and each subcurve will have
     * roughly equal curve.
     *
     * @param returnList         The list to which the subcurves will be added
     * @param source             The main curve to be divided
     * @param jump               Jump intervals, when sampling the curve the function will sample
     *                           every 'jump' units.
     * @return returnList
     */
    private static List<ICurve> divideToEqualCurvatureSubcurves(List<ICurve> returnList, ICurve source, double jump) {
        double t0, tPrev = 0;

        for (t0 = jump; t0 < 1.0; tPrev = t0, t0 += jump) {
            returnList.add(source.getSubCurve(tPrev, t0));
        }

        returnList.add(source.getSubCurve(tPrev, 1));
        return returnList;
    }


}
