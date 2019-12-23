package org.greenblitz.motion.pid;

import org.greenblitz.motion.tolerance.ITolerance;

public class CollapsingPIDController extends PIDController {

    protected double ICollapseThreshold = 0;

    public CollapsingPIDController(PIDObject obj, ITolerance tolerance) {
        super(obj, tolerance);
    }

    public CollapsingPIDController(PIDObject obj, double thresh) {
        super(obj);
        ICollapseThreshold = thresh;
    }

    public CollapsingPIDController(double kP, double kI, double kD, double kF) {
        super(kP, kI, kD, kF);
    }

    public CollapsingPIDController(double kP, double kI, double kD) {
        super(kP, kI, kD);
    }

    @Override
    public double calculatePID(double current) {
        if (!configured)
            throw new RuntimeException("PID - " + this + " - not configured");

        if (isFinished(current))
            return 0;

        var err = m_goal - current;
        var dt = updateTime();

        var p = m_obj.getKp() * err;

        m_integral += err * dt;
        var i = m_obj.getKi() * m_integral;

        if (err < ICollapseThreshold){
            m_integral = 0;
        }

        var d = m_obj.getKd() * (err - m_previousError) / dt;

        m_previousError = err;
        double calc = clamp(p + i + d + m_obj.getKf());
        return Math.max(Math.abs(calc), m_absoluteMinimumOut) * Math.signum(calc);
    }

}
