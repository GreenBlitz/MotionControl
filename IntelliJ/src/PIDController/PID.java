package PIDController;

import base.Input;
import base.IterativeController;
import base.Output;
import edu.wpi.first.wpilibj.PIDController;

public class PID extends IterativeController<Double, Double> {

    double m_lastError = 0;
    double m_error = 0;
    double m_totalError = 0;

    double m_kP;
    double m_kI;
    double m_kD;
    double m_kF;

    public PID(Input<Double> in, Output<Double> out, Double destination, double kp, double ki, double kd, double kf) {
        super(in, out, destination);
        m_kP = kp;
        m_kI = ki;
        m_kD = kd;
        m_kF = kf;
    }

    public PID(Input<Double> in, Output<Double> out, Double destination, double kp) {
        super(in, out, destination);
        m_kP = kp;
        m_kI = 0;
        m_kD = 0;
        m_kF = 0;
    }

    public PID(Input<Double> in, Output<Double> out, Double destination, double kp, double ki) {
        super(in, out, destination);
        m_kP = kp;
        m_kI = ki;
        m_kD = 0;
        m_kF = 0;
    }

    public PID(Input<Double> in, Output<Double> out, Double destination, double kp, double ki, double kd) {
        super(in, out, destination);
        m_kP = kp;
        m_kI = ki;
        m_kD = kd;
        m_kF = 0;
    }


    @Override
    public void calculate() {
        double in = m_input.recieve();
        m_lastError = m_error;
        m_error = m_destination - in;
        m_totalError += m_error;
        double calculatedValue = m_kP * m_error + m_kI * m_totalError + m_kD * (m_error - m_lastError) + m_kF * m_destination;
        if (calculatedValue > m_outputUpperBound)
            calculatedValue = m_outputUpperBound;
        else if (calculatedValue < m_outputLowerBound)
            calculatedValue = m_outputLowerBound;
        m_output.use(calculatedValue);
    }

    @Override
    public void initParameters() {
        //TODO add stuff
    }
}
