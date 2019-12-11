package org.greenblitz.motion.profiling;

import org.greenblitz.motion.profiling.curve.ICurve;
import org.greenblitz.utils.CSVWrapper;

import java.util.ArrayList;
import java.util.List;

public class DiscreteVelocityGraph {


    protected List<VelocitySegment> segments;

    public DiscreteVelocityGraph(List<ICurve> track, double maxLinearVel,
                         double maxAngularVel, double maxLinearAcc, double maxAngularAcc, int tailSize) {

        double tmpLength = 0;

        segments = new ArrayList<>();
        double curveLen;
        for (ICurve curve : track) {
            curveLen = curve.getLength(1);
            segments.add(new VelocitySegment(tmpLength, tmpLength + curveLen,
                    ChassisProfiler2D.getMaxVelocity(maxLinearVel, maxAngularVel, curve.getCurvature()),
                    ChassisProfiler2D.getMaxAcceleration(maxLinearAcc, maxAngularAcc, curve.getCurvature()))
            );
            tmpLength += curveLen;
        }


        int segCount = segments.size();
        for (int i = 1; i < segCount - 1; i++){
            segments.get(i).filter(segments, i, tailSize);
        }

        segments.get(0).developForwards(null, segments.get(1));
        segments.get(segCount - 1).developBackwards(segments.get(segCount - 2), null);

        for (int i = 1; i < segCount - 1; i++){
            segments.get(i).developForwards(segments.get(i - 1), segments.get(i + 1));
            segments.get(segCount - 1 - i).developBackwards(segments.get(segCount - 2 - i), segments.get(segCount - i));
        }

        segments.get(segCount - 1).developForwards(segments.get(segCount - 2), null);
        segments.get(0).developBackwards(null, segments.get(1));

    }


    public MotionProfile1D generateProfile(int index, double tStart) {
        VelocitySegment seg = segments.get(index);

        // TODO not mathematically perfect (dt)

        double vS = seg.getStartVelocity();
        double vE = seg.getEndVelocity();

        double dt = 2*((seg.distanceEnd - seg.distanceStart)/(vS + vE));
        return new MotionProfile1D(new MotionProfile1D.Segment(
                tStart, tStart + dt, (vE - vS)/dt, vS, seg.distanceStart));
//        if (Math.abs(vS - vE) < 0.001){
//            double dt = 2*((seg.distanceEnd - seg.distanceStart)/(vS + vE));
//            return new MotionProfile1D(new MotionProfile1D.Segment(
//                    tStart, tStart + dt, (vE - vS)/dt, vS, seg.distanceStart));
//        }
//        return Profiler1D.generateProfile(seg.velocityMax, seg.accel, -seg.accel, tStart,
//                new ActuatorLocation(seg.distanceStart, vS),
//                new ActuatorLocation(seg.distanceEnd, vE));
    }

    public void generateCSV(String name) {
        CSVWrapper file = CSVWrapper.generateWrapper(name, 0, "d", "velocity", "acceleration");
        for (VelocitySegment range : segments)
            range.insertToCSV(file);
        file.flush();
    }

    class VelocitySegment {

        public double velocityMax;
        public double velocityMaxSmoothed;
        public double velocityStartForwards;
        public double velocityEndForwards;
        public double velocityStartBackwards;
        public double velocityEndBackwards;
        public double accel;
        public double distanceStart, distanceEnd;

        public VelocitySegment(double start, double end, double maxV, double accel){
            this.distanceStart = start;
            this.distanceEnd = end;
            this.accel = accel;
            this.velocityMax = maxV;
            this.velocityMaxSmoothed = maxV;
        }

        public void developForwards(VelocitySegment prev, VelocitySegment next){
            if (prev == null) {
                velocityStartForwards = 0;
            } else {
                velocityStartForwards = prev.velocityEndForwards;
            }
            velocityEndForwards = Math.min(velocityMaxSmoothed,
                    Math.sqrt(velocityStartForwards*velocityStartForwards + 2*(distanceEnd - distanceStart)*accel));
            if (next != null){
                velocityEndForwards = Math.min(velocityEndForwards, next.velocityMaxSmoothed);
            }
        }

        public void developBackwards(VelocitySegment prev, VelocitySegment next){
            if (next == null) {
                velocityEndBackwards = 0;
            } else {
                velocityEndBackwards = next.velocityStartBackwards;
            }
            velocityStartBackwards = Math.min(velocityMaxSmoothed,
                    Math.sqrt(velocityEndBackwards*velocityEndBackwards + 2*(distanceEnd - distanceStart)*accel));
            if (prev != null){
                velocityStartBackwards = Math.min(velocityStartBackwards, prev.velocityMaxSmoothed);
            }
        }

        public void filter(List<VelocitySegment> segs, int subject, int tailSize){
            int size = Math.min(subject + tailSize, segs.size() - 1) - Math.max(subject - tailSize, 0) + 1;
            double val = 0;
            for (int i = Math.max(subject - tailSize, 0); i <= Math.min(subject + tailSize, segs.size() - 1); i++){
                val += segs.get(i).velocityMax / size;
            }
            this.velocityMaxSmoothed = Math.min(val, this.velocityMax);
        }

        public double getStartVelocity(){
            return Math.min(velocityStartForwards, velocityStartBackwards);
        }

        public double getEndVelocity(){
            return Math.min(velocityEndForwards, velocityEndBackwards);
        }

        public boolean isPartOfRange(double d){
            return d >= distanceStart && d <= distanceEnd;
        }

        public void insertToCSV(CSVWrapper file) {
            file.addValues(distanceStart, getStartVelocity(), accel);
        }

    }


}
