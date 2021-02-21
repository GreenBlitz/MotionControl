package org.greenblitz.motion.profiling.followers;

import org.greenblitz.debug.RemoteCSVTarget;
import org.greenblitz.motion.Localizer;
import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Position;
import org.greenblitz.motion.base.Vector2D;
import org.greenblitz.motion.profiling.MotionProfile2D;
import org.greenblitz.motion.profiling.ProfilingData;
import org.greenblitz.motion.profiling.ThreadedReturnProfiler;

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
    private AbstractFollower2D follower;
    private long startTime;
    private ThreadedReturnProfiler calculateProfile;
    private long lastUpdate;

    private double updateDelay;

    private double stateSampling;

    public LiveProfilingFollower2D(MotionProfile2D profile, double epsilon, double kX, double kY,
                                   double kAngle, double maxLinearVel, double maxAngularVel,
                                   double maxLinearAcc, double maxAngularAcc, double destinationTimeOffset, double tForCurve, AbstractFollower2D follower,
                                   double updateDelay, int stateSampling) {
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
        this.updateDelay = updateDelay;
        this.stateSampling = stateSampling;
    }



    public LiveProfilingFollower2D(MotionProfile2D profile, double epsilon, double kX, double kY,
                                   double kAngle, ProfilingData data, double destinationTimeOffset,
                                   double tForCurve, AbstractFollower2D follower, double updateDelay, int stateSampling){
        this(profile,epsilon,kX,kY,kAngle,data.getMaxLinearVelocity(), data.getMaxAngularVelocity(),
                data.getMaxLinearAccel(), data.getMaxAngularAccel(), destinationTimeOffset, tForCurve,  follower, updateDelay, stateSampling);
    }



    @Override
    public void init() {
        startTime = System.currentTimeMillis();
        follower.init();
        follower.setStartTime(startTime);
        lastUpdate = startTime;
        calculateProfile = new ThreadedReturnProfiler(profile, startTime, destinationTimeOffset, maxLinearVel,
                maxAngularVel, maxLinearAcc, maxAngularAcc, tForCurve, stateSampling);
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
            error = this.calcError(time);
        }
        if(time - lastUpdate > updateDelay && !calculateProfile.isAlive() &&(true ||((sendData && error < epsilon) || this.calcError(time) > epsilon))){
             lastUpdate = System.currentTimeMillis();
             calculateProfile.update(linearVelocity, angularVelocity);
             calculateProfile.start();
        }
    }

    private double calcError(double time){
        Position position = new Position(getLocation());
        double currX = position.getX();
        double currY = position.getY();
        double currAngle = position.getAngle();

        Position targetPosition = profile.getStateLocation(time-startTime);
        double targetX = targetPosition.getX();
        double targetY = targetPosition.getY();
        double targetAngle = targetPosition.getAngle();

        Point targetLast = profile.getStateLocation(profile.getTEnd());
        Point actualLast = targetLast.translate(targetX-2*currX, targetY-2*currY).rotate(targetAngle-currAngle).translate(currX, currY);

        double error = kX*(targetLast.getX()-actualLast.getX()) + kY*(targetLast.getY()-actualLast.getY()) + kAngle * (targetAngle-currAngle);
        if(sendData){
            globalTarget.report(time, error, currX, currY, currAngle, targetX, targetY, targetAngle);
        }
        return error;
    }



    private static Position getLocation(){
        return Localizer.getInstance().getLocation(); //TODO check if getLocation() or getLocationRaw()
    }


}
