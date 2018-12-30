package org.greenblitz.motion.base;

public interface IMotor {
    /**
     * Sets the motor power
     *
     * @param power percent of motor power (scaled between -1 and 1)
     */
    void set(double power);
}
