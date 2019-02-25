package org.greenblitz.motion.pid;

import org.greenblitz.motion.tolerance.ITolerance;

public class MultivariablePIDController {
    private PIDController[] m_controllers;

    public MultivariablePIDController(int count) {
        this.m_controllers = new PIDController[count];
    }

    public MultivariablePIDController(PIDController... pids) {
        m_controllers = pids;
    }

    public void setPIDObject(int index, PIDObject obj, ITolerance tol) {
        m_controllers[index] = new PIDController(obj, tol);
    }

    public void configurePID(int index, double curr, double value, double limLower, double limUpper, double absLimit){
        m_controllers[index].configure(curr, value, limLower, limUpper, absLimit);
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
        for (var i = 0; i < m_controllers.length; i++) {
            if (m_controllers[i].isFinished()) {
                return true;
            }
        }

        return false;
    }
}
    