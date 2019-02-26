package org.greenblitz.motion.profiling;

import org.greenblitz.motion.base.State;
import org.greenblitz.motion.profiling.curve.BezierCurve;
import org.greenblitz.motion.profiling.curve.ICurve;
import org.greenblitz.utils.CSVWrapper;

import java.util.ArrayList;
import java.util.List;

public class ChassisProfiler2D {

    public static MotionProfile2D generateProfile(List<State> locs, double curvatureTolerance, double jump, double maxLinearVel,
                                                  double maxAngularVel, double maxLinearAcc, double maxAngularAcc) {
        return generateProfile(locs, curvatureTolerance, jump, maxLinearVel, maxAngularVel, maxLinearAcc, maxAngularAcc, 0);
    }

    public static MotionProfile2D generateProfile(List<State> locs, double curvatureTolerance, double jump, double maxLinearVel,
                                                  double maxAngularVel, double maxLinearAcc, double maxAngularAcc, double tStart) {

        MotionProfile1D linearProfile = new MotionProfile1D(new MotionProfile1D.Segment(0, 0,0,0, 0));
        MotionProfile1D angularProfile = new MotionProfile1D(new MotionProfile1D.Segment(0, 0,0,0, 0));
        MotionProfile1D tempProfile;
        State first, second;
        ICurve curve;
        List<ICurve> subCurves = new ArrayList<>();
        ArrayList<ActuatorLocation> path = new ArrayList<>();
        List<MotionProfile1D.Segment> rotationSegs;

        double t0 = tStart;
        for (int i = 0; i < locs.size() - 1; i++) {

            first = locs.get(i);
            second = locs.get(i + 1);

            curve = new BezierCurve(first, second);

            subCurves.clear(); // All subcurves with kinda equal curve
            divideToEqualCurvatureSubcurves(subCurves, curve, jump, curvatureTolerance);

            VelocityGraph velByLoc = getVelocityGraph(subCurves, maxLinearVel, maxAngularVel,
                    maxLinearAcc, maxAngularAcc);

            double curvature;
            path.clear();
            path.add(new ActuatorLocation(0, 0));
            path.add(new ActuatorLocation(0, 0));

            for (int j = 0; j < subCurves.size(); j++) {
                ICurve subCur = subCurves.get(j);
                curvature = subCur.getCurvature();
                tempProfile = velByLoc.generateProfile(j, t0);
                t0 = tempProfile.getTEnd();

                linearProfile.unsafeAdd(tempProfile);

                rotationSegs = new ArrayList<>();
                MotionProfile1D.Segment curr, prev;
                for (int k = 0; k < tempProfile.segments.size(); k++) {
                    curr = tempProfile.segments.get(k).clone();
                    if(k != 0)
                        prev = tempProfile.segments.get(k - 1);
                    else
                        prev = null;
                    curr.setAccel(curr.getAccel() * curvature);
                    curr.setStartVelocity((k == 0 ? linearProfile.getVelocity(linearProfile.getTEnd()) : prev.getVelocity(prev.getTEnd()))*curvature);
                    curr.setStartLocation(k == 0 ? angularProfile.getLocation(angularProfile.getTEnd()) : prev.getLocation(prev.getTEnd()));
                    rotationSegs.add(curr);
                }
                angularProfile.unsafeAdd(new MotionProfile1D(rotationSegs));
            }
        }

        return new MotionProfile2D(linearProfile, angularProfile);
    }

    public static double getMaxVelocity(double maxLinearVel, double maxAngularVel, double curvature) {
        return 1.0 / (1.0 / maxLinearVel + Math.abs(curvature) / maxAngularVel);
    }

    public static double getMaxAcceleration(double maxLinearAcc, double maxAngularAcc, double curvature) {
        return 1.0 / (1.0 / maxLinearAcc + Math.abs(curvature) / maxAngularAcc);
    }

    /**
     * FIX THIS NOT WORK!
     * This function takes one curve, and stores it's subcurves in a list,
     * such as each subcurve continues the previous one and each subcurve will have
     * roughly equal curve.
     *
     * @param returnList         The list to which the subcurves will be added
     * @param source             The main curve to be divided
     * @param jump               Jump intervals, when sampling the curve the function will sample
     *                           every 'jump' units.
     * @param curvatureTolerance The maximum curve difference within each subcurve.
     * @return returnList
     */
    private static List<ICurve> divideToEqualCurvatureSubcurves(List<ICurve> returnList, ICurve source, double jump, double curvatureTolerance) {
        double t0 = 0;
        double curveStart, prevt0;
        while (t0 < 1.0) {
            curveStart = source.getCurvature(t0);
            prevt0 = t0;

            for (double j = t0 + jump; j <= 1; j += jump) {
                if (Math.abs(source.getCurvature(j) - curveStart) > curvatureTolerance) {
                    returnList.add(source.getSubCurve(t0, j));
                    t0 = j;
                    break;
                }
            }

            if (t0 == prevt0) {
                returnList.add(source.getSubCurve(t0, 1));
                break;
            }
        }
        return returnList;
    }

    private static VelocityGraph getVelocityGraph(List<ICurve> track, double maxLinearVel,
                                                  double maxAngularVel, double maxLinearAcc, double maxAngularAcc) {
        return new VelocityGraph(track, maxLinearVel, maxAngularVel, maxLinearAcc, maxAngularAcc);
    }


}
