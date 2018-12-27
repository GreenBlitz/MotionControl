package org.greenblitz.motion.base;

public interface IEncoder {
    /**
     *
     * @return raw encoder ticks
     */
    int getTicks();

    /**
     *
     * @return measured distance
     */
    double getDistance();

    /**
     *
     * @return raw encoder tick rate
     */
    int getTickRate();

    /**
     *
     * @return measured velocity
     */
    double getVelocity();

    /**
     * Resets the encoder
     */
    void reset();
}
