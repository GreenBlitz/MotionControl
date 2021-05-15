package org.greenblitz.motion.profiling;

import org.greenblitz.motion.base.TwoTuple;
import org.greenblitz.motion.base.Vector2D;
import org.greenblitz.motion.profiling.curve.ICurve;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

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

    private static double EPSILON = 0.0000001;

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

        segments = new ArrayList<>(track.size());
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

        segments.get(0).developForwards(new Vector2D(vStart * segments.get(0).phi(segments.get(0).curvatureStartBar, true), vStart));
        segments.get(segCount - 1).developBackwards(new Vector2D(vEnd * segments.get(segCount - 1).phi(segments.get(segCount - 1).curvatureStartBar, true), vEnd));

        for (int i = 1; i < segCount - 1; i++) {
            segments.get(i)
                    .developForwards(segments.get(i - 1).velocityEndForwards);

            segments.get(segCount - 1 - i)
                    .developBackwards(segments.get(segCount - i).velocityStartBackwards);
        }

        segments.get(segCount - 1).developForwards(segments.get(segCount - 2).velocityEndForwards);
        segments.get(0).developBackwards(segments.get(1).velocityStartBackwards);

    }


    public MotionProfile2D generateProfile() {

        double t = 0;
        MotionProfile1D left = new MotionProfile1D();
        MotionProfile1D right = new MotionProfile1D();
        TwoTuple<MotionProfile1D.Segment, MotionProfile1D.Segment> segs;

        double sumOfDxL = 0;
        double sumOfDxR = 0;

        for (Segment s : segments) {
            segs = s.toSegment(t,sumOfDxL,sumOfDxR);
            sumOfDxL += s.dx_l;
            sumOfDxR += s.dx_r;
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
        public Vector2D velocityStartForwards;
        public Vector2D velocityEndForwards;
        public Vector2D velocityStartBackwards;
        public Vector2D velocityEndBackwards;

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

        private double dx_r;
        private double dx_l;

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
            double curvatureBar = kappa * wheelBaseLength * 0.5;
            if(Math.abs(curvatureBar) == 1){
                return curvatureBar * (1 + EPSILON);
            }
            return curvatureBar;
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
         * @param leftOverRight is left over right
         * @return The ration between the left and right wheel velocities (l / r)
         */
        public double phi(double x, boolean leftOverRight) {
            if(leftOverRight)
                return (1 - x) / (1 + x);
            return (1 + x) / (1 - x);
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
            vMax = maximumVel;
            vMaxRaw = vMax;

            // dx_r = distance passed by right wheel, dx_l = distance passed by the left wheel
            // Calculated assuming path is arch. Just draw it and calculate with definitions it's simple
            dx_r = dx * (1 + curvatureBar); //dx * (1 + 0.5 * curvature * wheelBaseLength);
            dx_l = dx * (1 - curvatureBar);
        }

        public void developForwards(Vector2D velocityStart) {
            velocityStartForwards = velocityStart;

            boolean isRightFaster = curvatureEnd > 0;

            double fasterV, slowerV, dxFast, dxSlow;
            if(isRightFaster){
                fasterV = velocityStartForwards.getY();
                slowerV = velocityStartForwards.getX();
                dxFast = dx_r;
                dxSlow = dx_l;
            }else{
                fasterV = velocityStartForwards.getX();
                slowerV = velocityStartForwards.getY();
                dxFast = dx_l;
                dxSlow = dx_r;
            }

            // a_m = maximum acceleration
            double a_m = psi.getRealMaxAccel(fasterV, maxVBar, maxABar);
            // Calculated end velocity by distance. just develop the kinematics it's pretty easy.
            double fasterEndForwards = Math.min(vMax, Math.sqrt(fasterV*fasterV + 2 * a_m * dxFast));

            // Calculate the same thing exactly from the perspective of the left wheel.
            // Needed to make a_m accurate for both wheels.
            double aSlower;
            if(curvatureEndBar > 1 || curvatureEndBar < -1){
                aSlower = -psi.getRealMaxAccel(-slowerV, maxVBar, maxABar);
                fasterEndForwards = Math.min(fasterEndForwards,
                        -Math.sqrt(slowerV*slowerV + 2 * aSlower * dxSlow)/phi(curvatureEndBar, isRightFaster));
            }
            else{
                aSlower = psi.getRealMaxAccel(slowerV, maxVBar, maxABar);
                fasterEndForwards = Math.min(fasterEndForwards,
                        Math.sqrt(slowerV*slowerV + 2 * aSlower * dxSlow)/phi(curvatureEndBar, isRightFaster));
            }


            if(isRightFaster){
                //phi(kappaBar, true) = left / right => left = right * phi(kappaBar, true)
                velocityEndForwards.setX(fasterEndForwards * phi(fasterEndForwards, true));
                velocityEndForwards.setY(fasterEndForwards);
            }else{
                //phi(kappaBar, false) = right / left => right = left * phi(kappaBar, true)
                velocityEndForwards.setX(fasterEndForwards);
                velocityEndForwards.setY(fasterEndForwards * phi(fasterEndForwards, false));
            }
        }

        // Note that start and end are by time.
        public void developBackwards(Vector2D velocityEnd){
            // See developForwards for detailed explanation
            velocityEndBackwards = velocityEnd;

            boolean isRightFaster = curvatureStart > 0;

            double fasterV, slowerV, dxFast, dxSlow;
            if(isRightFaster){
                fasterV = velocityEndBackwards.getY();
                slowerV = velocityEndBackwards.getX();
                dxFast = dx_r;
                dxSlow = dx_l;
            }else{
                fasterV = velocityEndBackwards.getX();
                slowerV = velocityEndBackwards.getY();
                dxFast = dx_l;
                dxSlow = dx_r;
            }

            // Step 1: find a_m
            // a_m is decided by an approximation (assumes curvature is constant)
            double a_m = psi.getRealMaxAccel(-fasterV, maxVBar, maxABar);
            // Step 2: v_e
            double fasterStartBackwards = Math.min(vMax, Math.sqrt(fasterV*fasterV + 2 * a_m * dxFast));

            // calc the same thing from slower wheel prespective
            double aSlower;
            if(curvatureStartBar > 1 || curvatureStartBar < -1){
                aSlower = -psi.getRealMaxAccel(slowerV, maxVBar, maxABar);
                fasterStartBackwards = Math.min(fasterStartBackwards,
                        -Math.sqrt(slowerV*slowerV + 2 * aSlower * dxSlow)/phi(curvatureStartBar, isRightFaster));
            }else{
                aSlower = psi.getRealMaxAccel(-slowerV, maxVBar, maxABar);
                fasterStartBackwards = Math.min(fasterStartBackwards,
                        Math.sqrt(slowerV*slowerV + 2 * aSlower * dxSlow)/phi(curvatureStartBar, isRightFaster));
            }

            if(isRightFaster){
                //phi(kappaBar, true) = left / right => left = right * phi(kappaBar, true)
                velocityStartBackwards.setX(fasterStartBackwards * phi(fasterStartBackwards, true));
                velocityStartBackwards.setY(fasterStartBackwards);
            }else{
                //phi(kappaBar, false) = right / left => right = left * phi(kappaBar, true)
                velocityStartBackwards.setX(fasterStartBackwards);
                velocityStartBackwards.setY(fasterStartBackwards * phi(fasterStartBackwards, false));
            }
        }

        //TODO: check filter for wheel based
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
        public TwoTuple<MotionProfile1D.Segment, MotionProfile1D.Segment> toSegment(double tStart, double sumOfDxL, double sumOfDxR) {
            double velStartR, velStartL;
            if(Math.abs(velocityStartForwards.getY()) > Math.abs(velocityStartBackwards.getY())){
                velStartR = velocityStartBackwards.getY();
                velStartL = velocityStartBackwards.getX();
            }else{
                velStartR = velocityStartForwards.getY();
                velStartL = velocityStartForwards.getX();
            }

            double velEndR, velEndL;
            if(Math.abs(velocityEndForwards.getY()) > Math.abs(velocityEndBackwards.getY())){
                velEndR = velocityEndBackwards.getY();
                velEndL = velocityEndBackwards.getX();
            }else{
                velEndR = velocityEndForwards.getY();
                velEndL = velocityEndForwards.getX();
            }

            // For dt, '0.25 * (velStartR + velStartL + velEndR + velEndL)' is the linear velocity (check it).
            double dt = dx / (0.25 * (velStartR + velStartL + velEndR + velEndL));

            MotionProfile1D.Segment left = new MotionProfile1D.Segment(
                    tStart,
                    tStart + dt,
                    (velEndL - velStartL) / dt,
                    velStartL,
                    sumOfDxL);
            MotionProfile1D.Segment right = new MotionProfile1D.Segment(
                    tStart,
                    tStart + dt,
                    (velEndR - velStartR) / dt,
                    velStartR,
                    sumOfDxR);
            return new TwoTuple<>(left, right);
        }


    }

}
