package org.greenblitz.motion.profiling;

import org.greenblitz.motion.profiling.curve.ICurve;
import org.greenblitz.utils.CSVWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Package protected on purpose.
 * @author alexey
 */
class DiscreteVelocityGraph {

    protected List<VelocitySegment> segments;

    public DiscreteVelocityGraph(List<ICurve> track, double maxLinearVel,
                         double maxAngularVel, double maxLinearAcc, double maxAngularAcc, int tailSize) {

        double tmpLength = 0;

        segments = new ArrayList<>();
        double curveLen;
        double curvature;

        for (ICurve curve : track) {
            curveLen = curve.getLength(1);
            curvature = curve.getCurvature();
            segments.add(new VelocitySegment(tmpLength, tmpLength + curveLen,
                    ChassisProfiler2D.getMaxVelocity(maxLinearVel, maxAngularVel, curvature),
                    ChassisProfiler2D.getMaxAcceleration(maxLinearAcc, maxAngularAcc, curvature))
            );
            tmpLength += curveLen;
        }


        int segCount = segments.size();
        for (int i = 1; i < segCount - 1; i++){
            segments.get(i).filter(segments, i, tailSize);
        }

        segments.get(0).developForwardsFirst(segments.get(1), 0);
        segments.get(segCount - 1).developBackwardsLast(segments.get(segCount - 2), 0);

        for (int i = 1; i < segCount - 1; i++){
            segments.get(i).developForwards(segments.get(i - 1), segments.get(i + 1));
            segments.get(segCount - 1 - i).developBackwards(segments.get(segCount - 2 - i), segments.get(segCount - i));
        }

        segments.get(segCount - 1).developForwardsLast(segments.get(segCount - 2));
        segments.get(0).developBackwardsFirst(segments.get(1));

    }


    public MotionProfile1D generateProfile(int index, double tStart) {
        return new MotionProfile1D(generateSegment(index, tStart));
    }

    public MotionProfile1D.Segment generateSegment(int index, double tStart){
        VelocitySegment seg = segments.get(index);

        double vS = seg.getStartVelocity();
        double vE = seg.getEndVelocity();

        double dt = 2*((seg.distanceEnd - seg.distanceStart)/(vS + vE));
        return new MotionProfile1D.Segment(
                tStart, tStart + dt, (vE - vS)/dt, vS, seg.distanceStart);
    }


    public void generateCSV(String name) {
        CSVWrapper file = CSVWrapper.generateWrapper(name, 0, "d", "velocity", "acceleration");
        for (VelocitySegment range : segments)
            range.insertToCSV(file);
        file.flush();
    }

    class VelocitySegment {

        public final AccelerationInterpolator exponentialInterpolator =
            (currentVelocity, maximumAsymptoticVelocity, maximumInitialAccel) -> {
                if (currentVelocity == maximumAsymptoticVelocity)
                    return 0;
                else
                    return Math.pow(Math.E, 1/maximumAsymptoticVelocity + 1/(currentVelocity - maximumAsymptoticVelocity))*maximumInitialAccel;
            };

        public final AccelerationInterpolator hyperbolicInterpolator =
                (currentVelocity, maximumAsymptoticVelocity, maximumInitialAccel)
                        -> 1 / (currentVelocity + (1 / maximumInitialAccel))
                        - (currentVelocity / maximumAsymptoticVelocity) / (maximumAsymptoticVelocity + (1 / maximumInitialAccel));

        public final AccelerationInterpolator linearInterpolator =
                (currentVelocity, maximumAsymptoticVelocity, maximumInitialAccel)
                        -> -(maximumInitialAccel / maximumAsymptoticVelocity) * currentVelocity + maximumInitialAccel;

        public final AccelerationInterpolator equationInterpolator =
                (currentVelocity, maximumAsymptoticVelocity, maximumInitialAccel)
                        -> 0.831 + 5.9*currentVelocity - 3.1*Math.pow(currentVelocity, 2) + 0.366*Math.pow(currentVelocity, 3);

        public double velocityMax;
        public double velocityMaxSmoothed;
        public double velocityStartForwards;
        public double velocityEndForwards;
        public double velocityStartBackwards;
        public double velocityEndBackwards;
        public double accel;
        public double distanceStart, distanceEnd;
        public AccelerationInterpolator interpolator;

        public VelocitySegment(double start, double end, double maxV, double accel){
            this.distanceStart = start;
            this.distanceEnd = end;
            this.accel = accel;
            this.velocityMax = maxV;
            this.velocityMaxSmoothed = maxV;
            interpolator = linearInterpolator;
        }

        public void developForwardsFirst( VelocitySegment next, double startVel){
            velocityStartForwards = startVel;

            double withTheGrainAccel = interpolator.getRealMaxAccel(velocityStartForwards, velocityMax, accel);

            velocityEndForwards = Math.min(velocityMaxSmoothed,
                    Math.sqrt(velocityStartForwards*velocityStartForwards + 2*(distanceEnd - distanceStart)*withTheGrainAccel));

            velocityEndForwards = Math.min(velocityEndForwards, next.velocityMaxSmoothed);
        }

        public void developForwardsLast( VelocitySegment prev){
            velocityStartForwards = prev.velocityEndForwards;

            double withTheGrainAccel = interpolator.getRealMaxAccel(velocityStartForwards, velocityMax, accel);

            velocityEndForwards = Math.min(velocityMaxSmoothed,
                    Math.sqrt(velocityStartForwards*velocityStartForwards + 2*(distanceEnd - distanceStart)*withTheGrainAccel));
        }

        public void developForwards( VelocitySegment prev,  VelocitySegment next){
            velocityStartForwards = prev.velocityEndForwards;


            double withTheGrainAccel = interpolator.getRealMaxAccel(velocityStartForwards, velocityMax, accel);

            velocityEndForwards = Math.min(velocityMaxSmoothed,
                    Math.sqrt(velocityStartForwards*velocityStartForwards + 2*(distanceEnd - distanceStart)*withTheGrainAccel));

            velocityEndForwards = Math.min(velocityEndForwards, next.velocityMaxSmoothed);
        }

        public void developBackwardsFirst( VelocitySegment next){
            velocityEndBackwards = next.velocityStartBackwards;

            double actAcc = interpolator.getRealMaxAccel(-velocityEndBackwards, velocityMax, accel);

            velocityStartBackwards = Math.min(velocityMaxSmoothed,
                    Math.sqrt(velocityEndBackwards*velocityEndBackwards + 2*(distanceEnd - distanceStart)*actAcc));
        }

        public void developBackwardsLast( VelocitySegment prev, double endVel){
            velocityEndBackwards = endVel;

            double actAcc = interpolator.getRealMaxAccel(-velocityEndBackwards, velocityMax, accel);

            velocityStartBackwards = Math.min(velocityMaxSmoothed,
                    Math.sqrt(velocityEndBackwards*velocityEndBackwards + 2*(distanceEnd - distanceStart)*actAcc));
            velocityStartBackwards = Math.min(velocityStartBackwards, prev.velocityMaxSmoothed);
        }

        public void developBackwards( VelocitySegment prev,  VelocitySegment next){
            velocityEndBackwards = next.velocityStartBackwards;

            double actAcc = interpolator.getRealMaxAccel(-velocityEndBackwards, velocityMax, accel);

            velocityStartBackwards = Math.min(velocityMaxSmoothed,
                    Math.sqrt(velocityEndBackwards*velocityEndBackwards + 2*(distanceEnd - distanceStart)*actAcc));
            velocityStartBackwards = Math.min(velocityStartBackwards, prev.velocityMaxSmoothed);

        }

        public void filter(List<VelocitySegment> segs, int subject, int tailSize) {
            int start =  Math.max(subject - tailSize, 0);
            int end = Math.min(subject + tailSize, segs.size() - 1);
            double val = 0;
            for (int i = start; i <= end; i++) {
                val += segs.get(i).velocityMax;
            }
            this.velocityMaxSmoothed = Math.min(val / (end - start + 1), this.velocityMax);
        }

        public double getStartVelocity(){
            return Math.min(velocityStartForwards, velocityStartBackwards);
        }

        public double getEndVelocity(){
            return Math.min(velocityEndForwards, velocityEndBackwards);
        }

        public void insertToCSV(CSVWrapper file) {
            file.addValues(distanceStart, getStartVelocity(), accel);
        }

    }


}
