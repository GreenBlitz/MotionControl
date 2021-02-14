package org.greenblitz.motion.profiling;

import org.greenblitz.motion.base.TwoTuple;
import org.greenblitz.motion.profiling.curve.ICurve;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Orel & Ittai, inspired by Alexey's code
 */
public class DiscreteVelocityGraphLC {

    protected List<Segment> segments;

    //please ask Alexey, and if he explains it to you, tell me too!
    private int latestFilterIndex = -1;
    private int latestFilterTail;
    private double latestFilterValue = 0;

    //TODO: decide if we want to use the same bar to all segments when we develop.
    public double maxVBar, maxWBar;
    public double maxAVBar, maxAWBar;

    public DiscreteVelocityGraphLC(List<ICurve> track, double vStart, double vEnd, double maxV, double maxW,
                                   double maxAccVel, double maxAccWel, int tailSize) {

        maxVBar = maxV;
        maxWBar = maxW;
        maxAVBar = maxAccVel;
        maxAWBar = maxAccWel;


        double tmpLength = 0;

        segments = new ArrayList<>();
        double curveLen;
        double curvatureStart;
        double curvatureEnd;
        double curvature;

        for (ICurve curve : track) {
            curveLen = curve.getLength(1);
            curvatureStart = curve.getCurvature(0);
            curvatureEnd = curve.getCurvature(1);
            curvature = curve.getCurvature();
            segments.add(new DiscreteVelocityGraphLC.Segment(tmpLength, tmpLength + curveLen,
                    curvatureStart, curvatureEnd,
                    ChassisProfiler2D.getMaxVelocity(maxVBar, maxWBar, curvature),
                    ChassisProfiler2D.getMaxAcceleration(maxAVBar, maxAWBar, curvature))
            );
            tmpLength += curveLen;
        }

        int segCount = segments.size();



        segments.get(0).developForwards(vStart, maxVBar);
        segments.get(segCount - 1).developBackwards(maxVBar, vEnd);

        for (int i = 1; i < segCount - 1; i++) {
            segments.get(i)
                    .developForwards(segments.get(i - 1).velocityEndForwards, segments.get(i + 1).vMax);

            segments.get(segCount - 1 - i)
                    .developBackwards(segments.get(segCount - 2 - i).vMax, segments.get(segCount - i).velocityStartBackwards);
        }

        segments.get(segCount - 1).developForwards(segments.get(segCount - 2).velocityEndForwards, vEnd);
        segments.get(0).developBackwards(vStart, segments.get(1).velocityStartBackwards);

        //TODO: use filter
    }

    public MotionProfile2D generateProfile() {
        //TODO: check that all stuff happen in ChassisProfiler2D generateProfile are happening here somewhere
        double t = 0;
        MotionProfile1D angular = new MotionProfile1D();
        MotionProfile1D linear = new MotionProfile1D();
        TwoTuple<MotionProfile1D.Segment, MotionProfile1D.Segment> segs;

        double startAngle = 0;
        int i = 0;

        for (DiscreteVelocityGraphLC.Segment s : segments) {
            segs = s.toSegment(t, startAngle);
            t = segs.getFirst().tEnd;
            angular.unsafeAddSegment(segs.getFirst());
            linear.unsafeAddSegment(segs.getSecond());

            startAngle += segs.getFirst().getStartLocation();   
        }
        return new MotionProfile2D(angular, linear);
    }

    class Segment{

        /**
         * The interpolator converting current velocity to maximum acceleration.
         * linear isn't the best possible conversion, but it's pretty good.
         */
        public final AccelerationInterpolator linearInterpolator =
                (currentVelocity, maximumAsymptoticVelocity, maximumInitialAccel)
                        -> -(maximumInitialAccel / maximumAsymptoticVelocity) * currentVelocity + maximumInitialAccel;

        //all velocities are linear velocities, which means (R + L)/2
        public double velocityStartForwards;
        public double velocityEndForwards;
        public double velocityStartBackwards;
        public double velocityEndBackwards;

        public double vMax;
        public double vMaxRaw;
        public double aMax;

        public double curvatureStart;
        public double curvatureEnd;
        //public double curvatureStartBar;
        //public double curvatureEndBar;
        public double curvature;
        //public double curvatureBar;
        //we don't need the "bar" because curvature is the ratio.

        public double distanceStart, distanceEnd;
        public double dx; // the distance between start and end.

        /**
         * Constructor
         * @param distanceStart the distance of the segment's start point from the zero point.
         * @param distanceEnd the distance of the segment's end point from the zero point.
         * @param curvatureStart the curvature at the start point of the segment.
         * @param curvatureEnd the curvature at the end point of the segment.
         * @param vMax the maximum linear velocity available at the whole segment considering the curvature.
         */
        public Segment(double distanceStart, double distanceEnd, double curvatureStart, double curvatureEnd,
                       double vMax, double aMax) {

            this.curvatureStart = curvatureStart;
            this.curvatureEnd = curvatureEnd;
            //curvatureStartBar = convertKappa(curvatureStart);
            //curvatureEndBar = convertKappa(curvatureEnd);
            curvature = (curvatureStart + curvatureEnd) * 0.5; // Rough approximation of the curvature of the whole segment
            //curvatureBar = convertKappa(curvature);
            this.distanceStart = distanceStart;
            this.distanceEnd = distanceEnd;
            dx = distanceEnd - distanceStart;
            this.vMax = vMax;
            ; // Either the right wheel is faster (then vMax = maximumVel) or the left wheel is faster (then vMax = maximumVel / phi(curvatureEndBar))
            vMaxRaw = this.vMax;
        }

        //TODO: no maxVBar diamond graph

        /**
         * @param startV the velocity this segment should start from = the velocity that the previous segment was ended at
         * @param endVMax the max velocity we can reach considering the max velocity of the next segment
         */
        public void developForwards(double startV, double endVMax) {
            velocityStartForwards = startV;


            double withTheGrainAccel = linearInterpolator.getRealMaxAccel(velocityStartForwards, vMaxRaw, aMax);

            velocityEndForwards = Math.min(vMax,
                    Math.sqrt(velocityStartForwards * velocityStartForwards + 2 * (distanceEnd - distanceStart) * withTheGrainAccel));

            velocityEndForwards = Math.min(velocityEndForwards, endVMax);
        }

        /**
          * @param startVMax the max velocity we can start considering the max velocity of the previous segment
         * @param endV the velocity this segment should end at = the velocity that the next segment will start at
         */
        public void developBackwards(double startVMax, double endV) {
            velocityEndBackwards = endV;

            double actAcc = linearInterpolator.getRealMaxAccel(-velocityEndBackwards, vMaxRaw, aMax);

            velocityStartBackwards = Math.min(vMax,
                    Math.sqrt(velocityEndBackwards * velocityEndBackwards + 2 * (distanceEnd - distanceStart) * actAcc));
            velocityStartBackwards = Math.min(velocityStartBackwards, startVMax);

        }

        //go to line 17
        public void filter(List<WheelBasedVelocityGraph.Segment> segs, int subject, int tailSize) {
            int start = Math.max(subject - tailSize, 0);
            int end = Math.min(subject + tailSize, segs.size() - 1);
            double val;
            if (latestFilterIndex != -1 && latestFilterTail == tailSize) {

                val = latestFilterValue;
                if (start != 0)
                    val -= segs.get(start - 1).vMaxRaw;
                if (subject + tailSize <= segs.size() - 1)
                    val += segs.get(end).vMaxRaw;

            } else {

                val = 0.0;
                for (int i = start; i <= end; i++) {
                    val += segs.get(i).vMaxRaw;
                }

            }
            latestFilterValue = val;
            this.vMax = Math.min(val / (end - start + 1), this.vMaxRaw);
        }

        public TwoTuple<MotionProfile1D.Segment, MotionProfile1D.Segment> toSegment(double tStart, double startAngle) {
            double startV = Math.min(velocityStartForwards, velocityStartBackwards);
            double endV = velocityStartForwards <= velocityStartBackwards ? velocityEndForwards : velocityEndBackwards;
            double startW = curvature * startV;
            double endW = curvature * endV;
            double dt = dx / (0.5 * (startV + endV));


            //TODO: check the MotionProfile1D.Segment constructor
            MotionProfile1D.Segment linear = new MotionProfile1D.Segment(
                    tStart,
                    tStart + dt,
                    (endV - startV) / dt,
                    startV,
                    distanceStart);
            MotionProfile1D.Segment angular = new MotionProfile1D.Segment(
                    tStart,
                    tStart + dt,
                    (endW - startW) / dt,
                    startW,
                    startAngle);
            return new TwoTuple<>(angular, linear); // TODO: check in what order we should return
        }
    }
}

