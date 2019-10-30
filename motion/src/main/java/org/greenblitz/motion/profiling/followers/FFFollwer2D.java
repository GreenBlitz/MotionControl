package org.greenblitz.motion.profiling.followers;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.greenblitz.motion.base.TwoTuple;
import org.greenblitz.motion.base.Vector2D;
import org.greenblitz.motion.profiling.MotionProfile2D;

public class FFFollwer2D {

    protected long startTime;
    protected double kVl, kAl;
    protected double kVr, kAr;
    protected MotionProfile2D profile;

    public FFFollwer2D(double kVl, double kAl, double kVr, double kAr, MotionProfile2D profile) {
        this.kVl = kVl;
        this.kAl = kAl;
        this.kVr = kVr;
        this.kAr = kAr;
        this.profile = profile;
    }

    public void init(){
        startTime = System.currentTimeMillis();
    }

    public Vector2D run(){

        double timeNow = (System.currentTimeMillis() - startTime)/1000.0;

        if (profile.isOver(timeNow)) return new Vector2D(0, 0);

        Vector2D velocity = profile.getVelocity(timeNow);
        Vector2D acceleration = profile.getAcceleration(timeNow);

        double lin = velocity.getX()*kVl + acceleration.getX()*kAl;
        double angular = velocity.getY()*kVr + acceleration.getY()*kAr;

        return new Vector2D(lin - angular, lin + angular);

    }

    public boolean isFinished(){
        return profile.isOver((System.currentTimeMillis() - startTime)/1000.0);
    }


}