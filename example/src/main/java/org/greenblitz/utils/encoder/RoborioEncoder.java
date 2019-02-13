package org.greenblitz.utils.encoder;

import edu.wpi.first.wpilibj.Encoder;

public class RoborioEncoder extends AbstractEncoder {

    private final Encoder m_encoder;

    public RoborioEncoder(int channelA, int channelB, double ticksPerMeter) {
        super(ticksPerMeter);
        m_encoder = new Encoder(channelA, channelB);
        m_encoder.setDistancePerPulse(1);
    }

    @Override
    public int getRawTicks() {
        return m_inverted * m_encoder.get();
    }

    @Override
    public int getRawSpeed() {
        return m_inverted * (int) m_encoder.getRate();
    }

    @Override
    public void reset() {
        m_encoder.reset();
    }
}
