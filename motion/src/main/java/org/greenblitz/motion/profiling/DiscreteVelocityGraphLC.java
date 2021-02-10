package org.greenblitz.motion.profiling;

import java.util.List;

public class DiscreteVelocityGraphLC {

    protected List<Segment> segments;

    //please ask Alexey, and if he explains it to you, tell me too!
    private int latestFilterIndex = -1;
    private int latestFilterTail;
    private double latestFilterValue = 0;



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

        public double acceleration;

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
         * @param acceleration the acceleration
         */
        public Segment(double distanceStart, double distanceEnd, double curvatureStart, double curvatureEnd, double vMax
                , double acceleration) {

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
            this.acceleration = acceleration;
        }

        /**
         * @param startV the velocity this segment should start from = the velocity that the previous segment was ended at
         * @param endVMax the max velocity we can reach considering the max velocity of the next segment
         */
        public void developForwards(double startV, double endVMax) {
            velocityStartForwards = startV;


            double withTheGrainAccel = linearInterpolator.getRealMaxAccel(velocityStartForwards, vMax, acceleration);

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

            double actAcc = linearInterpolator.getRealMaxAccel(-velocityEndBackwards, vMax, acceleration);

            velocityStartBackwards = Math.min(vMax,
                    Math.sqrt(velocityEndBackwards * velocityEndBackwards + 2 * (distanceEnd - distanceStart) * actAcc));
            velocityStartBackwards = Math.min(velocityStartBackwards, startVMax);

        }

        //go to line 9
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
    }
}

