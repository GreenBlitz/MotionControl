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
    public double AbsoluteMaXV, AbsoluteMaXW;
    public double AbsoluteMaXLinA, AbsoluteMaXAngA;

    /**
     *Constructor
     * @param track The list of the curves we would like to drive on.
     * @param vStart the velocity that the whole route starts from = the vStart of the first segment.
     * @param vEnd the velocity that the whole route ends at = the vEnd of the last segment.
     * @param maxV the maximum linear velocity the robot can reach ignoring the curvature.
     * @param maxW the maximum angular velocity the robot can reach ignoring the linear velocity.
     * @param maxAccLin the maximum linear acceleration the robot can reach ignoring the curvature.
     * @param maxAccAng the maximum angular acceleration the robot can reach ignoring the linear velocity.
     * @param tailSize I don't know yet what is the meaning of this variable, sorry :(
     */
    public DiscreteVelocityGraphLC(List<ICurve> track, double vStart, double vEnd, double maxV, double maxW,
                                   double maxAccLin, double maxAccAng, int tailSize) {

        AbsoluteMaXV = maxV;
        AbsoluteMaXW = maxW;
        AbsoluteMaXLinA = maxAccLin;
        AbsoluteMaXAngA = maxAccAng;

        double tmpLength = 0; //The distance from the beginning to the current segment

        segments = new ArrayList<>();
        double curveLen; //The length of the current curve
        double curvatureStart;
        double curvatureEnd;
        double curvature;

        //creating the segments for all the curves in the track.
        for (ICurve curve : track) {
            curveLen = curve.getLength(1);
            curvatureStart = curve.getCurvature(0);
            curvatureEnd = curve.getCurvature(1);
            curvature = curve.getCurvature();
            segments.add(new DiscreteVelocityGraphLC.Segment(tmpLength, tmpLength + curveLen,
                    curvatureStart, curvatureEnd,
                    ChassisProfiler2D.getMaxVelocity(AbsoluteMaXV, AbsoluteMaXW, curvature),
                    ChassisProfiler2D.getMaxAcceleration(AbsoluteMaXLinA, AbsoluteMaXAngA, curvature))
            );
            tmpLength += curveLen;
        }

        //filtering
        int segCount = segments.size();
        latestFilterTail = tailSize;
        for (int i = 1; i < segCount - 1; i++) {
            segments.get(i).filter(segments, i, tailSize);
            latestFilterIndex = i;
        }


        //developing the first segment forwards and the last backwards.
        segments.get(0).developForwards(vStart, segments.get(1).vMax);
        segments.get(segCount - 1).developBackwards(segments.get(segCount - 2).vMax, vEnd);

        //developing all of the segments from the second segment to the one before the last.
        for (int i = 1; i < segCount - 1; i++) {
            segments.get(i)
                    .developForwards(segments.get(i - 1).velocityEndForwards, segments.get(i + 1).vMax);

            segments.get(segCount - 1 - i)
                    .developBackwards(segments.get(segCount - 2 - i).vMax, segments.get(segCount - i).velocityStartBackwards);
        }

        //setting the missing start and end variables for the first and the last segments.
        segments.get(segCount - 1).velocityStartForwards = segments.get(segCount - 2).velocityEndForwards;
        segments.get(0).velocityEndBackwards = segments.get(1).velocityStartBackwards;


        //segments.get(segCount - 1).developForwards(segments.get(segCount - 2).velocityEndForwards, vEnd);
        //segments.get(0).developBackwards(vStart, segments.get(1).velocityStartBackwards);


    }

    /**
     * @return a MotionProfile2D of the graph in a format of < angular, linear >.
     */
    public MotionProfile2D generateProfile() {
        double t = 0; //the time
        MotionProfile1D angular = new MotionProfile1D();
        MotionProfile1D linear = new MotionProfile1D();
        TwoTuple<MotionProfile1D.Segment, MotionProfile1D.Segment> segs;

        double startAngle = 0;

        //runs over the graph and creates the profile
        for (DiscreteVelocityGraphLC.Segment s : segments) {
            segs = s.toSegment(t, startAngle);
            t = segs.getFirst().tEnd;
            angular.unsafeAddSegment(segs.getFirst());
            linear.unsafeAddSegment(segs.getSecond());

            MotionProfile1D.Segment ang = segs.getFirst();
            double dt = ang.getTEnd() - ang.getTStart();

            startAngle += ang.getStartLocation() + dt*ang.getStartVelocity() + dt*dt*ang.getAccel()*0.5;
            // x = x0 + v0t + 0.5at^2
        }


        System.out.println("Angular\n" + angular.toString());
        System.out.println("Linear\n" + linear.toString());

        return new MotionProfile2D(angular, linear);
    }


    class Segment{

        /**
         * The interpolator converting current velocity to maximum acceleration.
         * Linear isn't the best possible conversion, but it's pretty good.
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

        /**
         * If the maxV of the current segment is above average in the range of the tail size, it makes it the average.
         * @param segs the list of the segments.
         * @param currentIndex the index of the segment we are filtering around.
         * @param tailSize the range of the segments we would like to filter.
         */
        public void filter(List<DiscreteVelocityGraphLC.Segment> segs, int currentIndex, int tailSize) {
            //calculating the index of the start and the end segments.
            int start = Math.max(currentIndex - tailSize, 0);
            int end = Math.min(currentIndex + tailSize, segs.size() - 1);
            double sum; //sum of maxVs.
            if (latestFilterIndex != -1 && latestFilterTail == tailSize) { //if you had already filtered and hadn't
                // changed the tailSize, it means that the sum is the same as last time, minus the maxV of the segment
                // that is now out of range plus the maxV of the segment that is now in range.

                sum = latestFilterValue;
                if (start != 0)
                    sum -= segs.get(start - 1).vMaxRaw;
                if (currentIndex + tailSize <= segs.size() - 1)
                    sum += segs.get(end).vMaxRaw;

            } else { //in  general case, it sums up all the max velocities.

                sum = 0.0;
                for (int i = start; i <= end; i++) {
                    sum += segs.get(i).vMaxRaw;
                }

            }
            latestFilterValue = sum;
            this.vMax = Math.min(sum / (end - start + 1), this.vMaxRaw); // calculate the average.
        }

        /**
         * takes the information from the segment and puts it in MotionProfile1D.Segment
         * @param tStart the time the segment starts at.
         * @param startAngle the angle that the segment starts at.
         * @return
         */
        public TwoTuple<MotionProfile1D.Segment, MotionProfile1D.Segment> toSegment(double tStart, double startAngle) {
            //calculations for the the angular segment because we are deriving the angular segment *from* the linear one.
            double startV = Math.min(velocityStartForwards, velocityStartBackwards);
            double endV = velocityStartForwards <= velocityStartBackwards ? velocityEndForwards : velocityEndBackwards;
            double startW = curvature * startV;
            double endW = curvature * endV;
            double dt = dx / (0.5 * (startV + endV));


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
            return new TwoTuple<>(angular, linear);
        }
    }
}

