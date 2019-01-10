package org.greenblitz.motion.motionprofiling;

import java.util.ArrayList;
import java.util.List;

public class OneDProfiler {

    public static MotionProfile generateProfile(List<ActuatorLocation> points,
                                                double maxV, double maxASpeedup, double maxASlowdown)
    throws PathfinderException {
        double v1, v2, S, a1, a2, t1, t2, root, sum, denominator, t0;
        List<MotionProfile.Segment> segments = new ArrayList<>();
        for (int i = 0; i < points.size() - 1; i++) {
            ActuatorLocation curr = points.get(i);
            ActuatorLocation next = points.get(i + 1);
            v1 = curr.getV();
            v2 = next.getV();
            S = next.x - curr.x;
            if (S > 0){
                a1 = maxASpeedup;
                a2 = maxASlowdown;
            } else {
                a1 = maxASlowdown;
                a2 = maxASpeedup;
            }

            double minTime = Math.abs((v2 - v1)/a1);
            double minDistPass = minTime*v1 + 0.5*a1*minTime*minTime;
            if (Math.abs(minDistPass) > Math.abs(S) && Math.signum(minDistPass) == Math.signum(S))
                throw new PathfinderException("Not enough space to accelerate, minimum "
                + minDistPass + "m required."
                );

            root = Math.sqrt((a2 - a1)*(2*a1*a2*S + a2*v1*v1 - a1*v2*v2));
            sum = -a1*v2 + a2*v2;
            denominator = a2*(a2 - a1);

            t2 = Math.max((sum + root)/denominator, (sum - root)/denominator);
            t1 = (v2 - a2*t2 - v1)/a1;

            if (!(t1 > 0 && t2 > 0))
                throw new PathfinderException("Path entered not valid for some reason");

            t0 = i == 0 ? 0 : segments.get(segments.size() - 1).tStart;

            MotionProfile.Segment first = new MotionProfile.Segment(
                    t0,
                    t0 + t1,
                    a1,
                    curr.v,
                    curr.x
            );
            MotionProfile.Segment last = new MotionProfile.Segment(
                    t0 + t1,
                    t0 + t1 + t2,
                    a2,
                    curr.v + a1*t1,
                    curr.x + curr.v*t1 + 0.5*a1*t1*t1
            );
            segments.add(first);
            segments.add(last);
        }
        return new MotionProfile(segments);
    }

}
