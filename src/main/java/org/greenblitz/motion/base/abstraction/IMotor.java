package org.greenblitz.motion.base.abstraction;

public interface IMotor {
    /**
     * Sets the motor power
     *
     * @param power percent of motor power (scaled between -1 and 1)
     */
    void set(double power);

    default void stop(){
        set(0);
    }
}
