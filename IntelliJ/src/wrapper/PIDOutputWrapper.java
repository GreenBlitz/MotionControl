package wrapper;

import base.Output;
import edu.wpi.first.wpilibj.PIDOutput;

/**
 * Created by karlo on 14/12/2017.
 * Wraps wpilib PIDOutput
 */
public class PIDOutputWrapper implements Output {
    private final PIDOutput m_output;
    
    public PIDOutputWrapper(PIDOutput output) { m_output = output; }

    public void use(double[] output) { m_output.pidWrite(output[0]); }

    public PIDOutput wrapped() { return m_output; }

    @Override
    public void use(Object output) {

    }

    @Override
    public Object noPower() {
        return null;
    }

    @Override
    public void stop() {

    }
}