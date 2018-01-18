package wrapper;

import base.Input;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;

/**
 * Created by karlo on 14/12/2017.
 * Wraps wpilib PIDSource
 */
public class PIDSourceWrapper implements Input<Double> {
    private final PIDSource m_input;

    public PIDSourceWrapper(PIDSource input) {
        m_input = input;
    }

    @Override
    public Double recieve() { return m_input.pidGet(); }

    /**
     * Set which parameter of the device you are using as a process control variable.
     *
     * @param pidSource An enum to select the parameter.
     */
    public void setPIDSourceType(PIDSourceType pidSource) {
        m_input.setPIDSourceType(pidSource);
    }

    /**
     * Get which parameter of the device you are using as a process control variable.
     *
     * @return the currently selected PID source parameter
     */
    public PIDSourceType getPIDSourceType() {
        return m_input.getPIDSourceType();
    }

    @Override
    public String toString() {
        return "PIDSourceWrapper{" +
                "m_input=" + m_input +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PIDSourceWrapper)) return false;

        PIDSourceWrapper that = (PIDSourceWrapper) o;

        return m_input.equals(that.m_input);
    }

    @Override
    public int hashCode() {
        return m_input.hashCode();
    }
}
