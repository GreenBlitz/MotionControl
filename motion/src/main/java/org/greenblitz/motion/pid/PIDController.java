package org.greenblitz.motion.pid;

import org.greenblitz.motion.exceptions.UninitializedPIDException;
import org.greenblitz.motion.tolerance.ITolerance;

public class PIDController {

    private PIDObject m_obj;

    private long m_previousTime;
    private double m_goal;
    private double m_previousError;
    private double m_integral;

    private double m_minimumOutput;
    private double m_maximumOutput;
    private double m_deadband;

    private boolean m_initialized;

    private ITolerance m_tolerance;

    public PIDController(PIDObject obj, ITolerance tolerance) {
        m_obj = obj;
        m_tolerance = tolerance;
        m_initialized = false;
    }

    public PIDController(PIDObject obj) {
        this(obj, null);
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

    public void initialize(double goal, double limitLower, double limitUpper, double deadband){
        setGoal(goal);
        resetIntegralZone(0);
        configureOutputLimits(limitLower, limitUpper);
        m_previousTime = System.currentTimeMillis();
        m_deadband = deadband;
        m_initialized = true;
    }

    public void initialize(double goal) {
        initialize(goal, -Double.MAX_VALUE, Double.MAX_VALUE, 0);
    }

    public double getDeadband() {
        return m_deadband;
    }

    public void setDeadband(double deadband) {
        this.m_deadband = deadband;
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
        if (!m_initialized)
            throw new UninitializedPIDException(this);

        if (isFinished(current))
            return 0;

        var err = error(m_goal, current);
        m_previousError = err;

        var dt = updateTime();

        var p = m_obj.getKp() * err;

        m_integral += err * dt;
        var i = m_obj.getKi() * m_integral;

        var d = m_obj.getKd() * error(err, m_previousError) / dt;

        var f = m_obj.getKf() * getGoal();

        return applyDeadband(clamp(p + i + d + f));
    }

    public PIDObject getPidObject() {
        return m_obj;
    }

    public double getLastError() {
        return m_previousError;
    }

    public void resetIntegralZone(double iZone) {
        m_integral = iZone;
    }

    public ITolerance getTolerance() {
        return m_tolerance;
    }

    public void setTolerance(ITolerance tol) {
        m_tolerance = tol;
    }

    public boolean isFinished() {
        return isFinished(getLastError());
    }

    public boolean isFinished(double current) { return hasTolerance() && m_tolerance.onTarget(getGoal(), error(getGoal(), current)); }

    public boolean hasTolerance() {
        return m_tolerance != null;
    }

    public boolean isInitialized() {
        return m_initialized;
    }

    public void setP(double kP) {
        m_obj.setKp(kP);
    }

    private double updateTime() {
        var current = System.currentTimeMillis();
        double ms = current - m_previousTime;
        m_previousTime = current;
        return ms;
    }

    private double clamp(double value) {
        return Math.min(Math.max(value, m_minimumOutput), m_maximumOutput);
    }

    private double error(double goal, double current) {
        return goal - current;
    }

    private double applyDeadband(double value) {
        return Math.max(Math.abs(value), m_deadband) * Math.signum(value);
    }
}
