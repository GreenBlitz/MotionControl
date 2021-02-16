package org.greenblitz.motion.profiling.followers;

import org.greenblitz.debug.RemoteCSVTarget;
import org.greenblitz.motion.Localizer;
import org.greenblitz.motion.base.Position;
import org.greenblitz.motion.base.State;
import org.greenblitz.motion.base.Vector2D;
import org.greenblitz.motion.pid.PIDObject;
import org.greenblitz.motion.profiling.MotionProfile2D;
import org.greenblitz.motion.profiling.ProfilingData;
import org.greenblitz.motion.profiling.ReturnProfiler2D;
import org.greenblitz.motion.profiling.ThreadedReturnProfiler;
import org.greenblitz.utils.LinkedList;

public class LiveProfilingFollower2D extends AbstractFollower2D {
    private double destinationTimeOffset;
    private double epsilon;
    private double kX;
    private double kY;
    private double kAngle;
    private double kLinVel;
    private double kAngVel;
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
                                   double kAngle, double kLinVel, double kAngVel, double maxLinearVel, double maxAngularVel,
                                   double maxLinearAcc, double maxAngularAcc, double destinationTimeOffset, double tForCurve, AbstractFollower2D follower) {
        this.profile = profile;
        this.epsilon = epsilon;
        this.kX = kX;
        this.kY = kY;
        this.kAngle = kAngle;
        this.kLinVel = kLinVel;
        this.kAngVel = kAngVel;
        this.maxLinearVel = maxLinearVel;
        this.maxAngularVel = maxAngularVel;
        this.maxLinearAcc = maxLinearAcc;
        this.maxAngularAcc = maxAngularAcc;
        this.destinationTimeOffset = destinationTimeOffset;
        this.tForCurve = tForCurve;
        this.follower = follower;
    }

    public LiveProfilingFollower2D(MotionProfile2D profile, double epsilon, double kX, double kY,
                                   double kAngle, double kLinVel, double kAngVel, double maxLinearVel, double maxAngularVel,
                                   double maxLinearAcc, double maxAngularAcc, double tForCurve, AbstractFollower2D follower){
        this(profile,epsilon,kX,kY,kAngle,kLinVel,kAngVel,maxLinearVel,maxAngularVel,maxLinearAcc,maxAngularAcc, 2000, tForCurve,  follower);
    }

    public LiveProfilingFollower2D(MotionProfile2D profile, double epsilon, double kX, double kY,
                                   double kAngle,double kLinVel,double kAngVel, ProfilingData data, double tForCurve, AbstractFollower2D follower){
        this(profile,epsilon,kX,kY,kAngle,kLinVel,kAngVel,data.getMaxLinearVelocity(), data.getMaxAngularVelocity(),
                data.getMaxLinearAccel(), data.getMaxAngularAccel(), 2000, tForCurve,  follower);
    }


    @Override
    public void init() {
        startTime = System.currentTimeMillis();
        follower.init();
        follower.setStartTime(startTime);
        lastUpdate = startTime;
        calculateProfile = new ThreadedReturnProfiler(profile, startTime, destinationTimeOffset, maxLinearVel,
                maxAngularVel, maxLinearAcc, maxAngularAcc, tForCurve);
        if(sendData){
            globalTarget = RemoteCSVTarget.initTarget("errorTarget","time", "error", "currX", "currY",
                    "currAngle", "currLinVel", "currAngVel", "targetX", "targetY", "targetAngle", "targetLinVel", "targetAngVel");
        }

    }

    @Override
    public Vector2D forceRun(double leftCurr, double rightCurr, double angularVel, double timeNow) {
        updateProfile((leftCurr + rightCurr)/2, angularVel, timeNow);
        Vector2D motorPowers = follower.forceRun(leftCurr, rightCurr, angularVel, timeNow);
        profile = calculateProfile.getProfile();
        return motorPowers;
    }





    public void updateProfile(double linearVelocity, double angularVelocity, double time){
        double error = 0;
        if(sendData) {
            error = this.calcError(linearVelocity, angularVelocity, time);
        }
        if(time - lastUpdate > updateDelay && !calculateProfile.isAlive() &&(true ||((sendData && error < epsilon) || this.calcError(linearVelocity, angularVelocity, time) > epsilon))){
             lastUpdate = System.currentTimeMillis();
             calculateProfile.update(linearVelocity, angularVelocity);
             calculateProfile.start();
        }
    }

    private double calcError(double linearVelocity, double angularVelocity, double time){
        State state = new State(getLocation(), linearVelocity, angularVelocity);
        double currX = state.getX();
        double currY = state.getY();
        double currAngle = state.getAngle();
        double currLinVel = state.getLinearVelocity();
        double currAngVel = state.getAngularVelocity();

        State currPosition = profile.getStateLocation(time-startTime);
        double targetX = currPosition.getX();
        double targetY = currPosition.getY();
        double targetAngle = currPosition.getAngle();
        double targetLinVel = currPosition.getLinearVelocity();
        double targetAngVel = currPosition.getAngularVelocity();

        double error = kX * (targetX - currX) + kY * (targetY - currY) + kAngle * (targetAngle - currAngle) + kLinVel *
                (targetLinVel - currLinVel) + kAngVel * (targetAngVel - currAngVel);
        if(sendData){
            globalTarget.report(time, error, currX, currY, currAngle, currLinVel, currAngVel, targetX, targetY, targetAngle, targetLinVel, targetLinVel);
        }
        return error;
    }


    private static Position getLocation(){
        return Localizer.getInstance().getLocation(); //TODO check if getLocation() or getLocationRaw()
    }


}
