package org.greenblitz.motion.profiling.followers;

import org.greenblitz.motion.Localizer;
import org.greenblitz.motion.base.Position;
import org.greenblitz.motion.base.State;
import org.greenblitz.motion.profiling.MotionProfile2D;

public class LiveProfilingFollower2D {















    public static void updateProfile(){

    }

    private static double calcError(MotionProfile2D profile, double currT, double linearVelocity,
                                    double angularVelocity, double kX, double kY, double kAngle){
        State state = getLocation(linearVelocity, angularVelocity);
        double currX = state.getX();
        double currY = state.getY();
        double currAngle = state.getAngle();

        MotionProfile2D.Segment2D currSegment = profile.quickGetSegment(currT);
        double targetX = currSegment.getStateLocation().getX();
        double targetY = currSegment.getStateLocation().getY();
        double targetAngle = currSegment.getStateLocation().getAngle();

        return kX * (targetX  - currX) + kY * (targetY - currY) + kAngle * (targetAngle - currAngle);
    }

    private static MotionProfile2D generateNewProfile(){

    }

    private static State getLocation(double linearVelocity, double angularVelocity){
        Localizer localizer = Localizer.getInstance();
        Position pos = localizer.getLocation(); //TODO check if getLocation() or getLocationRaw()
        return new State(pos, linearVelocity, angularVelocity);
    }
}
