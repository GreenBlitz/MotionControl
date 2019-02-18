package org.greenblitz.motion.pid;

import org.greenblitz.motion.tolerance.ITolerance;

public class PIDController {

    private PIDObject m_obj;
    private ITolerance m_tolerance;
    private long m_previousTime;
    private double m_goal;
    private double m_previousError;
    private double m_integral = 0;

    public PIDController(ITolerance tol, PIDObject object) {
        m_obj = object;
        m_tolerance = tol;
    }

    public PIDController(ITolerance tol, double kP, double kI, double kD, double kF) {
        this(tol, new PIDObject(kP, kI, kD, kF));
    }

    public PIDController(ITolerance tol, double kP, double kI, double kD) {
        this(tol, kP, kI, kD, 0);
    }

    public void setGoal(double goal) {
        m_goal = goal;
    }

    public double getGoal() {
        return m_goal;
    }

    public double calculatePID(double current) {
        var err = m_goal - current;
        var dt = updateTime();

        var p = m_obj.getKp() * err;

        m_integral += err * dt;
        var i = m_obj.getKi() * m_integral;

        var d = m_obj.getKd() * (err - m_previousError) / dt;

        m_previousError = err;
        return p + i + d;
    }

    public PIDObject getPidObject() {
        return m_obj;
    }

    public double getLastError() {
        return m_previousError;
    }

    public boolean isFinished() {
        return m_tolerance.onTarget(m_goal, getLastError());
    }

    private double updateTime() {
        var current = System.currentTimeMillis();
        double ms = current - m_previousTime;
        m_previousTime = current;
        return ms;
    }

    private void resetIntegralZone() {
        m_integral = 0;
    }
}
