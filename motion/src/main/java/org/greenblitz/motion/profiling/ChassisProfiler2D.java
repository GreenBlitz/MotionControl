package org.greenblitz.motion.profiling;

import org.greenblitz.motion.profiling.curve.ICurve;
import org.greenblitz.motion.base.State;

import java.util.ArrayList;
import java.util.List;

public class ChassisProfiler2D {

    public static MotionProfile2D generateProfile(List<State> locs, double curvatureTolerance, double jump, double maxLinearVel,
                                                  double maxAngularVel, double maxLinearAcc, double maxAngularAcc) {

        MotionProfile1D linearProfile = new MotionProfile1D();
        MotionProfile1D angularProfile = new MotionProfile1D();
        MotionProfile1D tempProfile = new MotionProfile1D();
        State first, second;

        for (int i = 0; i < locs.size() - 1; i++) {

            first = locs.get(i);
            second = locs.get(i + 1);

            ICurve curve = null; // TODO generate the curve somehow

            List<ICurve> subCurves = new ArrayList<>(); // All subcurves with kinda equal curvature
            double t0 = 0;
            double curveStart, prevt0;
            while (t0 < 1.0) {
                curveStart = curve.getCurvature(t0);
                prevt0 = t0;

                for (double j = t0 + jump; j <= 1; j += jump) {
                    if (Math.abs(curve.getCurvature(j) - curveStart) > curvatureTolerance) {
                        subCurves.add(curve.getSubCurve(t0, j));
                        t0 = j;
                        break;
                    }
                }

                if (t0 == prevt0) {
                    subCurves.add(curve.getSubCurve(t0, 1));
                    break;
                }
            }

            double currentMaxLinearVelocity, currentMaxAngularVelocity, curvature;
            ArrayList<ActuatorLocation> path = new ArrayList<>();
            path.add(new ActuatorLocation(0, 0));
            path.add(new ActuatorLocation(0, 0));
            for (ICurve subCur : subCurves) {
                curvature = subCur.getCurvature(0);
                currentMaxLinearVelocity = 1.0 / (1.0 / maxLinearVel + Math.abs(curvature) / maxAngularVel);
                currentMaxAngularVelocity = currentMaxLinearVelocity * curvature;

                path.get(0).setX(subCur.getLength(0));
                path.get(0).setV(subCur.getLinearVelocity(0));
                path.get(1).setX(subCur.getLength(1));
                path.get(1).setV(subCur.getLinearVelocity(1));

                tempProfile = Profiler1D.generateProfile(
                        path,
                        currentMaxLinearVelocity, maxLinearAcc, -maxLinearAcc
                );

                linearProfile.safeAdd(Profiler1D.generateProfile(
                        path,
                        currentMaxLinearVelocity, maxLinearAcc, -maxLinearAcc
                ));
                List<MotionProfile1D.Segment> rotSegs = tempProfile.getSegments();
                for (MotionProfile1D.Segment seg : rotSegs){
                    seg.setAccel(seg.getAccel()*curvature);
                }
            angularProfile.safeAdd(new MotionProfile1D(rotSegs));
            }
        }

        return new MotionProfile2D(linearProfile, angularProfile);
    }

}
