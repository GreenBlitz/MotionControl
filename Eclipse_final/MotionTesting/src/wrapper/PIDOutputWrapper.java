package wrapper;

import base.Output;
import edu.wpi.first.wpilibj.PIDOutput;

/**
 *
 * Wraps wpilib PIDOutput
 */
public class PIDOutputWrapper implements Output<Double> {
    private final PIDOutput m_output;
    
    public PIDOutputWrapper(PIDOutput output) { m_output = output; }

    public void use(double[] output) { m_output.pidWrite(output[0]); }

    public PIDOutput wrapped() { return m_output; }

    @Override
    public void use(Double output) {

    }

    @Override
    public Double noPower() {
        return 0.0;
    }
}
