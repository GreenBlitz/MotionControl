package org.greenblitz.motion.profiling;

import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Position;
import org.greenblitz.motion.base.State;
import org.greenblitz.motion.profiling.curve.CurveList;
import org.greenblitz.motion.profiling.curve.bazier.BezierCurve;
import org.greenblitz.motion.profiling.curve.ICurve;
import org.greenblitz.motion.profiling.curve.spline.CubicSplineGenerator;
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
        return generateProfile(locations, jump, data.getMaxLinearVelocity(), data.getMaxAngularVelocity(),
                data.getMaxLinearAccel(), data.getMaxAngularAccel(), tStart, 1.0, SMOOTHING_TAIL_SIZE);
    }

    public static MotionProfile2D generateProfile(List<State> locations, double jump, double maxLinearVel,
                                                  double maxAngularVel, double maxLinearAcc, double maxAngularAcc, double tStart) {
        return generateProfile(locations, jump, maxLinearVel, maxAngularVel, maxLinearAcc, maxAngularAcc, tStart, 1.0, SMOOTHING_TAIL_SIZE);
    }

    public static MotionProfile2D generateProfile(List<State> locations, double jump, ProfilingData d, double tStart,
                                                  double tForCurve){
        return generateProfile(locations, jump, d.getMaxLinearVelocity(), d.getMaxAngularVelocity(),
                d.getMaxLinearAccel(), d.getMaxAngularAccel(), tStart, tForCurve, SMOOTHING_TAIL_SIZE);
    }

    /**
     * @param locations path with points
     * @param jump
     * @param maxLinearVel maximal linear velocity
     * @param maxAngularVel maximal angular velocity
     * @param maxLinearAcc maximal linear acceleration
     * @param maxAngularAcc maximal angular acceleration
     * @param tStart
     * @param tForCurve
     * @param smoothingTail the bigger the smoother the velocity graph will be, but a little slower
     * @return
     */
    public static MotionProfile2D generateProfile(List<State> locations, double jump, double maxLinearVel,
                                                  double maxAngularVel, double maxLinearAcc, double maxAngularAcc, double tStart,
                                                  double tForCurve, int smoothingTail) {
        long t0profiling = System.currentTimeMillis();

        /**
         *
         */
        MotionProfile1D linearProfile = new MotionProfile1D(new MotionProfile1D.Segment(0, 0,0,0, 0));
        MotionProfile1D angularProfile = new MotionProfile1D(new MotionProfile1D.Segment(0, 0,0,0, 0));
        MotionProfile1D tempProfile;
        List<MotionProfile1D.Segment> rotationSegs;
        DiscreteVelocityGraph velByLoc;

        double t0 = tStart;
        /**
         * divides the path All sub-curves with kinda equal curve
         */
        List<ICurve> subCurves = dividePathToSubCurves(locations,jump,tForCurve);

        velByLoc = new DiscreteVelocityGraph(subCurves, maxLinearVel, maxAngularVel, maxLinearAcc, maxAngularAcc, smoothingTail);
        double curvature;

        for (int j = 0; j < subCurves.size(); j++) {

            ICurve subCur = subCurves.get(j);
            curvature = subCur.getCurvature();
            tempProfile = velByLoc.generateProfile(j, t0);

            t0 = tempProfile.getTEnd();

            rotationSegs = new ArrayList<>();
            MotionProfile1D.Segment curr, prev;

            // TODO this loop can be slightly optimised to remove all the if statements
            for (int k = 0; k < tempProfile.segments.size(); k++) {
                curr = tempProfile.segments.get(k).clone();
                if(k != 0)
                    prev = tempProfile.segments.get(k - 1);
                else
                    prev = null;

                curr.setStartVelocity((k == 0 ? linearProfile.getVelocity(linearProfile.getTEnd()) : prev.getVelocity(prev.getTEnd()))*curvature);
                curr.setStartLocation(k == 0 ? angularProfile.getLocation(angularProfile.getTEnd()) : prev.getLocation(prev.getTEnd()));

                if (prev != null){
                    curr.setAccel(Math.abs(curr.accel * curvature) *
                            Math.signum(curr.startVelocity - rotationSegs.get(k - 1).startVelocity));
                } else {
                    curr.setAccel(Math.abs(curr.accel * curvature) *
                            Math.signum(curr.startVelocity - angularProfile.getVelocity(angularProfile.getTEnd())));
                }

                rotationSegs.add(curr);
            }

            linearProfile.unsafeAdd(tempProfile);
            angularProfile.unsafeAdd(new MotionProfile1D(rotationSegs));
        }

        System.out.println("Profiling");
        System.out.println(System.currentTimeMillis() - t0profiling);

        return new MotionProfile2D(linearProfile, angularProfile);
    }

    private static List<ICurve> dividePathToSubCurves(List<State> locations, double jump, double tForCurve){
        List<ICurve> subCurves = new ArrayList<>();
        State first,second;
        for (int i = 0; i < locations.size() - 1; i++) {

            first = locations.get(i);
            second = locations.get(i + 1);
            // TODO this is very arbitrary
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


    public static final double DIST = 0;
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
//        long time = System.currentTimeMillis();
        double t0, tPrev = 0;
        double sumSoFar = 0;
        double tPrevUsed = 0;

        for (t0 = getJump(source, 0, jump); t0 < 1.0; tPrev = t0, t0 += getJump(source, t0, jump)) {
            if(t0 > 1)
                throw new RuntimeException("how you do this");
            sumSoFar += source.getSubCurve(tPrev, t0).getLength(1);
            if (true){
                returnList.add(source.getSubCurve(tPrevUsed, t0));
                tPrevUsed = t0;
                sumSoFar = 0;
            }
        }

        returnList.add(source.getSubCurve(tPrevUsed, 1));
        return returnList;
    }

    private static double getJump(ICurve curve, double location, double jump){
        return jump;
    }

    private static VelocityGraph getVelocityGraph(List<ICurve> track, double maxLinearVel,
                                                  double maxAngularVel, double maxLinearAcc, double maxAngularAcc) {
        return new VelocityGraph(track, maxLinearVel, maxAngularVel, maxLinearAcc, maxAngularAcc);
    }


}
