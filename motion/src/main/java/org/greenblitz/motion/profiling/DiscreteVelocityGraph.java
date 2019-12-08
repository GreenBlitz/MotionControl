package org.greenblitz.motion.profiling;

import org.greenblitz.motion.profiling.curve.ICurve;

import java.util.ArrayList;
import java.util.List;

public class DiscreteVelocityGraph {


    protected List<VelocitySegment> segments;

    public DiscreteVelocityGraph(List<ICurve> track, double maxLinearVel,
                         double maxAngularVel, double maxLinearAcc, double maxAngularAcc) {

        double tmpLength = 0;

        segments = new ArrayList<>();
        for (ICurve curve : track) {
            segments.add(new VelocitySegment(tmpLength, tmpLength + curve.getLength(1),
                    ChassisProfiler2D.getMaxVelocity(maxLinearVel, maxAngularVel, curve.getCurvature()),
                    ChassisProfiler2D.getMaxAcceleration(maxLinearAcc, maxAngularAcc, curve.getCurvature()))
            );
            tmpLength += curve.getLength(1);
        }
        int segCount = segments.size();

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
        return Profiler1D.generateProfile(seg.velocityMax, seg.accel, -seg.accel, tStart,
                new ActuatorLocation(seg.distanceStart, seg.getStartVelocity()),
                new ActuatorLocation(seg.distanceEnd, seg.getEndVelocity()));
    }


    class VelocitySegment {

        public double velocityMax;
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
        }

        public void developForwards(VelocitySegment prev, VelocitySegment next){
            if (prev == null) {
                velocityStartForwards = 0;
            } else {
                velocityStartForwards = prev.velocityEndForwards;
            }
            velocityEndForwards = Math.min(velocityMax,
                    Math.sqrt(velocityStartForwards*velocityStartForwards + 2*(distanceEnd - distanceStart)*accel));
            if (next != null){
                velocityEndForwards = Math.min(velocityStartForwards, next.velocityMax);
            }
        }

        public void developBackwards(VelocitySegment prev, VelocitySegment next){
            if (next == null) {
                velocityEndBackwards = 0;
            } else {
                velocityEndBackwards = next.velocityStartBackwards;
            }
            velocityStartBackwards = Math.min(velocityMax,
                    Math.sqrt(velocityEndBackwards*velocityEndBackwards + 2*(distanceEnd - distanceStart)*accel));
            if (prev != null){
                velocityStartBackwards = Math.min(velocityStartBackwards, prev.velocityMax);
            }
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

    }


}
