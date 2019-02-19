package org.greenblitz.motion.profiling;

import org.greenblitz.motion.base.State;
import org.greenblitz.motion.profiling.curve.BezierCurve;
import org.greenblitz.motion.profiling.curve.ICurve;

import java.util.ArrayList;
import java.util.List;

public class ChassisProfiler2D {

    public static MotionProfile2D generateProfile(List<State> locs, double curvatureTolerance, double jump, double maxLinearVel,
                                                  double maxAngularVel, double maxLinearAcc, double maxAngularAcc) {
        return generateProfile(locs, curvatureTolerance, jump, maxLinearVel, maxAngularVel, maxLinearAcc, maxAngularAcc, 0);
    }

    public static MotionProfile2D generateProfile(List<State> locs, double curvatureTolerance, double jump, double maxLinearVel,
                                                  double maxAngularVel, double maxLinearAcc, double maxAngularAcc, double tStart) {

        MotionProfile1D linearProfile = new MotionProfile1D();
        MotionProfile1D angularProfile = new MotionProfile1D();
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

            subCurves.clear(); // All subcurves with kinda equal curvature
            divideToEqualCurvatureSubcurves(subCurves, curve, jump, curvatureTolerance);
            System.out.println(subCurves);

            double currentMaxLinearVelocity, currenctMaxLinearAccel, curvature;
            path.clear();
            path.add(new ActuatorLocation(0, 0));
            path.add(new ActuatorLocation(0, 0));
            double lenSoFar = 0;
            for (ICurve subCur : subCurves) {
                curvature = subCur.getCurvature();
                currentMaxLinearVelocity = getMaxVelocity(maxLinearVel, maxAngularVel, curvature);
                currenctMaxLinearAccel = getMaxAcceleration(maxLinearAcc, maxAngularAcc, curvature);

                path.get(0).setX(lenSoFar);
                path.get(0).setV(subCur.getLinearVelocity(0));
                path.get(1).setX(lenSoFar + subCur.getLength(1));
                path.get(1).setV(subCur.getLinearVelocity(1));
                lenSoFar = path.get(1).getX();

                tempProfile = Profiler1D.generateProfile(
                        path,
                        currentMaxLinearVelocity, currenctMaxLinearAccel, -currenctMaxLinearAccel, t0
                );
                t0 = tempProfile.getTEnd();

                linearProfile.unsafeAdd(tempProfile);

                rotationSegs = tempProfile.getSegments();
                MotionProfile1D.Segment seg0 = rotationSegs.get(0);
                seg0.setAccel(seg0.getAccel()*curvature);
                seg0.setStartLocation(first.getAngle());
                seg0.setStartVelocity(first.getAngularVelocity());
                MotionProfile1D.Segment curr, prev;
                for (int k = 1; k < rotationSegs.size(); k++) {
                    curr = rotationSegs.get(k);
                    prev = rotationSegs.get(k - 1);
                    curr.setAccel(curr.getAccel() * curvature);
                    curr.setStartVelocity(prev.getVelocity(prev.getTEnd()));
                    curr.setStartLocation(prev.getLocation(prev.getTEnd()));
                }
                angularProfile.unsafeAdd(new MotionProfile1D(rotationSegs));
            }
        }

        return new MotionProfile2D(linearProfile, angularProfile);
    }

    private static double getMaxVelocity(double maxLinearVel, double maxAngularVel, double curvature){
        return 1.0 / (1.0 / maxLinearVel + Math.abs(curvature) / maxAngularVel);
    }

    private static double getMaxAcceleration(double maxLinearAcc, double maxAngularAcc, double curvature){
        return 1.0 / (1.0 / maxLinearAcc + Math.abs(curvature) / maxAngularAcc);
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

    private static List<VelocitySegment> getVelocityGragh(List<ICurve> track, double maxLinearVel,
                                                          double maxAngularVel, double maxLinearAcc, double maxAngularAcc){
        List<VelocitySegment> gragh1 = new ArrayList<>();
        gragh1.add(new VelocitySegment(0, 0, 0, 0));
        for(ICurve curve: track){
            double start = gragh1.get(gragh1.size()-1).getDEnd();
            double length = curve.getLength(1);
            double vStart = getMaxVelocity(maxLinearVel, maxAngularVel, curve.getCurvature());
            double slope = 0;
            gragh1.add(new VelocitySegment(start,start+length, vStart, slope));
        }
        double end = gragh1.get(gragh1.size()-1).getDEnd();
        gragh1.add(new VelocitySegment(end, end, 0, 0));

        List<VelocitySegment> gragh2 = new ArrayList<>();
        gragh2.add(gragh1.get(0));
        for(int ind=1; ind<gragh1.size(); ind++){
            if(gragh1.get(ind).getVStart() > gragh2.get(ind-1).getVStart()){
                ICurve curve = track.get(ind);
                double slope = getMaxAcceleration(maxLinearAcc, maxAngularAcc, curve.getCurvature())/;
                double start = gragh2.get(ind-1).getDEnd();
                double length = curve.getLength(1);
                double vStart = gragh1.get(gragh1.size()-1).getVEnd();

            }
        }
    }

    public static class VelocitySegment{
        private double dStart, dEnd, vStart, vEnd, rootConst;

        public VelocitySegment(double dStart, double dEnd, double vStart, double rootConst) {
            this.dStart = dStart;
            this.dEnd = dEnd;
            this.vStart = vStart;
            this.rootConst = rootConst;
            this.vEnd = vStart + rootConst *(dEnd-dStart);
        }

        public double getDStart() {
            return dStart;
        }

        public void setDStart(double dStart) {
            this.dStart = dStart;
            this.vStart = ;
        }

        public double getDEnd() {
            return dEnd;
        }

        public void setDEnd(double dEnd) {
            this.dEnd = dEnd;
            this.vEnd = vStart + rootConst *(dEnd-dStart);
        }

        public double getVStart() {
            return vStart;
        }

        public void setVStart(double vStart) {
            this.vStart = vStart;
            this.rootConst = (vEnd-vStart)/(dEnd-dStart);
        }

        public double getVEnd() {
            return vEnd;
        }

        public void setVEnd(double vEnd) {
            this.vEnd = vEnd;
            this.rootConst = (vEnd-vStart)/(dEnd-dStart);
        }

        public double getRootConst() {
            return rootConst;
        }

        public void setRootConst(double rootConst) {
            this.rootConst = rootConst;
            this.vEnd = vStart + rootConst *(dEnd-dStart);
        }
    }
}
