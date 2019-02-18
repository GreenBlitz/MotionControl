package org.greenblitz.motion.profiling;

import edu.wpi.first.wpilibj.command.WaitCommand;
import org.greenblitz.motion.base.TwoTuple;
import org.greenblitz.motion.profiling.exceptions.NotEnoughAcceleratingSpace;
import org.greenblitz.motion.profiling.exceptions.ProfilingException;

import java.util.ArrayList;
import java.util.List;

/**
 * this is class of one function no documentation
 *
 * @author Alexey ~ Savioor
 */
public class Profiler1D {

    /**
     * generates the quickest motion brofile going through all the waypoints at the specified velocities.
     *
     * @param waypoints
     * @param maxV maximum velocity
     * @param maxAcc maximum acceleration, used to accelerate
     * @param minAcc minimum acceleration, used to decelerate
     * @return the motion brofile
     * @throws ProfilingException Profiling isn't always possible. When so this exceptions is thrown.
     */
    public static MotionProfile1D generateProfile(List<ActuatorLocation> waypoints,
                                                  double maxV, double maxAcc, double minAcc) {
        return Profiler1D.generateProfile(waypoints, maxV, maxAcc, minAcc, 0);
    }

    /**
     * generates the quickest motion brofile going through all the waypoints at the specified velocities.
     *
     * @param waypoints
     * @param maxV maximum velocity
     * @param maxAcc maximum acceleration, used to accelerate
     * @param minAcc minimum acceleration, used to decelerate
     * @param tStart the time this profile starts at
     * @return the motion brofile
     * @throws ProfilingException Profiling isn't always possible. When so this exceptions is thrown.
     */
    public static MotionProfile1D generateProfile(List<ActuatorLocation> waypoints,
                                                  double maxV, double maxAcc, double minAcc, double tStart) {
        if (Math.signum(minAcc) == Math.signum(maxAcc))
            throw new ProfilingException("Sign of max speedup and max slowdown can't be the same.");
        if (maxV == 0 || minAcc == 0 || maxAcc == 0)
            throw new ProfilingException("One of the actuator constants is 0 but isn't allowed to be.");
        if (waypoints.size() > 0 && Math.abs(waypoints.get(0).getV()) > Math.abs(maxV))
            throw new ProfilingException("Can't accelerate past +-" + maxV + "m/s. " + waypoints.get(0).getV() + "m/s was given on point 0");

        double v1, v2, S, a1, a2, t1, t2, root, sum, denominator, t0, underRoot,
                intersectionOne, intersectionTwo, areaLost, timeToAdd, midSecStart, midSecEnd, lastSecEnd;
        List<MotionProfile1D.Segment> segments = new ArrayList<>();
        for (int i = 0; i < waypoints.size() - 1; i++) {


            ActuatorLocation curr = waypoints.get(i);
            ActuatorLocation next = waypoints.get(i + 1);
            v1 = curr.getV();
            v2 = next.getV();
            S = next.x - curr.x;
            if (S > 0){
                a1 = maxAcc;
                a2 = minAcc;
            } else {
                a1 = minAcc;
                a2 = maxAcc;
            }

            if (Math.abs(v2) > Math.abs(maxV))
                throw new ProfilingException("Can't accelerate past +-" + maxV + "m/s. " + v2 + "m/s was given on point " + (i + 1));

            double minTime = Math.abs((v2 - v1)/a1);
            double minDistPass = minTime*v1 + 0.5*a1*minTime*minTime;
            if (Math.abs(minDistPass) > Math.abs(S) && Math.signum(minDistPass) == Math.signum(S))
                throw new NotEnoughAcceleratingSpace("Not enough space to accelerate, minimum "
                + minDistPass + "m required. Occurred when profiling between point " + i + " and point " + (i + 1) + "."
                );

            TwoTuple<Double, Double> triangleTimes = getTriangleProfileTimes(a1, a2, S, v1, v2, i);
            t1 = triangleTimes.getFirst();
            t2 = triangleTimes.getSecond();

            double[] trapezoidalSegments = flatterTriangleProfileToTrapezoid(t1, t2, maxV, v1, v2, a1, a2);

            t0 = i == 0 ? tStart : segments.get(segments.size() - 1).tEnd;

            addSegmentByTimes(segments, trapezoidalSegments[0], trapezoidalSegments[1], trapezoidalSegments[2], t0, t1, t2, a1, a2, curr);

        }
        MotionProfile1D ret = new MotionProfile1D(segments);
        ret.removeBugSegments();
        return ret;
    }

    /**
     *
     * @param a1 maxA in first segment
     * @param a2 maxA in second segment
     * @param S Area (distance) to cover
     * @param v1 V in the start
     * @param v2 V in the end
     * @param i index of points for debugging
     * @return A TwoTuple with the first element being the first segment time and the second the second segment time
     */
    public static TwoTuple<Double, Double> getTriangleProfileTimes(double a1, double a2, double S, double v1, double v2, double i){
        double underRoot = (a2 - a1)*(2*a1*a2*S + a2*v1*v1 - a1*v2*v2);
        if (underRoot < 0)
            throw new ProfilingException("Path not calculable, root is negative between point "
                    + i + " and " + (i + 1) + ".");
        double root = Math.sqrt(underRoot);
        double sum = -a1*v2 + a2*v2;
        double denominator = a2*(a2 - a1);

        double t2 = Math.max((sum + root)/denominator, (sum - root)/denominator);
        double t1 = (v2 - a2*t2 - v1)/a1;

        if (t1 < 0 || t2 < 0)
            throw new ProfilingException("Path entered not valid for unknown reason. " +
                    "Occurred when profiling between point " + i + " and point " + (i + 1) + ".");

        return new TwoTuple<>(t1, t2);
    }

    public static void addSegmentByTimes(List<MotionProfile1D.Segment> segments, double midSecStart, double midSecEnd, double lastSecEnd,
                                         double t0, double t1, double t2, double a1, double a2, ActuatorLocation curr) {
        if (midSecStart == midSecEnd) {
            MotionProfile1D.Segment first = new MotionProfile1D.Segment(
                    t0,
                    t0 + t1,
                    a1,
                    curr.v,
                    curr.x
            );
            MotionProfile1D.Segment last = new MotionProfile1D.Segment(
                    t0 + t1,
                    t0 + t1 + t2,
                    a2,
                    curr.v + a1 * t1,
                    curr.x + curr.v * t1 + 0.5 * a1 * t1 * t1
            );
            segments.add(first);
            segments.add(last);
        } else {
            MotionProfile1D.Segment first = new MotionProfile1D.Segment(
                    t0,
                    t0 + midSecStart,
                    a1,
                    curr.v,
                    curr.x
            );
            MotionProfile1D.Segment middle = new MotionProfile1D.Segment(
                    t0 + midSecStart,
                    t0 + midSecEnd,
                    0,
                    curr.v + midSecStart * a1,
                    curr.x + curr.v * midSecStart + 0.5 * a1 * midSecStart * midSecStart
            );
            MotionProfile1D.Segment last = new MotionProfile1D.Segment(
                    t0 + midSecEnd,
                    t0 + lastSecEnd,
                    a2,
                    curr.v + midSecStart * a1,
                    curr.x + curr.v * midSecStart + 0.5 * a1 * midSecStart * midSecStart
                            + (midSecEnd - midSecStart) * (curr.v + midSecStart * a1)
            );
            segments.add(first);
            segments.add(middle);
            segments.add(last);
        }
    }

    /**
     *
     * @param t1 first element from getTriangleProfileTimes()
     * @param t2 second element form getTriangleProfileTimes()
     * @param maxV maximum velocity
     * @param v1 V in the start
     * @param v2 V in the end
     * @param a1 maxA in first segment
     * @param a2 maxA in second segment
     * @return [end of first segment, end of second segment, end of last segment]
     */
    public static double[] flatterTriangleProfileToTrapezoid(
            double t1, double t2, double maxV, double v1, double v2, double a1, double a2
    ){
        double midSecStart = t1;
        double midSecEnd = t1;
        double lastSecEnd = t1 + t2;

        double intersectionOne = (maxV - v1)/a1;
        double intersectionTwo = (maxV + a2*(t1 + t2) - v2)/a2;
        if (intersectionOne < t1 && intersectionOne > 0) {
            double areaLost = 0.5 * (intersectionTwo - intersectionOne) * (a1 * t1 + v1 - maxV);
            double timeToAdd = areaLost / maxV;

            midSecStart = intersectionOne;
            midSecEnd = intersectionTwo + timeToAdd;
            lastSecEnd = t1 + t2 + timeToAdd;
        }

        intersectionOne = (-maxV - v1)/a1;
        intersectionTwo = (-maxV + a2*(t1 + t2) - v2)/a2;
        if (intersectionOne < t1 && intersectionOne > 0) {
            double areaLost = 0.5 * (intersectionTwo - intersectionOne) * (a1 * t1 + v1 + maxV);
            double timeToAdd = areaLost / (-maxV);

            midSecStart = intersectionOne;
            midSecEnd = intersectionTwo + timeToAdd;
            lastSecEnd = t1 + t2 + timeToAdd;
        }

        return new double[] {midSecStart, midSecEnd, lastSecEnd };
    }

}
