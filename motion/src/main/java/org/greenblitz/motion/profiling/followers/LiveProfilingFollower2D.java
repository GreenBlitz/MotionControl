package org.greenblitz.motion.profiling.followers;

import org.greenblitz.motion.Localizer;
import org.greenblitz.motion.base.Position;
import org.greenblitz.motion.base.State;
import org.greenblitz.motion.base.Vector2D;
import org.greenblitz.motion.profiling.MotionProfile2D;
import org.greenblitz.motion.profiling.ReturnProfiler2D;
import org.greenblitz.utils.LinkedList;

public class LiveProfilingFollower2D extends AbstractFollower2D {
    private double destinationTimeOffset;
    private double epsilon;
    private double kX;
    private double kY;
    private double kAngle;
    private double maxLinearVel;
    private double maxAngularVel;
    private double maxLinearAcc;
    private double maxAngularAcc;
    private MotionProfile2D profile;
    private PidFollower2D follower;
    private long startTime;

    public LiveProfilingFollower2D(MotionProfile2D profile, double epsilon, double kX, double kY,
                                   double kAngle, double maxLinearVel, double maxAngularVel,
                                   double maxLinearAcc, double maxAngularAcc, double destinationTimeOffset){
        this.profile = profile;
        this.epsilon = epsilon;
        this.kX = kX;
        this.kY = kY;
        this.kAngle = kAngle;
        this.maxLinearVel = maxLinearVel;
        this.maxAngularVel = maxAngularVel;
        this.maxLinearAcc = maxLinearAcc;
        this.maxAngularAcc = maxAngularAcc;
        this.destinationTimeOffset = destinationTimeOffset;
    }

    public LiveProfilingFollower2D(MotionProfile2D profile, double epsilon, double kX, double kY,
                                   double kAngle, double maxLinearVel, double maxAngularVel,
                                   double maxLinearAcc, double maxAngularAcc){
        this(profile,epsilon,kX,kY,kAngle,2000,maxLinearVel,maxAngularVel,maxLinearAcc,maxAngularAcc);
    }


    @Override
    public void init() {
        startTime = System.currentTimeMillis();
    }

    @Override
    public Vector2D forceRun(double leftCurr, double rightCurr, double angularVel, double timeNow) {
        return null;
    }





    public void updateProfile(double tForCurve, double linearVelocity, double angularVelocity){
        if(this.calcError(linearVelocity, angularVelocity) > epsilon){
            profile = generateNewProfile(tForCurve,linearVelocity,angularVelocity);
        }
    }

    private double calcError(double linearVelocity, double angularVelocity){
        State state = new State(getLocation(), linearVelocity, angularVelocity);
        double currX = state.getX();
        double currY = state.getY();
        double currAngle = state.getAngle();

        Position currPosition = profile.getActualLocation(System.currentTimeMillis()-startTime);
        double targetX = currPosition.getX();
        double targetY = currPosition.getY();
        double targetAngle = currPosition.getAngle();

        return kX * (targetX - currX) + kY * (targetY - currY) + kAngle * (targetAngle - currAngle);
    }

    private MotionProfile2D generateNewProfile(double tForCurve, double linearVelocity, double angularVelocity){
        int t = (int) (System.currentTimeMillis() - startTime);
        int indexOfMergeSegment = profile.quickGetIndex(t+destinationTimeOffset);
        LinkedList.Node<MotionProfile2D.Segment2D> mergeSegmentNode = profile.quickGetNode(t+destinationTimeOffset);
        return ReturnProfiler2D.generateProfile(
                profile, new State(getLocation().translate(profile.getJahanaRelation().negate()), linearVelocity, angularVelocity) //TODO test if negate needed
                , indexOfMergeSegment, mergeSegmentNode, 0.01, System.currentTimeMillis(), maxLinearVel, maxAngularVel,
                maxLinearAcc, maxAngularAcc, tForCurve, 4);
    }

    private static Position getLocation(){
        Localizer localizer = Localizer.getInstance();
        return localizer.getLocation(); //TODO check if getLocation() or getLocationRaw()
    }


}
