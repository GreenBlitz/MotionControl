package org.greenblitz.utils.encoder;

public interface IEncoder {
    double getTicksPerMeter();

    void setTicksPerMeter(double value);

    int getRawTicks();

    int getRawSpeed();

    default double getDistance() {
        return getRawTicks() / getTicksPerMeter();
    }

    default double getSpeed() {
        return getRawSpeed() / getTicksPerMeter();
    }

    void reset();

    void setInverted(boolean inverted);

    default void invert() {
        setInverted(!isInverted());
    }

    boolean isInverted();
}
