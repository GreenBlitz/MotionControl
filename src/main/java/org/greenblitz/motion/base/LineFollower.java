package org.greenblitz.motion.base;

public class LineFollower extends PIDController {
    public LineFollower (double m_kp, double m_ki, double m_kd) {
        super(m_kp, m_ki, m_kd);
    }


}
