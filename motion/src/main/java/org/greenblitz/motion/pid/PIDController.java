package org.greenblitz.motion.pid;

public class PIDController {

    protected PIDObject obj;
    protected long previousTime;

    public PIDController(PIDObject object){
        obj = object;
    }
    public PIDController(double kP, double kI, double kD, double kF) {
        this(new PIDObject(kP, kI, kD, kF));
    }

    public PIDController(double kP, double kI, double kD) {
        this(kP, kI, kD, 0);
    }

    public void init(double goal, double value){
        obj.init(goal, value);
        previousTime = System.currentTimeMillis();
    }

    public double calculatePID(double goal, double current){

        double secsPassed = (System.currentTimeMillis() - previousTime) / 1000.0;
        previousTime = System.currentTimeMillis();

        return obj.calculatePID(goal, current, secsPassed);
    }


    public boolean isFinished(double goal, double current, double maxAllowedError){
        return Math.abs(goal - current) <= maxAllowedError;
    }

    public PIDObject getPidObject(){
        return obj;
    }

}
