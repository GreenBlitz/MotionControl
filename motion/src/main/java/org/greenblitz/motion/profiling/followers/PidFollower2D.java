package org.greenblitz.motion.profiling.followers;

import org.greenblitz.debug.RemoteCSVTarget;
import org.greenblitz.motion.base.Vector2D;
import org.greenblitz.motion.pid.CollapsingPIDController;
import org.greenblitz.motion.pid.PIDController;
import org.greenblitz.motion.pid.PIDObject;
import org.greenblitz.motion.profiling.MotionProfile2D;

public class PidFollower2D {

    protected long startTime;
    protected double kVl, kAl;
    protected double kVr, kAr;
    protected MotionProfile2D profile;
    protected double PIDLimit;
    protected CollapsingPIDController leftController, rightController;
    protected PIDController angularVelocityController;
    protected double wheelDist;

    protected RemoteCSVTarget wheelTarget;
    protected RemoteCSVTarget globalTarget;
    protected boolean sendData = false;

    public PidFollower2D(double kVl, double kAl, double kVr, double kAr, double wheelDist, MotionProfile2D profile) {
        this(kVl, kAl, kVr, kAr, new PIDObject(0), 0, 0, new PIDObject(0), wheelDist, profile);
    }

    public PidFollower2D(double kVl, double kAl, double kVr, double kAr,
                         PIDObject vals, double collapseVals, double pidLimit, PIDObject angVals, double wheelDist,
                         MotionProfile2D profile) {
        this.kVl = kVl;
        this.kAl = kAl;
        this.kVr = kVr;
        this.kAr = kAr;
        this.profile = profile;
        this.wheelDist = wheelDist;
        leftController = new CollapsingPIDController(vals, collapseVals);
        rightController = new CollapsingPIDController(vals, collapseVals);
        angularVelocityController = new PIDController(angVals);
        PIDLimit = pidLimit;
    }

    public void init(){
        startTime = System.currentTimeMillis();
        leftController.configure(0,0,-PIDLimit,PIDLimit,0);
        rightController.configure(0,0,-PIDLimit,PIDLimit,0);
        angularVelocityController.configure(0, 0, -PIDLimit, PIDLimit, 0);

        if (sendData) {
            wheelTarget = RemoteCSVTarget.initTarget("WheelData", "time", "DesiredLeft", "ActualLeft",
                    "DesiredRight", "ActualRight");
            globalTarget = RemoteCSVTarget.initTarget("ProfileData", "time", "DesiredLinVel",
                    "ActualLinVel", "DesiredAngVel", "ActualAngVel");
        }
    }

    public Vector2D run(double left, double right, double angularVel){
        return run(left, right, angularVel, System.currentTimeMillis());
    }

    public Vector2D run(double leftCurr, double rightCurr, double angularVel, double curTime){

        double timeNow = (curTime - startTime)/1000.0;

        if (profile.isOver(timeNow)) return new Vector2D(0, 0);

        Vector2D velocity = profile.getVelocity(timeNow);
        Vector2D acceleration = profile.getAcceleration(timeNow);

        angularVelocityController.setGoal(velocity.getY());
        double angularPIDOut = angularVelocityController.calculatePID(angularVel);

        /*
        See:
        https://matrixcalc.org/en/slu.html#solve-using-Cramer%27s-rule%28%7B%7B1/2,1/2,0,0,v%7D,%7B1/d,-1/d,0,0,o%7D%7D%29
         */
        double leftMotorV = (wheelDist*velocity.getY() + 2*velocity.getX())/2.0;
        double leftMotorA = (wheelDist*acceleration.getY() + 2*acceleration.getX())/2.0;
        double rightMotorV = (-wheelDist*velocity.getY() + 2*velocity.getX())/2.0;
        double rightMotorA = (-wheelDist*acceleration.getY() + 2*acceleration.getX())/2.0;

        if (sendData){
            wheelTarget.report(timeNow, leftMotorV, leftCurr, rightMotorV, rightCurr);
            globalTarget.report(timeNow, velocity.getX(), (leftCurr + rightCurr)/2.0, velocity.getY(),
                    (leftCurr - rightCurr)/wheelDist);
        }

        leftController.setGoal(leftMotorV);
        rightController.setGoal(rightMotorV);

        if (acceleration.getX() > 0) {
            return new Vector2D(leftMotorV * kVl + leftMotorA * kAl + leftController.calculatePID(leftCurr) + angularPIDOut,
                    rightMotorV * kVr + rightMotorA * kAr + rightController.calculatePID(rightCurr) - angularPIDOut);
        } else {
            return new Vector2D(leftMotorV * kVl + leftMotorA * kAl * 0.5 + leftController.calculatePID(leftCurr) + angularPIDOut,
                    rightMotorV * kVr + rightMotorA * kAr * 0.5 + rightController.calculatePID(rightCurr) - angularPIDOut);
        }

    }

    public boolean isFinished(){
        return profile.isOver((System.currentTimeMillis() - startTime)/1000.0);
    }

    public void setSendData(boolean val){
        sendData = val;
    }

}