package org.greenblitz.motion.pid;

import org.greenblitz.motion.tolerance.ITolerance;

public class MultivariablePIDController {
    private PIDController[] m_controllers;

    public MultivariablePIDController(int count) {
        this.m_controllers = new PIDController[count];
    }

    public void config(int index, ITolerance tol, PIDObject obj){
        m_controllers[index] = new PIDController(tol, obj);
    }

    public PIDController get(int index) {
        return m_controllers[index];
    }

    public void setGoals(double... goals) {
        for (var i = 0; i < m_controllers.length; i++) {
            m_controllers[i].setGoal(goals[i]);
        }
    }

    public double[] calcuate(double[] values) {
        double[] ret = new double[m_controllers.length];
        for (int i = 0; i < ret.length; i++)
            ret[i] = m_controllers[i].calculatePID(values[i]);
        return ret;
    }
}
