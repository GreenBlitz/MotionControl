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
    protected PIDController linController, rotController;

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
        linController = new PIDController(leftVals);
        rotController = new PIDController(rightVals);
        PIDLimit = pidLimit;
    }

    public void init(){
        startTime = System.currentTimeMillis();
        linController.configure(0,0,-PIDLimit,PIDLimit,0);
        rotController.configure(0,0,-PIDLimit,PIDLimit,0);
    }

    public Vector2D run(double linearCurr, double angularCurr){

        double timeNow = (System.currentTimeMillis() - startTime)/1000.0;

        if (profile.isOver(timeNow)) return new Vector2D(0, 0);

        Vector2D velocity = profile.getVelocity(timeNow);
        Vector2D acceleration = profile.getAcceleration(timeNow);

        double linDesired = velocity.getX();
        double accDesired = velocity.getY();

        linController.setGoal(linDesired);
        rotController.setGoal(accDesired);

        double lin = linDesired*kVl + acceleration.getX()*kAl + linController.calculatePID(linearCurr);
        double angular = accDesired*kVr + acceleration.getY()*kAr + rotController.calculatePID(angularCurr);

        return new Vector2D((lin - angular),
                lin + angular);

    }

    public boolean isFinished(){
        return profile.isOver((System.currentTimeMillis() - startTime)/1000.0);
    }


}