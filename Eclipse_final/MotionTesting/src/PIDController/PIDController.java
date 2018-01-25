package PIDController;

import base.Input;
import base.IterativeController;
import base.Output;

public class PIDController extends IterativeController<Double, Double> {

    private double m_lastError = 0;
    private double m_totalError = 0;

    double m_kP;
    double m_kI;
    double m_kD;
    double m_kF;

    public PIDController(Input<Double> in, Output<Double> out, Double destination,
    		double kp, double ki, double kd, double kf, String name) {
        super(in, out, destination, name);
        m_kP = kp;
        m_kI = ki;
        m_kD = kd;
        m_kF = kf;
    }

    public PIDController(Input<Double> in, Output<Double> out, Double destination, double kp, String name) {
        super(in, out, destination, name);
        m_kP = kp;
        m_kI = 0;
        m_kD = 0;
        m_kF = 0;
    }

    public PIDController(Input<Double> in, Output<Double> out, Double destination, double kp, double ki, String name) {
        super(in, out, destination, name);
        m_kP = kp;
        m_kI = ki;
        m_kD = 0;
        m_kF = 0;
    }

    public PIDController(Input<Double> in, Output<Double> out, Double destination,
    		double kp, double ki, double kd, String name) {
        super(in, out, destination, name);
        m_kP = kp;
        m_kI = ki;
        m_kD = kd;
        m_kF = 0;
    }


    @Override
    public Double calculate(Double in) {
        m_totalError += getError(in);
        double calculatedValue = calculatePIDvalue(
        		getError(in), m_lastError, m_totalError, m_destination,
        		m_kP, m_kI, m_kD, m_kF);
        m_lastError = getError(in);
        return calculatedValue;
    }

    /**
     * 
     * @param currentError
     * @param lastError
     * @param totalError
     * @param destination
     * @param Kp
     * @param Ki
     * @param Kd
     * @param Kf
     * @return
     */
    private double calculatePIDvalue(
    		double currentError, double lastError, double totalError, double destination,
    		double Kp, double Ki, double Kd, double Kf) {
    	return Kp * currentError + Ki * totalError + Kd * (currentError - lastError) +
    			Kf * destination;
    }

	@Override
	public Double getError(Double input, Double dest) {
		return dest - input;
	}

    
    
}
