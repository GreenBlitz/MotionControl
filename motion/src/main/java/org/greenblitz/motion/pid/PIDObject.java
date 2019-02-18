package org.greenblitz.motion.pid;

public class PIDObject {

    private double m_kp, m_kd, m_ki, m_kf;

    @Override
    public String toString() {
        return "PIDObject{" +
                "kp=" + m_kp +
                ", kd=" + m_kd +
                ", ki=" + m_ki +
                ", kf=" + m_kf +
                '}';
    }

    public PIDObject(double kp, double ki, double kd, double kf) {
        this.m_kp = kp;
        this.m_kd = kd;
        this.m_ki = ki;
        this.m_kf = kf;
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
