package gbmotion.PIDController;

import gbmotion.base.controller.Input;
import gbmotion.base.controller.IterativeController;
import gbmotion.base.controller.Output;

public class PIDController extends IterativeController<Double, Double> {

	private double m_lastError = 0;
	private double m_totalError = 0;

	private final double m_kP;
	private final double m_kI;
	private final double m_kD;
	private final double m_kF;

	/**
	 * 
	 * @param in
	 * @param out
	 * @param destination
	 * @param kp
	 * @param ki
	 * @param kd
	 * @param kf
	 * @param name
	 */
	public PIDController(Input<Double> in, Output<Double> out, Double destination, double kp, double ki, double kd,
			double kf, double toleranceDist) {
		super(in, out, destination, "PID Controller");
		m_kP = kp;
		m_kI = ki;
		m_kD = kd;
		m_kF = kf;
		setTolerance(new AbsoluteTolerance(output -> Math.abs(output), toleranceDist));
	}

	/**
	 * 
	 * @param in
	 * @param out
	 * @param destination
	 * @param kp
	 * @param name
	 */
	public PIDController(Input<Double> in, Output<Double> out, Double destination, double kp, double toleranceDist) {
		super(in, out, destination, "PID Controller");
		m_kP = kp;
		m_kI = 0;
		m_kD = 0;
		m_kF = 0;
		setTolerance(new AbsoluteTolerance(output -> Math.abs(output), toleranceDist));
	}

	/**
	 * 
	 * @param in
	 * @param out
	 * @param destination
	 * @param kp
	 * @param ki
	 * @param name
	 */
	public PIDController(Input<Double> in, Output<Double> out, Double destination, double kp, double ki, double toleranceDist) {
		super(in, out, destination, "PID Controller");
		m_kP = kp;
		m_kI = ki;
		m_kD = 0;
		m_kF = 0;
		setTolerance(new AbsoluteTolerance(output -> Math.abs(output), toleranceDist));
	}

	/**
	 * 
	 * @param in
	 * @param out
	 * @param destination
	 * @param kp
	 * @param ki
	 * @param kd
	 * @param name
	 */
	public PIDController(Input<Double> in, Output<Double> out, Double destination, double kp, double ki, double kd,
			double toleranceDist) {
		super(in, out, destination, "PID Controller");
		m_kP = kp;
		m_kI = ki;
		m_kD = kd;
		m_kF = 0;
		setTolerance(new AbsoluteTolerance(output -> Math.abs(output), toleranceDist));
	}

	@Override
	public Double calculate(Double in) {
		m_totalError += getError(in);
		double calculatedValue = calculatePIDvalue(getError(in), m_lastError, m_totalError, m_destination, m_kP, m_kI,
				m_kD, m_kF);
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
	private double calculatePIDvalue(double currentError, double lastError, double totalError, double destination,
			double Kp, double Ki, double Kd, double Kf) {
		return Kp * currentError + Ki * totalError + Kd * (currentError - lastError) + Kf * destination;
	}

	@Override
	public Double getError(Double input, Double dest) {
		return dest - input;
	}
	
	public double getKp() {
		return m_kP;
	}
	
	public double getKd() {
		return m_kD;
	}
	
	public double getKi() {
		return m_kI;
	}
	
	public double getKf() {
		return m_kF;
	}
}
