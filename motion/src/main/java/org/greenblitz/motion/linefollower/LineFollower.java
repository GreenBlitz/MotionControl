package org.greenblitz.motion.linefollower;

import org.greenblitz.motion.pid.PIDController;
import org.greenblitz.motion.pid.PIDObject;

public class LineFollower extends PIDController {
    public LineFollower(double kp, double ki, double kd) {
        super(new PIDObject(kp, ki, kd));
    }
}
