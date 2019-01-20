package org.greenblitz.motion.linefollower;
import org.greenblitz.motion.pid.PIDController;

public class LineFollower extends PIDController {
    public LineFollower(double kp, double ki, double kd) {
        super(kp, ki, kd);
    }
}
