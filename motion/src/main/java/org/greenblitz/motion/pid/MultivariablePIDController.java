package org.greenblitz.motion.pid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultivariablePIDController {

    protected List<PIDObject> pidObjects;
    protected long previousTime;

    public MultivariablePIDController(PIDObject... objs){
        pidObjects = Arrays.asList(objs);
    }

    public MultivariablePIDController(List<PIDObject> objs){
        pidObjects = objs;
    }

    public static MultivariablePIDController generateSingle(double kp, double ki, double kd, double kf) {
        List<PIDObject> controller = new ArrayList<>();
        controller.add(new PIDObject(kp, ki, kd, kf));
        return new MultivariablePIDController(controller);
    }

    public static MultivariablePIDController generateSingle(double kp, double ki, double kd){
        return generateSingle(kp, kd, ki, 0);
    }

    public static MultivariablePIDController generateSingle(double kp, double ki){
        return generateSingle(kp, ki, 0);
    }

    public static MultivariablePIDController generateSingle(double kp){
        return generateSingle(kp, 0);
    }

    /**
     * Calling this implies starting to use the controller
     * @param values Name of controller with [goal, value0] as values
     */
    public void init(double[] goals, double[] values){
        for (int i = 0; i < pidObjects.size(); i++)
            pidObjects.get(i).init(goals[i], values[i]);
        previousTime = System.currentTimeMillis();
    }

    /**
     *
     * @param goals
     * @param current
     * @return
     */
    public double[] calculatePID(double[] goals, double[] current){

        double secsPassed = (System.currentTimeMillis() - previousTime) / 1000.0;
        previousTime = System.currentTimeMillis();

        double[] pidVals = new double[pidObjects.size()];

        for (int i = 0; i < pidObjects.size(); i++)
            pidVals[i] = pidObjects.get(i).calculatePID(goals[i], current[i], secsPassed);

        return pidVals;
    }

    /**
     *
     * @param goal
     * @param current
     * @param maxAllowedError
     * @return
     */
    public boolean isFinished(double[] goal, double[] current, double maxAllowedError){
        for (int i = 0; i < goal.length; i++){
            if (Math.abs(goal[i] - current[i]) > maxAllowedError)
                return false;
        }
        return true;
    }

    public PIDObject getPidObject(int index){
        return pidObjects.get(index);
    }

}
