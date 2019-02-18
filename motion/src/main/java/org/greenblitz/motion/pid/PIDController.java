package org.greenblitz.motion.pid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PIDController {

    protected PIDObject obj;
    protected long previousTime;

    /**
     *
     * @param object
     */
    public PIDController(PIDObject object){
        obj = object;
    }

    /**
     *
     * @param kP
     * @param kI
     * @param kD
     * @param kF
     */
    public PIDController(double kP, double kI, double kD, double kF) {
        this(new PIDObject(kP, kI, kD, kF));
    }

    /**
     *
     * @param kP
     * @param kI
     * @param kD
     */
    public PIDController(double kP, double kI, double kD) {
        this(kP, kI, kD, 0);
    }

    /**
     * Calling this implies starting to use the controller
     * @param goal
     * @param value
     */
    public void init(double goal, double value){
        obj.init(goal, value);
        previousTime = System.currentTimeMillis();
    }

    /**
     *
     * @param goal
     * @param current
     * @return
     */
    public double calculatePID(double goal, double current){

        double secsPassed = (System.currentTimeMillis() - previousTime) / 1000.0;
        previousTime = System.currentTimeMillis();

        return obj.calculatePID(goal, current, secsPassed);
    }

    /**
     *
     * @param goal
     * @param current
     * @param maxAllowedError
     * @return
     */
    public boolean isFinished(double goal, double current, double maxAllowedError){
        return Math.abs(goal - current) <= maxAllowedError;
    }

    public PIDObject getPidObject(){
        return obj;
    }

}
