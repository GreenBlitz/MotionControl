package org.greenblitz.motion.pid;

import org.greenblitz.debug.RemoteCSVTargetBuffer;
import org.greenblitz.motion.exceptions.UninitializedPIDException;
import org.greenblitz.motion.tolerance.ITolerance;
import org.greenblitz.utils.Time;

public class PIDController {

    protected PIDObject m_obj;
    protected double m_previousTime;
    protected double m_goal;
    protected double m_previousError;
    protected double m_integral;
    protected double startTime;

    protected double m_minimumOutput;
    protected double m_maximumOutput;
    protected double m_absoluteMinimumOut;

    protected boolean configured;

    protected ITolerance m_tolerance;

    private RemoteCSVTargetBuffer PIDTarget;
    private double dataDelay = 0;
    private String targetName = "PIDTarget";

    public PIDController(PIDObject obj, ITolerance tolerance) {
        m_obj = obj;
        m_tolerance = tolerance;
        configured = false;
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

    public void configure(double curr, double goal, double limitLower, double limitUpper, double absoluteMinimumOut) {
        setGoal(goal);
        m_previousError = goal - curr;
        resetIntegralZone(0);
        configureOutputLimits(limitLower, limitUpper);
        m_previousTime = Time.getTime();
        m_absoluteMinimumOut = absoluteMinimumOut;
        startTime = Time.getTime();
        if(dataDelay != 0){
            PIDTarget = new RemoteCSVTargetBuffer(targetName, dataDelay, "time", "P", "I", "D", "kf", "PID");
        }
        configured = true;
    }

    public double getAbsoluteMinimumOut() {
        return m_absoluteMinimumOut;
    }

    public void setAbsoluteMinimumOut(double m_absoluteMinimumOut) {
        this.m_absoluteMinimumOut = m_absoluteMinimumOut;
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
        if (!configured)
            throw new UninitializedPIDException("PID - " + this + " - not configured");

        if (isFinished(current))
            return 0;

        double err = (m_goal - current) * m_obj.getInverted();
        double dt = updateTime();

        double p = m_obj.getKp() * err;

        m_integral += err * dt;
        double i = m_obj.getKi() * m_integral;

        double d = 0.0;
        if (Math.abs(dt) >= 0.001)
            d = m_obj.getKd() * (err - m_previousError) / dt;

        m_previousError = err;
        if(dataDelay != 0 ) PIDTarget.report(Time.getTime() - startTime, p, i, d, m_obj.getKf(), p + i + d + m_obj.getKf());
        return clampFully(p + i + d + m_obj.getKf());
    }

    public double clampFully(double value) {
        double calc = clamp(value);
        if (Double.isNaN(m_absoluteMinimumOut)) {
            return calc;
        }
        return Math.copySign(Math.max(Math.abs(calc), m_absoluteMinimumOut), calc);
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

    public boolean isFinished(double current) {
        return hasTolerance() && m_tolerance.onTarget(getGoal(), current);
    }

    public boolean hasTolerance() {
        return m_tolerance != null;
    }

    protected double updateTime() {
        double current = Time.getTime();
        double sec = current - m_previousTime;
        m_previousTime = current;
        return sec;
    }

    protected double clamp(double value) {
        if (Double.isNaN(m_maximumOutput + m_minimumOutput)) {
            return value;
        }
        return Math.min(Math.max(value, m_minimumOutput), m_maximumOutput);
    }

    public void setPidObject(PIDObject newPidObj){
        this.m_obj = newPidObj;
    }

    public void atEnd(){
        if(dataDelay != 0){
            PIDTarget.passToCSV(true);
        }
    }

    public void setSendData(boolean sendData){dataDelay = sendData? 0.05:0;}
    public void setDataDelay(double dataDelay, String name){
        this.dataDelay = dataDelay;
        targetName = name;
    }
}
