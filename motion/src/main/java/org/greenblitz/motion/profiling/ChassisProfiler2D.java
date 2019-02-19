package org.greenblitz.motion.profiling;

import org.greenblitz.motion.base.State;
import org.greenblitz.motion.profiling.curve.BezierCurve;
import org.greenblitz.motion.profiling.curve.ICurve;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;

public class ChassisProfiler2D {

    public static MotionProfile2D generateProfile(List<State> locs, double curvatureTolerance, double jump, double maxLinearVel,
                                                  double maxAngularVel, double maxLinearAcc, double maxAngularAcc, double tStart) {

        MotionProfile1D linearProfile = new MotionProfile1D();
        MotionProfile1D angularProfile = new MotionProfile1D();
        MotionProfile1D tempProfile;
        State first, second;
        ICurve curve;
        List<ICurve> subCurves = new ArrayList<>();
        ArrayList<ActuatorLocation> path = new ArrayList<>();
        List<MotionProfile1D.Segment> rotSegs;

        double t0 = tStart;
        for (int i = 0; i < locs.size() - 1; i++) {

            first = locs.get(i);
            second = locs.get(i + 1);

            curve = new BezierCurve(first, second, 0, 1);

            subCurves.clear(); // All subcurves with
            divideToEqualCurvatureSubcurves(subCurves, curve, jump, curvatureTolerance);

            double currentMaxLinearVelocity, currenctMaxLinearAccel, curvature;
            path.clear();
            path.add(new ActuatorLocation(0, 0));
            path.add(new ActuatorLocation(0, 0));
            for (ICurve subCur : subCurves) {
                curvature = subCur.getCurvature(0);
                currentMaxLinearVelocity = 1.0 / (1.0 / maxLinearVel + Math.abs(curvature) / maxAngularVel);
                currenctMaxLinearAccel = 1.0 / (1.0 / maxLinearAcc + Math.abs(curvature) / maxAngularAcc);

                path.get(0).setX(subCur.getLength(0));
                path.get(0).setV(subCur.getLinearVelocity(0));
                path.get(1).setX(subCur.getLength(1));
                path.get(1).setV(subCur.getLinearVelocity(1));

                tempProfile = Profiler1D.generateProfile(
                        path,
                        currentMaxLinearVelocity, currenctMaxLinearAccel, -currenctMaxLinearAccel, t0
                );
                t0 = tempProfile.getTEnd();

                linearProfile.unsafeAdd(tempProfile);

                rotSegs = tempProfile.getSegments();
                for (MotionProfile1D.Segment seg : rotSegs) {
                    seg.setAccel(seg.getAccel() * curvature);
                }
                angularProfile.unsafeAdd(new MotionProfile1D(rotSegs));
            }
        }

        return new MotionProfile2D(linearProfile, angularProfile);
    }

    /**
     * This function takes one curve, and stores it's subcurves in a list,
     * such as each subcurve continues the previous one and each subcurve will have
     * roughly equal curvature.
     * @param returnList The list to which the subcurves will be added
     * @param source The main curve to be divided
     * @param jump Jump intervals, when sampling the curvature the function will sample
     *             every 'jump' units.
     * @param curvatureTolerance The maximum curvature difference within each subcurve.
     * @return returnList
     */
    private static List<ICurve> divideToEqualCurvatureSubcurves(List<ICurve> returnList, ICurve source, double jump, double curvatureTolerance){
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

}
