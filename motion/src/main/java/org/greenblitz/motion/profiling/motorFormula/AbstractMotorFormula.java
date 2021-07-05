package org.greenblitz.motion.profiling.motorFormula;

public abstract class AbstractMotorFormula{
    protected double defaultPower;
    protected double maxVel;

    public AbstractMotorFormula(double defaultPower, double maxVel){
        this.defaultPower = defaultPower;
        this.maxVel = maxVel;
    }

    public abstract double getVel(double acc, double power);

    public abstract double getAcc(double vel, double power);

    public abstract double getPower(double vel, double acc);

    public double getVel(double acc){
        return getVel(acc, defaultPower);
    }

    public double getAcc(double vel){
        return getVel(vel, defaultPower);
    }

    public double getDefaultPower(){
        return this.defaultPower;
    }

    public double getMaxVel(){
        return this.maxVel;
    }
}
