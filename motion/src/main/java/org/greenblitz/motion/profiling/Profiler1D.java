package org.greenblitz.motion.profiling;

import edu.wpi.first.wpilibj.command.WaitCommand;
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
                                                  double maxV, double maxAcc, double minAcc){
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

            underRoot = (a2 - a1)*(2*a1*a2*S + a2*v1*v1 - a1*v2*v2);
            if (underRoot < 0)
                throw new ProfilingException("Path not calculable, root is negative between point "
                        + i + " and " + (i + 1) + ".");
            root = Math.sqrt(underRoot);
            sum = -a1*v2 + a2*v2;
            denominator = a2*(a2 - a1);

            t2 = Math.max((sum + root)/denominator, (sum - root)/denominator);
            t1 = (v2 - a2*t2 - v1)/a1;

            if (t1 < 0 || t2 < 0)
                throw new ProfilingException("Path entered not valid for unknown reason. " +
                        "Occurred when profiling between point " + i + " and point " + (i + 1) + ".");

            midSecStart = t1;
            midSecEnd = t1;
            lastSecEnd = t1 + t2;

            intersectionOne = (maxV - v1)/a1;
            intersectionTwo = (maxV + a2*(t1 + t2) - v2)/a2;
            if (intersectionOne < t1 && intersectionOne > 0) {
                areaLost = 0.5 * (intersectionTwo - intersectionOne) * (a1 * t1 + v1 - maxV);
                timeToAdd = areaLost / maxV;

                midSecStart = intersectionOne;
                midSecEnd = intersectionTwo + timeToAdd;
                lastSecEnd = t1 + t2 + timeToAdd;
            }

            intersectionOne = (-maxV - v1)/a1;
            intersectionTwo = (-maxV + a2*(t1 + t2) - v2)/a2;
            if (intersectionOne < t1 && intersectionOne > 0) {
                areaLost = 0.5 * (intersectionTwo - intersectionOne) * (a1 * t1 + v1 + maxV);
                timeToAdd = areaLost / (-maxV);

                midSecStart = intersectionOne;
                midSecEnd = intersectionTwo + timeToAdd;
                lastSecEnd = t1 + t2 + timeToAdd;
            }

            if (midSecEnd < midSecStart || midSecStart < 0 || lastSecEnd < midSecEnd){
                throw new ProfilingException("Some error occurred between point " + i + " and point " + (i + 1) + " when cutting triangle.");
            }

            t0 = i == 0 ? tStart : segments.get(segments.size() - 1).tEnd;

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
                        curr.v + midSecStart*a1,
                        curr.x + curr.v*midSecStart + 0.5*a1*midSecStart*midSecStart
                );
                MotionProfile1D.Segment last = new MotionProfile1D.Segment(
                        t0 + midSecEnd,
                        t0 + lastSecEnd,
                        a2,
                        curr.v + midSecStart*a1,
                        curr.x + curr.v*midSecStart + 0.5*a1*midSecStart*midSecStart
                                + (midSecEnd - midSecStart)*(curr.v + midSecStart*a1)
                );
                segments.add(first);
                segments.add(middle);
                segments.add(last);
            }
        }
        MotionProfile1D ret = new MotionProfile1D(segments);
        ret.removeBugSegments();
        return ret;
    }

}
