package org.greenblitz.motion.profiling;

import org.greenblitz.motion.base.TwoTuple;
import org.greenblitz.motion.profiling.curve.ICurve;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Some variable names are the names of the mathematical symbols I used while developing this. sorry.
 *
 * @author Alexey
 */
class WheelBasedVelocityGraph {

    protected double wheelBaseLength;
    protected double maxVBar;
    protected double maxABar;

    private int latestFilterIndex = -1;
    private int latestFilterTail;
    private double latestFilterValue = 0;

    protected List<Segment> segments;

    /**
     *
     * @param track
     * @param vStart
     * @param vEnd
     * @param maxVel
     * @param maxAcc
     * @param wheelBaseL
     * @param tailSize
     */
    public WheelBasedVelocityGraph(List<ICurve> track, double vStart, double vEnd, double maxVel,
                                 double maxAcc, double wheelBaseL, int tailSize) {

        maxVBar = maxVel;
        maxABar = maxAcc;
        wheelBaseLength = wheelBaseL;

        double tmpLength = 0;

        segments = new ArrayList<>();
        double curveLen;
        double curvatureStart;
        double curvatureEnd;

        for (ICurve curve : track) {
            curveLen = curve.getLength(1);
            curvatureStart = curve.getCurvature(0);
            curvatureEnd = curve.getCurvature(1);
            segments.add(new Segment(tmpLength, tmpLength + curveLen,
                    curvatureStart, curvatureEnd, maxVel)
            );
            tmpLength += curveLen;
        }

        int segCount = segments.size();

        // TODO since every point has the same maxVel, this does nothing
//        latestFilterTail = tailSize;
//        for (int i = 1; i < segCount - 1; i++) {
//            segments.get(i).filter(segments, i, tailSize);
//            latestFilterIndex = i;
//        }

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

    }


    public MotionProfile2D generateProfile() {

        double t = 0;
        MotionProfile1D left = new MotionProfile1D();
        MotionProfile1D right = new MotionProfile1D();
        TwoTuple<MotionProfile1D.Segment, MotionProfile1D.Segment> segs;

        for (Segment s : segments) {
            segs = s.toSegment(t);
            t = segs.getFirst().tEnd;
            left.unsafeAddSegment(segs.getFirst());
            right.unsafeAddSegment(segs.getSecond());
        }
        return new MotionProfile2D(left, right);
    }

    class Segment {

        /**
         * The interpolator converting current velocity to maximum acceleration.
         * linear isn't the best possible conversion, but it's pretty good.
         */
        public final AccelerationInterpolator psi =
                (currentVelocity, maximumAsymptoticVelocity, maximumInitialAccel)
                        -> -(maximumInitialAccel / maximumAsymptoticVelocity) * currentVelocity + maximumInitialAccel;

        // All velocities are of the right wheel
        public double velocityStartForwards;
        public double velocityEndForwards;
        public double velocityStartBackwards;
        public double velocityEndBackwards;

        public double vMax;
        public double vMaxRaw;

        public double curvatureStart;
        public double curvatureEnd;
        public double curvatureStartBar;
        public double curvatureEndBar;
        public double curvature;
        public double curvatureBar;

        public double distanceStart;
        public double distanceEnd;
        public double dx;

        /**
         *
         * We have: omega/Lv = kappa.
         * Where:
         * omega = angular velocity
         * Lv = linear velocity
         * kappa = curvature
         *
         * Substituting left wheel and right wheel velocities as l, r respectively we get:
         *
         * ((r - l)/d) / ((r + l)/2) = [(r - l)/(r + l)]*[2/d] = kappa
         *
         * Where d = the distance between the wheels
         * Thus we get:
         * (r - l)/(r + l) = (d / 2) * kappa
         *
         * We define kappaBar = (d / 2) * kappa
         *
         * @param kappa some curvature
         * @return this curvature normalized for the robot
         */
        public double convertKappa(double kappa){
            return kappa * wheelBaseLength * 0.5;
        }

        /**
         * We have:
         * (r - l)/(r + l) = kappaBar
         * So:
         * r - l = r * kappaBar + l * kappaBar
         * l * (1 + kappaBar) = r * (1 - kappaBar)
         * l = r * (1 - kappaBar)/(1 + kappaBar)
         * l/r = (1 - kappaBar)/(1 + kappaBar)
         *
         *
         * @param x some normalized curvature
         * @return The ration between the left and right wheel velocities (l / r)
         */
        public double phi(double x) {
            return (1 - x) / (1 + x);
        }

        /**
         *
         * @param start start location
         * @param end end location
         * @param crveStart start curvature
         * @param crveEnd end curvature
         * @param maximumVel the maximum allowed velocity in this segment
         */
        public Segment(double start, double end, double crveStart, double crveEnd, double maximumVel) {

            curvatureStart = crveStart;
            curvatureEnd = crveEnd;
            curvatureStartBar = convertKappa(curvatureStart);
            curvatureEndBar = convertKappa(curvatureEnd);
            curvature = (curvatureStart + curvatureEnd) * 0.5; // Rough approximation of the curvature of the whole segment
            curvatureBar = convertKappa(curvature);
            distanceStart = start;
            distanceEnd = end;
            dx = distanceEnd - distanceStart;
            vMax = Math.min(
                    curvatureStart >= 0 ? maximumVel : maximumVel / phi(curvatureStartBar),
                    curvatureEnd >= 0 ? maximumVel : maximumVel / phi(curvatureEndBar)
            ); // Either the right wheel is faster (then vMax = maximumVel) or the left wheel is faster (then vMax = maximumVel / phi(curvatureEndBar))
            vMaxRaw = vMax;

        }

        public void developForwards(double velocityStart, double velocityEndMax) {
            velocityStartForwards = velocityStart;

            // a_m = maximum acceleration
            // Calculated by assuming curvature is constant for simplicity.
            // Concept is similar to finding the max velocity. We use here:
            // a_r / a_l = (v_e - v_s) / (u_e - u_s)
            // Where:
            // a_r, a_l = acc for right and left wheel
            // v_e, v_s = end and start vel for right wheel
            // u_e, u_s = end and start vel for left wheel
            double a_m = Math.min(psi.getRealMaxAccel(velocityStartForwards, maxVBar, maxABar),
                    Math.abs(
                            psi.getRealMaxAccel(velocityStartForwards * phi(curvatureStartBar), maxVBar, maxABar) / phi(curvatureBar)));

            // dx_r = distance passed by right wheel.
            // Calculated assuming path is arch. Just draw it and calculate with definitions it's simple
            double dx_r = dx * (1 + 0.5 * curvature * wheelBaseLength);

            // Calculated end velocity by distance. just develop the kinematics it's pretty easy.
            velocityEndForwards = Math.min(Math.min(vMax, velocityEndMax),
                    Math.sqrt(velocityStartForwards*velocityStartForwards + 2 * a_m * dx_r));
        }

        // Note that start and end are by time.
        public void developBackwards(double velocityStartMax, double velocityEnd) {
            // See developForwards for detailed explanation

            velocityEndBackwards = velocityEnd; // v_e is here

            // Step 1: find a_m
            // a_m is decided by an approximation (assumes curvature is constant)
            double a_m = Math.min(psi.getRealMaxAccel(-velocityEndBackwards, maxVBar, maxABar),
                    Math.abs(
                            psi.getRealMaxAccel(-velocityEndBackwards * phi(curvatureEndBar), maxVBar, maxABar) / phi(curvatureBar)));
            // Step 2: v_e
            double dx_r = dx * (1 + 0.5 * curvature * wheelBaseLength);
            velocityStartBackwards = Math.min(Math.min(vMax, velocityStartMax),
                    Math.sqrt(velocityEndBackwards*velocityEndBackwards + 2 * a_m * dx_r));

        }

        public void filter(List<Segment> segs, int subject, int tailSize) {
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

        /**
         *
         * @param tStart The start time of this segment within the general path
         * @return the two profiles for each wheel for this segment (left is first, right is second)
         */
        public TwoTuple<MotionProfile1D.Segment, MotionProfile1D.Segment> toSegment(double tStart) {
            double velStartR = Math.min(velocityStartForwards, velocityStartBackwards);
            double velEndR = velocityStartForwards <= velocityStartBackwards ? velocityEndForwards : velocityEndBackwards;
            double velStartL = velStartR * phi(curvatureStartBar);
            double velEndL = velEndR * phi(curvatureEndBar);
            // For dt, '0.25 * (velStartR + velStartL + velEndR + velEndL)' is the linear velocity (check it).
            double dt = dx / (0.25 * (velStartR + velStartL + velEndR + velEndL));
            MotionProfile1D.Segment right = new MotionProfile1D.Segment(
                    tStart,
                    tStart + dt,
                    (velEndR - velStartR) / dt,
                    velStartR,
                    velEndR);
            MotionProfile1D.Segment left = new MotionProfile1D.Segment(
                    tStart,
                    tStart + dt,
                    (velEndL - velStartL) / dt,
                    velStartL,
                    velEndL);
            return new TwoTuple<>(left, right);
        }


    }

}
