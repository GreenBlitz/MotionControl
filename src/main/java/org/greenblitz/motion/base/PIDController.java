package org.greenblitz.motion.base;

public class PIDController {

    protected double m_kp, m_kd, m_ki, m_kf;
    protected double integral;
    protected double previousError;
    protected long previousTime;

    public PIDController(double m_kp, double m_ki, double m_kd, double m_kf) {
        this.m_kp = m_kp;
        this.m_kd = m_kd;
        this.m_ki = m_ki;
        this.m_kf = m_kf;
    }

    public PIDController(double kp, double ki, double kd){
        this(kp, kd, ki, 0);
    }

    public PIDController(double kp, double ki){
        this(kp, ki, 0);
    }

    public PIDController(double kp){
        this(kp, 0);
    }

    /**
     * Calling this implies starting to use the controller
     */
    public void init(double goal, double value0){
        integral = 0;
        previousError = goal - value0;
        previousTime = System.currentTimeMillis();
    }

    /**
     *
     * @param goal
     * @param current
     * @return
     */
    public double calculatePID(double goal, double current){
        // Set e
        double err = goal - current;

        // Set de/dt
        double secsPassed = (System.currentTimeMillis() - previousTime) / 1000.0;
        double errD = (err - previousError) / (secsPassed);
        previousTime = System.currentTimeMillis();
        previousError = err;

        // Set Int(e)dt
        integral += err * secsPassed;

        return m_kp * err + m_ki * integral + m_kd * errD + m_kf * current;
    }

    /**
     *
     * @param goal
     * @param current
     * @param maxAllowedError
     * @return
     */
    public boolean isFinish(double goal, double current, double maxAllowedError){
        return Math.abs(goal - current) <= maxAllowedError;
    }

    /**
     *
     */
    public void resetIntegral(){
        integral = 0;
    }

    public double getKp() {
        return m_kp;
    }

    public void setKp(double m_kp) {
        this.m_kp = m_kp;
    }

    public double getKd() {
        return m_kd;
    }

    public void setKd(double m_kd) {
        this.m_kd = m_kd;
    }

    public double getKi() {
        return m_ki;
    }

    public void setKi(double m_ki) {
        this.m_ki = m_ki;
    }

    public double getKf() {
        return m_kf;
    }

    public void setKf(double m_kf) {
        this.m_kf = m_kf;
    }
}
