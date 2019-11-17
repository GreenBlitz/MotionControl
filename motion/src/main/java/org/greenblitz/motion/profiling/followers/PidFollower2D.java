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

    public PidFollower2D(double kVl, double kAl, double kVr, double kAr, MotionProfile2D profile) {
        this(kVl, kAl, kVr, kAr, new PIDObject(0), new PIDObject(0), 0, profile);
    }

    public PidFollower2D(double kVl, double kAl, double kVr, double kAr,
                         PIDObject leftVals, PIDObject rightVals, double pidLimit,
                         MotionProfile2D profile) {
        this.kVl = kVl;
        this.kAl = kAl;
        this.kVr = kVr;
        this.kAr = kAr;
        this.profile = profile;
        leftController = new PIDController(leftVals);
        rightController = new PIDController(rightVals);
        PIDLimit = pidLimit;
    }

    public void init(){
        startTime = System.currentTimeMillis();
        leftController.configure(0,0,-PIDLimit,PIDLimit,0);
        rightController.configure(0,0,-PIDLimit,PIDLimit,0);
    }

    public Vector2D run(double leftVelocity, double rightVelocity){

        double timeNow = (System.currentTimeMillis() - startTime)/1000.0;

        if (profile.isOver(timeNow)) return new Vector2D(0, 0);

        Vector2D velocity = profile.getVelocity(timeNow);
        Vector2D acceleration = profile.getAcceleration(timeNow);

        double lin = velocity.getX()*kVl + acceleration.getX()*kAl;
        double angular = velocity.getY()*kVr + acceleration.getY()*kAr;

        leftController.setGoal(velocity.getX()*kVl - velocity.getY()*kVr);
        rightController.setGoal(velocity.getX()*kVl + velocity.getY()*kVr);

        return new Vector2D((lin - angular) + leftController.calculatePID(leftVelocity),
                lin + angular + rightController.calculatePID(rightVelocity));

    }

    public boolean isFinished(){
        return profile.isOver((System.currentTimeMillis() - startTime)/1000.0);
    }


}