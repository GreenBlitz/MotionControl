package org.greenblitz.motion.pid;

import org.greenblitz.motion.tolerance.ITolerance;

public class PIDController {

    private PIDObject m_obj;
    private long m_previousTime;
    private double m_goal;
    private double m_previousError;
    private double m_integral;

    private double m_minimumOutput;
    private double m_maximumOutput;

    public PIDController(PIDObject object) {
        m_obj = object;
    }

    public PIDController(double kP, double kI, double kD, double kF) {
        this(new PIDObject(kP, kI, kD, kF));
    }

    public PIDController(double kP, double kI, double kD) {
        this(kP, kI, kD, 0);
    }

    public void configureOutputLimits(double min, double max) {
        m_minimumOutput = min;
        m_maximumOutput = max;
    }

    public void setGoal(double goal) {
        m_goal = goal;
    }

    public double getGoal() {
        return m_goal;
    }

    public double getLowerOutputLimit() {
        return m_minimumOutput;
    }

    public double getUpperOutputLimit() {
        return m_maximumOutput;
    }

    public double calculatePID(double current) {
        var err = m_goal - current;
        var dt = updateTime();

        var p = m_obj.getKp() * err;

        m_integral += err * dt;
        var i = m_obj.getKi() * m_integral;

        var d = m_obj.getKd() * (err - m_previousError) / dt;

        m_previousError = err;
        return clamp(p + i + d);
    }

    public PIDObject getPidObject() {
        return m_obj;
    }

    public double getLastError() {
        return m_previousError;
    }

    public void resetIntegralZone() {
        m_integral = 0;
    }

    private double updateTime() {
        var current = System.currentTimeMillis();
        double ms = current - m_previousTime;
        m_previousTime = current;
        return ms;
    }

    private double clamp(double value) {
        return Math.max(Math.min(value, m_minimumOutput), m_maximumOutput);
    }
}
