package org.greenblitz.utils.encoder;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

/**
 * This class has many functions that make using an encoder much simpler.
 * The class uses the encoders prebuilt into the TalonSRX for all calculations.
 *
 * @see edu.wpi.first.wpilibj.Encoder
 * @see TalonSRX
 */

public class TalonEncoder extends AbstractEncoder {
    private final TalonSRX m_talon;

    public TalonSRX getTalon() {
        return m_talon;
    }

    /**
     * This constructor receives a TalonSRX and the ticks per meter of the Talon
     * It also checks to see if the ticks per meter are valid as well.
     * @param talon A TalonSRX object which is used of it's encoder. (m_talon)
     * @param ticksPerMeter A final double of the ticks per meter the talon feels per meter of movement.
     */
    public TalonEncoder(TalonSRX talon, double ticksPerMeter) {
        super(ticksPerMeter);
        m_talon = talon;
    }

    /**
     * This function returns the amount of ticks that have been felt by encoder since it was last reset.
     *
     * @return The amount of ticks felt by the talon.
     */
    public int getRawTicks() {
        return m_inverted * m_talon.getSensorCollection().getQuadraturePosition();
    }


    /**
     * This function resets the encoder
     */
    public void reset() {
        m_talon.getSensorCollection().setQuadraturePosition(0, 30);
    }

    @Override
    public int getRawSpeed() {
        return m_inverted * m_talon.getSensorCollection().getQuadratureVelocity() * 10;
    }
}