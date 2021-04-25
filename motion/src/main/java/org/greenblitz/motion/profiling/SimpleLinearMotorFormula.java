package org.greenblitz.motion.profiling;

public class SimpleLinearMotorFormula implements IMotorFormula {

    private double ka;
    private double kv;

    /**
     * The relation between power, vel and acc described by:
     * p = kv * v + ka * a
     *
     * DIY - calc the constants (takes about 5 min and 2 disposable robots)
     * Step 1 - Put the const's var in max value so the other var will cancel
     * Step 2 - Use your brain
     * Step 3 - Test on the robot
     * Step 4 - Find that you are really dumb, this formula doesn't work well
     * Step 5 - Fix robot
     * Step 6 - Find that you are really dumb, this robot is really dumb
     * Step 8 - Fix build
     * Step 9 - Learn to count, start from 0 this time
     */

    public SimpleLinearMotorFormula(double ka, double kv) {
        this.ka = ka;
        this.kv = kv;
    }

    @Override
    public double getVel(double acc, double power) {
        return (power - ka * acc) / kv;
    }

    @Override
    public double getAcc(double vel, double power) {
        return (power - kv * vel) / ka;
    }

    @Override
    public double getPower(double vel, double acc) {
        return kv * vel + ka * acc;
    }
}
