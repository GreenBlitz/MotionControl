package org.greenblitz.motion.profiling;

import org.greenblitz.motion.base.ICurve;
import org.greenblitz.motion.base.State;

import java.util.ArrayList;
import java.util.List;

public class ChassisProfiler2D {

    public static MotionProfile2D generateProfile(List<State> locs, double curvatureTolerance, double jump, double maxLinearVel,
                                                  double maxAngularVel, double maxLinearAcc, double maxAngularAcc) {

        ICurve curve = null; // TODO generate the curve somehow

        List<ICurve> subCurves = new ArrayList<>(); // All subcurves with kinda equal curvature
        double t0 = 0;
        double curveStart, prevt0;
        while (t0 < 1.0) {
            curveStart = curve.getCurvature(t0);
            prevt0 = t0;

            for (double i = t0 + jump; i <= 1; i += jump) {
                if (Math.abs(curve.getCurvature(i) - curveStart) > curvatureTolerance) {
                    subCurves.add(curve.getSubCurve(t0, i));
                    t0 = i;
                    break;
                }
            }

            if (t0 == prevt0) {
                subCurves.add(curve.getSubCurve(t0, 1));
                break;
            }
        }

        MotionProfile1D linearProfile = new MotionProfile1D();
        MotionProfile1D angularProfile = new MotionProfile1D();
        double currentMaxLinearVelocity, currentMaxAngularVelocity, curvature;
        for (ICurve subCur : subCurves){
            curvature = subCur.getCurvature(0);
            currentMaxLinearVelocity = 1.0 / (1.0/maxLinearVel + curvature/maxAngularVel);
        }

        return new MotionProfile2D(linearProfile, angularProfile);
    }

}
