package org.greenblitz.motion.profiling.followers;

import org.greenblitz.motion.Localizer;
import org.greenblitz.motion.base.Position;
import org.greenblitz.motion.base.State;
import org.greenblitz.motion.profiling.MotionProfile2D;
import org.greenblitz.motion.profiling.ReturnProfiler2D;
import org.greenblitz.utils.LinkedList;

public class LiveProfilingFollower2D {















    public static void updateProfile(){

    }

    private static double calcError(MotionProfile2D profile, double currT, double linearVelocity,
                                    double angularVelocity, double kX, double kY, double kAngle){
        State state = new State(getLocation(), linearVelocity, angularVelocity);
        double currX = state.getX();
        double currY = state.getY();
        double currAngle = state.getAngle();

        MotionProfile2D.Segment2D currSegment = profile.quickGetSegment(currT);
        double targetX = currSegment.getStateLocation().getX();
        double targetY = currSegment.getStateLocation().getY();
        double targetAngle = currSegment.getStateLocation().getAngle();

        return kX * (targetX  - currX) + kY * (targetY - currY) + kAngle * (targetAngle - currAngle);
    }

    private static MotionProfile2D generateNewProfile(MotionProfile2D mainProfile, double maxLinearVel, double maxAngularVel,
                                                      double maxLinearAcc, double maxAngularAcc, double tForCurve, double linearVelocity, double angularVelocity){
        long  t = System.currentTimeMillis();
        int indexOfMergeSegment = mainProfile.quickGetIndex(t+2000);
        LinkedList.Node<MotionProfile2D.Segment2D> mergeSegmentNode = mainProfile.quickGetNode(t+2000);
        return ReturnProfiler2D.generateProfile(
                mainProfile, new State(getLocation(), linearVelocity, angularVelocity) , indexOfMergeSegment,
                mergeSegmentNode, 0.01, System.currentTimeMillis(), maxLinearVel, maxAngularVel,
                maxLinearAcc, maxAngularAcc, tForCurve, 4);
    }

    private static Position getLocation(){
        Localizer localizer = Localizer.getInstance();
        return localizer.getLocation(); //TODO check if getLocation() or getLocationRaw()

    }


}
