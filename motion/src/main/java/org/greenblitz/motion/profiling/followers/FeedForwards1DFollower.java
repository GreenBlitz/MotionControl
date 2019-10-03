package org.greenblitz.motion.profiling.followers;

import org.greenblitz.motion.profiling.MotionProfile1D;

public class FeedForwards1DFollower {

    protected MotionProfile1D profile;
    protected long startTime;
    protected double kV, kA;

    public FeedForwards1DFollower(MotionProfile1D profile, double kV, double kA){
        this.profile = profile;
        this.kV = kV;
        this.kA = kA;
    }

    public void init(){
        startTime = System.currentTimeMillis();
    }

    public double run(){
        double timeNow = (System.currentTimeMillis() - startTime)/1000.0;
        if (profile.isOver(timeNow)) return 0;
        return profile.getVelocity(timeNow)*kV + profile.getAcceleration(timeNow)*kA;
    }

    public boolean isFinished(){
        return profile.isOver((System.currentTimeMillis() - startTime)/1000.0);
    }

    public double getkV() {
        return kV;
    }

    public void setkV(double kV) {
        this.kV = kV;
    }

    public double getkA() {
        return kA;
    }

    public void setkA(double kA) {
        this.kA = kA;
    }
}
