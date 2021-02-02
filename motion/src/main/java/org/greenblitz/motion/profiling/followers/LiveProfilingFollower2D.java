package org.greenblitz.motion.profiling.followers;

import org.greenblitz.motion.Localizer;
import org.greenblitz.motion.base.Position;
import org.greenblitz.motion.base.State;
import org.greenblitz.motion.profiling.MotionProfile2D;
import org.greenblitz.motion.profiling.ReturnProfiler2D;
import org.greenblitz.utils.LinkedList;

public class LiveProfilingFollower2D {
    private double destinationTimeOffset;
    private double epsilon;
    private double kX;
    private double kY;
    private double kAngle;
    private MotionProfile2D profile;

    public LiveProfilingFollower2D(MotionProfile2D profile, double epsilon, double kX, double kY,
                                   double kAngle, double destinationTimeOffset){
        this.profile = profile;
        this.epsilon = epsilon;
        this.kX = kX;
        this.kY = kY;
        this.kAngle = kAngle;
        this.destinationTimeOffset = destinationTimeOffset;
    }

    public LiveProfilingFollower2D(MotionProfile2D profile, double epsilon, double kX, double kY,
                                   double kAngle){
        this(profile,epsilon,kX,kY,kAngle,2000);
    }

    public void updateProfile(double currT, double maxLinearVel, double maxAngularVel,
                              double maxLinearAcc, double maxAngularAcc, double tForCurve,
                              double linearVelocity, double angularVelocity){
        if(this.calcError(currT, linearVelocity, angularVelocity) > epsilon){
            profile = generateNewProfile(maxLinearVel,maxAngularVel, maxLinearAcc,
                    maxAngularAcc, tForCurve,linearVelocity,angularVelocity);
        }
    }

    private double calcError(double currT, double linearVelocity, double angularVelocity){
        State state = new State(getLocation(), linearVelocity, angularVelocity);
        double currX = state.getX();
        double currY = state.getY();
        double currAngle = state.getAngle();

        MotionProfile2D.Segment2D currSegment = profile.quickGetSegment(currT);
        double targetX = currSegment.getStateLocation().getX();
        double targetY = currSegment.getStateLocation().getY();
        double targetAngle = currSegment.getStateLocation().getAngle();

        return kX * (targetX - currX) + kY * (targetY - currY) + kAngle * (targetAngle - currAngle);
    }

    private MotionProfile2D generateNewProfile(double maxLinearVel, double maxAngularVel, double maxLinearAcc,
                                               double maxAngularAcc, double tForCurve, double linearVelocity,
                                               double angularVelocity){
        long t = System.currentTimeMillis();
        int indexOfMergeSegment = profile.quickGetIndex(t+destinationTimeOffset);
        LinkedList.Node<MotionProfile2D.Segment2D> mergeSegmentNode = profile.quickGetNode(t+destinationTimeOffset);
        return ReturnProfiler2D.generateProfile(
                profile, new State(getLocation(), linearVelocity, angularVelocity) , indexOfMergeSegment,
                mergeSegmentNode, 0.01, System.currentTimeMillis(), maxLinearVel, maxAngularVel,
                maxLinearAcc, maxAngularAcc, tForCurve, 4);
    }

    private static Position getLocation(){
        Localizer localizer = Localizer.getInstance();
        return localizer.getLocation(); //TODO check if getLocation() or getLocationRaw()
    }
}
