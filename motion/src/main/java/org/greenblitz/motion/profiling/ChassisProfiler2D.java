package org.greenblitz.motion.profiling;

import org.greenblitz.motion.base.State;
import org.greenblitz.motion.profiling.curve.bazier.BezierCurve;
import org.greenblitz.motion.profiling.curve.ICurve;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexey
 * @author Udi
 */
public class ChassisProfiler2D {

    public static MotionProfile2D generateProfile(List<State> locations, double jump, double maxLinearVel,
                                                  double maxAngularVel, double maxLinearAcc, double maxAngularAcc, double tStart, double epsilon){
        VelocityGraph.setDefaultEpsilon(epsilon);
        return generateProfile(locations, jump, maxLinearVel, maxAngularVel, maxLinearAcc, maxAngularAcc, tStart);
    }

    public static MotionProfile2D generateProfile(List<State> locations, double jump, double maxLinearVel,
                                                  double maxAngularVel, double maxLinearAcc, double maxAngularAcc) {
        return generateProfile(locations, jump, maxLinearVel, maxAngularVel, maxLinearAcc, maxAngularAcc, 0);
    }

    public static MotionProfile2D generateProfile(List<State> locations, double jump, double maxLinearVel,
                                                  double maxAngularVel, double maxLinearAcc, double maxAngularAcc, double tStart) {

        MotionProfile1D linearProfile = new MotionProfile1D(new MotionProfile1D.Segment(0, 0,0,0, 0));
        MotionProfile1D angularProfile = new MotionProfile1D(new MotionProfile1D.Segment(0, 0,0,0, 0));
        MotionProfile1D tempProfile;
        State first, second;
        ICurve curve;
        List<ICurve> subCurves = new ArrayList<>(); // All sub-curves with kinda equal curve
        ArrayList<ActuatorLocation> path = new ArrayList<>();
        List<MotionProfile1D.Segment> rotationSegs;

        double t0 = tStart;
        for (int i = 0; i < locations.size() - 1; i++) {

            first = locations.get(i);
            second = locations.get(i + 1);

            curve = new BezierCurve(first, second);

            subCurves.clear();
            divideToEqualCurvatureSubcurves(subCurves, curve, jump);

            VelocityGraph velByLoc = getVelocityGraph(subCurves, maxLinearVel, maxAngularVel,
                    maxLinearAcc, maxAngularAcc);
//            velByLoc.generateCSV("velocityByDistance.csv");

            double curvature;
            path.clear();
            path.add(new ActuatorLocation(0, 0));
            path.add(new ActuatorLocation(0, 0));

            long t0profiling = System.currentTimeMillis();

            for (int j = 0; j < subCurves.size(); j++) {
                ICurve subCur = subCurves.get(j);
                curvature = subCur.getCurvature();
                long tDab = System.currentTimeMillis();
                tempProfile = velByLoc.generateProfile(j, t0);
                long dTDab = System.currentTimeMillis() - tDab;
                if(dTDab != 0) System.out.println(dTDab);
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

            System.out.println("Profiling");
            System.out.println(System.currentTimeMillis() - t0profiling);
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
     * @return returnList
     */
    private static List<ICurve> divideToEqualCurvatureSubcurves(List<ICurve> returnList, ICurve source, double jump) {
        long time = System.currentTimeMillis();
        double t0, tPrev = 0;
        for (t0 = getJump(source, 0, jump); t0 < 1.0; tPrev = t0, t0 += getJump(source, t0, jump)) {

            if(t0 > 1)
                throw new RuntimeException("how you do this");

            returnList.add(source.getSubCurve(tPrev, t0));

        }

        returnList.add(source.getSubCurve(tPrev, 1));
        System.out.println("curve division");
        System.out.println(System.currentTimeMillis()-time);
        return returnList;
    }

    private static double getJump(ICurve curve, double location, double jump){
//        double vel = curve.getLinearVelocity(location);
//        double ret = vel > jump ? jump/vel : 0.01;
//        return ret;
        return jump;
    }

    private static VelocityGraph getVelocityGraph(List<ICurve> track, double maxLinearVel,
                                                  double maxAngularVel, double maxLinearAcc, double maxAngularAcc) {
        return new VelocityGraph(track, maxLinearVel, maxAngularVel, maxLinearAcc, maxAngularAcc);
    }


}
