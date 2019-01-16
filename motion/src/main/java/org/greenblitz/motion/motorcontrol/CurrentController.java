package org.greenblitz.motion.motorcontrol;

import org.greenblitz.motion.pid.PIDController;

// TODO
public class CurrentController extends PIDController {


    public CurrentController(double m_kp, double m_ki, double m_kd, double m_kf) {
        super(m_kp, m_ki, m_kd, m_kf);
    }

    public CurrentController(double kp, double ki, double kd) {
        super(kp, ki, kd);
    }

    public CurrentController(double kp, double ki) {
        super(kp, ki);
    }

    public CurrentController(double kp) {
        super(kp);
    }
}
