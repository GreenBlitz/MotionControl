package org.greenblitz.motion.pid;

import org.greenblitz.motion.tolerance.ITolerance;

public class MultivariablePIDController {
    private PIDController[] m_controllers;
    private ITolerance[] m_tolerance;

    public MultivariablePIDController(int count) {
        this.m_controllers = new PIDController[count];
        m_tolerance = new ITolerance[count];
    }

    public void config(int index, PIDObject obj, ITolerance tol) {
        m_controllers[index] = new PIDController(obj);
        m_tolerance[index] = tol;
    }

    public PIDController get(int index) {
        return m_controllers[index];
    }

    public void setGoals(double... goals) {
        for (var i = 0; i < m_controllers.length; i++) {
            m_controllers[i].setGoal(goals[i]);
        }
    }

    public double[] calculate(double... values) {
        double[] ret = new double[m_controllers.length];
        for (int i = 0; i < ret.length; i++)
            ret[i] = m_controllers[i].calculatePID(values[i]);
        return ret;
    }

    public boolean isFinished() {
        for (var i = 0; i < m_tolerance.length; i++) {
            if (m_tolerance[i] != null && m_tolerance[i].onTarget(m_controllers[i].getGoal(), m_controllers[i].getLastError())) {
                return true;
            }
        }

        return false;
    }
}
