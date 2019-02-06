package org.greenblitz.motion.pid;

public class PIDObject {

    protected double m_kp, m_kd, m_ki, m_kf;

    @Override
    public String toString() {
        return "PIDObject{" +
                "m_kp=" + m_kp +
                ", m_kd=" + m_kd +
                ", m_ki=" + m_ki +
                ", m_kf=" + m_kf +
                ", integral=" + integral +
                '}';
    }

    protected double integral;
    protected double previousError;

    public PIDObject(double m_kp, double m_ki, double m_kd, double m_kf) {
        this.m_kp = m_kp;
        this.m_kd = m_kd;
        this.m_ki = m_ki;
        this.m_kf = m_kf;
    }

    public PIDObject(double kp, double ki, double kd){
        this(kp, kd, ki, 0);
    }

    public PIDObject(double kp, double ki){
        this(kp, ki, 0);
    }

    public PIDObject(double kp){
        this(kp, 0);
    }

    /**
     * Calling this implies starting to use the controller
     */
    public void init(double goal, double value0){
        resetIntegral();
        previousError = goal - value0;
    }

    /**
     *
     * @param goal
     * @param current
     * @param dt
     * @return
     */
    public double calculatePID(double goal, double current, double dt){
        // Set e
        double err = goal - current;

        // Set de/dt
        double errD = (err - previousError) / dt;
        previousError = err;

        // Set Int(e)dt
        integral += err * dt;

        return m_kp * err + m_ki * integral + m_kd * errD + m_kf * goal;
    }

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
