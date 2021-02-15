package org.greenblitz.motion.profiling.followers;

import org.greenblitz.motion.Localizer;
import org.greenblitz.motion.base.Position;
import org.greenblitz.motion.base.State;
import org.greenblitz.motion.base.Vector2D;
import org.greenblitz.motion.pid.PIDObject;
import org.greenblitz.motion.profiling.MotionProfile2D;
import org.greenblitz.motion.profiling.ReturnProfiler2D;
import org.greenblitz.motion.profiling.ThreadedReturnProfiler;
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
    private double tForCurve;
    private MotionProfile2D profile;
    private AbstractFollower2D follower;
    private long startTime;
    private ThreadedReturnProfiler calculateProfile;
    private long lastUpdate;

    private static final long updateDelay = 500;

    public LiveProfilingFollower2D(MotionProfile2D profile, double epsilon, double kX, double kY,
                                   double kAngle, double maxLinearVel, double maxAngularVel,
                                   double maxLinearAcc, double maxAngularAcc, double destinationTimeOffset, double tForCurve, AbstractFollower2D follower) {
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
        this.tForCurve = tForCurve;
        this.follower = follower;
    }

    public LiveProfilingFollower2D(MotionProfile2D profile, double epsilon, double kX, double kY,
                                   double kAngle, double maxLinearVel, double maxAngularVel,
                                   double maxLinearAcc, double maxAngularAcc, double tForCurve, AbstractFollower2D follower){
        this(profile,epsilon,kX,kY,kAngle,maxLinearVel,maxAngularVel,maxLinearAcc,maxAngularAcc, 2000, tForCurve,  follower);
    }


    @Override
    public void init() {
        startTime = System.currentTimeMillis();
        follower.init();
        follower.setStartTime(startTime);
        lastUpdate = startTime;
        calculateProfile = new ThreadedReturnProfiler(profile, startTime, destinationTimeOffset, maxLinearVel,
                maxAngularVel, maxLinearAcc, maxAngularAcc, tForCurve);

    }

    @Override
    public Vector2D forceRun(double leftCurr, double rightCurr, double angularVel, double timeNow) {
        updateProfile((leftCurr + rightCurr)/2, angularVel);
        Vector2D motorPowers = follower.forceRun(leftCurr, rightCurr, angularVel, timeNow);
        profile = calculateProfile.getProfile();
        return motorPowers;
    }





    public void updateProfile(double linearVelocity, double angularVelocity){
        if(System.currentTimeMillis() - lastUpdate > updateDelay && !calculateProfile.isAlive() && this.calcError(linearVelocity, angularVelocity) > epsilon){
             lastUpdate = System.currentTimeMillis();
             calculateProfile.update(linearVelocity, angularVelocity);
             calculateProfile.start();
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


    private static Position getLocation(){
        return Localizer.getInstance().getLocation(); //TODO check if getLocation() or getLocationRaw()
    }


}
