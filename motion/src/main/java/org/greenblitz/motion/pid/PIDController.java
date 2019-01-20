package org.greenblitz.motion.pid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PIDController {

    protected PIDObject obj;
    protected long previousTime;

    public PIDController(PIDObject object){
        obj = object;
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
