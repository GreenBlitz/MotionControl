package org.greenblitz.motion.utils;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

/**
 * This class has many functions that make using an encoder much simpler.
 * The class uses the encoders prebuilt into the TalonSRX for all calculations.
 *
 * @see edu.wpi.first.wpilibj.Encoder
 * @see TalonSRX
 */

public class SmartEncoder {
    private double m_lastVelocity = 0;
    private int m_lastVelocityTicks = 0;
    private long m_lastVelocityTime = 0;
    private final TalonSRX m_talon;
    private final double m_ticksPerMeter;

    /**
     * This constructor receives a TalonSRX and the ticks per meter of the Talon
     * It also checks to see if the ticks per meter are valid as well.
     * @param talon A TalonSRX object which is used of it's encoder. (m_talon)
     * @param ticksPerMeter A final double of the ticks per meter the talon feels per meter of movement.
     */
    public SmartEncoder(TalonSRX talon, double ticksPerMeter) {
        if (ticksPerMeter == +0.0 || !Double.isFinite(ticksPerMeter) || ticksPerMeter == -0.0)
            throw new IllegalArgumentException("invalid ticks per meter value '" + ticksPerMeter + "'");

        m_talon = talon;
        m_ticksPerMeter = ticksPerMeter;
    }

    /**
     * This function returns the amount of ticks that have been felt by encoder since it was last reset.
     *
     * @return The amount of ticks felt by the talon.
     */
    public int getTicks() {
        return m_talon.getSensorCollection().getQuadraturePosition();
    }

    /**
     * This function returns the amount of meters that the encoder has felt, by dividing tick by the ticks per meter value.
     *
     * @return The ticks felt by the talon divided by the ticks it feels per meter.
     */
    public double getDistance() {
        return getTicks() / m_ticksPerMeter;
    }

    /**
     * This function returns the velocity felt by the encoder divided by the ticks per meter.
     *
     * @return The velocity felt by the talon divided by the ticks per meter.
     */
    public double getSpeed() {
        return ((double) m_talon.getSensorCollection().getQuadratureVelocity()) /*testGetSpeed()*/ / m_ticksPerMeter;
    }

    private double testGetSpeed() {
        int distanceDifference = getTicks() - m_lastVelocityTicks;
        long timeDifference = System.currentTimeMillis() - m_lastVelocityTime;

        m_lastVelocityTicks = getTicks();
        m_lastVelocityTime = System.currentTimeMillis();

        if (timeDifference != 0 )
            m_lastVelocity = distanceDifference / timeDifference;
        return m_lastVelocity;
    }

    /**
     * This function resets the encoder
     *
     * @return Error code if the encoder could not be reset, otherwise resets the encoder.
     */
    public ErrorCode reset() {
        ErrorCode ec = m_talon.getSensorCollection().setQuadraturePosition(0, 100);
        if (ec != ErrorCode.OK) {
            System.err.println("error occured while reseting encoder '" + m_talon.getHandle() + "': " + ec);
        }
        System.err.println("reset encoder");
        return getTicks() == 0 ? ec : reset();
    }
}