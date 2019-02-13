package org.greenblitz.utils.encoder;

public abstract class AbstractEncoder implements IEncoder {
    protected double m_ticksPerMeter;
    protected int m_inverted = 1;

    protected AbstractEncoder(double ticksPerMeter) {
        if (ticksPerMeter == 0.0 || !Double.isFinite(ticksPerMeter))
            throw new IllegalArgumentException("invalid ticks per meter value '" + ticksPerMeter + "'");

        m_ticksPerMeter = ticksPerMeter;
    }

    @Override
    public double getTicksPerMeter() {
        return m_ticksPerMeter;
    }

    @Override
    public void setTicksPerMeter(double value) {
        m_ticksPerMeter = value;
    }

    @Override
    public double getDistance() {
        return getRawTicks() / getTicksPerMeter();
    }

    @Override
    public double getSpeed() {
        return getRawSpeed() / getTicksPerMeter();
    }

    @Override
    public void setInverted(boolean inverted) {
        m_inverted = inverted ? -1 : 1;
    }

    @Override
    public boolean isInverted() {
        return m_inverted == -1;
    }
}
