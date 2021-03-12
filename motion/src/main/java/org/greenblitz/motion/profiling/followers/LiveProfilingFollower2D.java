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
    private AbstractFollower2D follower;
    private ThreadedReturnProfiler calculateProfile;
    private double lastUpdate;

    private double updateDelay;

    public LiveProfilingFollower2D(MotionProfile2D profile, double epsilon, double kX, double kY,
                                   double kAngle, double kLinVel, double kAngVel, double maxLinearVel, double maxAngularVel,
                                   double maxLinearAcc, double maxAngularAcc, double destinationTimeOffset, double tForCurve, AbstractFollower2D follower,
                                   double updateDelay) {
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
        this.updateDelay = updateDelay;
    }



    public LiveProfilingFollower2D(MotionProfile2D profile, double epsilon, double kX, double kY,
                                   double kAngle,double kLinVel,double kAngVel, ProfilingData data, double destinationTimeOffset,
                                   double tForCurve, AbstractFollower2D follower, double updateDelay){
        this(profile,epsilon,kX,kY,kAngle,kLinVel,kAngVel,data.getMaxLinearVelocity(), data.getMaxAngularVelocity(),
                data.getMaxLinearAccel(), data.getMaxAngularAccel(), destinationTimeOffset, tForCurve,  follower, updateDelay);
    }


    /**
     * Resets all relevant data, call before every run.
     */
    @Override
    public void init() {
        startTime = System.currentTimeMillis();
        follower.init();
        follower.setStartTime(startTime);
        lastUpdate = 0;
        profile.updateJahana();
        if(profile.getJahanaRelation() == null){
            System.out.println("JahanaRelation is null in init");
        }
        calculateProfile = new ThreadedReturnProfiler(profile, startTime, destinationTimeOffset, maxLinearVel,
                maxAngularVel, maxLinearAcc, maxAngularAcc, tForCurve);
        if(sendData){
            globalTarget = RemoteCSVTarget.initTarget("errorTarget","time", "error", "currX", "currY",
                    "currAngle", "currLinVel", "currAngVel", "targetX", "targetY", "targetAngle", "targetLinVel", "targetAngVel");
        }

    }

    /**
     * @param leftCurr   The left wheel velocity
     * @param rightCurr  The right wheel velocity
     * @param angularVel The angular velocity
     * @param timeNow    The current time in seconds
     * @return A vector of power to each motor in the format (left, right)
     */
    @Override
    public Vector2D forceRun(double leftCurr, double rightCurr, double angularVel, double timeNow) {
        updateProfile((leftCurr + rightCurr)/2, angularVel, timeNow);
        Vector2D motorPowers = follower.forceRun(leftCurr, rightCurr, angularVel, timeNow);
        this.setProfile(calculateProfile.getProfile());
        return motorPowers;
    }


    /**
     * checks if needs to update the profile, and starts calculating the new profile if needed
     * @param linearVelocity  the current linear velocity
     * @param angularVelocity the current angular velocity
     * @param time            the current time in seconds
     */
    public void updateProfile(double linearVelocity, double angularVelocity, double time){
        State state = new State(getLocation(), linearVelocity, angularVelocity);
        double error = 0;
        if(sendData) {
            error = this.calcError(time, state);
        }
        if(time - lastUpdate > updateDelay && !calculateProfile.isAlive() &&(true ||((sendData && error < epsilon) || this.calcError(time, state) > epsilon))){
             lastUpdate = time;
             calculateProfile.update(state);
             System.out.println("Switched profile");
             calculateProfile.start();
        }
    }

    private double calcError( double time, State state){
        double currX = state.getX();
        double currY = state.getY();
        double currAngle = state.getAngle();
        double currLinVel = state.getLinearVelocity();
        double currAngVel = state.getAngularVelocity();

        if(profile.getJahanaRelation() == null){
            System.out.println("jahanaRelation is null in calcError");
        }

        State currPosition = profile.getStateLocation(time);
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


    /**
     * @return the current position (according to the localizer)
     */
    private static Position getLocation(){
        return Localizer.getInstance().getLocationRaw(); //TODO check if getLocation() or getLocationRaw()
    }

    @Override
    public void setProfile(MotionProfile2D newProfile){
        profile = newProfile;
        follower.setProfile(newProfile);
    }


    @Override
    public void atEnd(){
        System.out.println(profile);
    }

    @Override
    public AbstractFollower2D clone() {
        return new LiveProfilingFollower2D(this.profile.clone(),this.epsilon,this.kX,this.kY,this.kAngle,this.kLinVel,
                this.kAngVel, this.maxLinearVel, this.maxAngularVel, this.maxLinearAcc, this.maxAngularAcc,
                this.destinationTimeOffset, this.tForCurve, this.follower.clone(), this.updateDelay);
    }


}
