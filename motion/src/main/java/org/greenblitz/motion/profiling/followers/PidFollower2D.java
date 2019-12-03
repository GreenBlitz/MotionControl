package org.greenblitz.motion.profiling.followers;

import org.greenblitz.motion.base.Vector2D;
import org.greenblitz.motion.pid.PIDController;
import org.greenblitz.motion.pid.PIDObject;
import org.greenblitz.motion.profiling.MotionProfile2D;

public class PidFollower2D {

    protected long startTime;
    protected double kVl, kAl;
    protected double kVr, kAr;
    protected MotionProfile2D profile;
    double PIDLimit;
    protected PIDController leftController, rightController;
    protected double wheelDist;

    public PidFollower2D(double kVl, double kAl, double kVr, double kAr, double wheelDist, MotionProfile2D profile) {
        this(kVl, kAl, kVr, kAr, new PIDObject(0), new PIDObject(0), 0, wheelDist, profile);
    }

    public PidFollower2D(double kVl, double kAl, double kVr, double kAr,
                         PIDObject leftVals, PIDObject rightVals, double pidLimit, double wheelDist,
                         MotionProfile2D profile) {
        this.kVl = kVl;
        this.kAl = kAl;
        this.kVr = kVr;
        this.kAr = kAr;
        this.profile = profile;
        this.wheelDist = wheelDist;
        leftController = new PIDController(leftVals);
        rightController = new PIDController(rightVals);
        PIDLimit = pidLimit;
    }

    public void init(){
        startTime = System.currentTimeMillis();
        leftController.configure(0,0,-PIDLimit,PIDLimit,0);
        rightController.configure(0,0,-PIDLimit,PIDLimit,0);
    }

    public Vector2D run(double leftCurr, double rightCurr){

        double timeNow = (System.currentTimeMillis() - startTime)/1000.0;

        if (profile.isOver(timeNow)) return new Vector2D(0, 0);

        Vector2D velocity = profile.getVelocity(timeNow);
        Vector2D acceleration = profile.getAcceleration(timeNow);

        /*
        See:
        https://matrixcalc.org/en/slu.html#solve-using-Cramer%27s-rule%28%7B%7B1/2,1/2,0,0,v%7D,%7B1/d,-1/d,0,0,o%7D%7D%29
         */
        double leftMotorV = (wheelDist*velocity.getY() + 2*velocity.getX())/2.0;
        double leftMotorA = (wheelDist*acceleration.getY() + 2*acceleration.getX())/2.0;
        double rightMotorV = (-wheelDist*velocity.getY() + 2*velocity.getX())/2.0;
        double rightMotorA = (-wheelDist*acceleration.getY() + 2*acceleration.getX())/2.0;

        leftController.setGoal(leftMotorV);
        rightController.setGoal(rightMotorV);

        return new Vector2D(leftMotorV*kVl + leftMotorA*kAl + leftController.calculatePID(leftCurr),
                rightMotorV*kVr + rightMotorA*kAr + rightController.calculatePID(rightCurr));

    }

    public boolean isFinished(){
        return profile.isOver((System.currentTimeMillis() - startTime)/1000.0);
    }


}