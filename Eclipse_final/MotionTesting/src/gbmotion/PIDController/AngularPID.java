package gbmotion.PIDController;

import gbmotion.base.controller.Input;
import gbmotion.base.controller.Output;

public class AngularPID extends PIDController {

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
	public AngularPID(Input<Double> in, Output<Double> out, Double destination, double kp, double ki, double kd,
			double kf, double toleranceDist) {
		super(in, out, destination, kp, ki, kd, toleranceDist);
	}

	/**
	 * 
	 * @param in
	 * @param out
	 * @param destination
	 * @param kp
	 * @param name
	 */
	public AngularPID(Input<Double> in, Output<Double> out, Double destination, double kp, double toleranceDist) {
		super(in, out, destination, kp, toleranceDist);
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
	public AngularPID(Input<Double> in, Output<Double> out, Double destination, double kp, double ki,
			double toleranceDist) {
		super(in, out, destination, kp, ki, toleranceDist);
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
	public AngularPID(Input<Double> in, Output<Double> out, Double destination, double kp, double ki, double kd,
			double toleranceDist) {
		super(in, out, destination, kp, ki, kd, toleranceDist);
	}

	@Override
	public Double getError(Double input, Double dest) {
		double res = dest - input;
		while (res > Math.PI) {
			res -= 2 * Math.PI;
		}
		while (res < Math.PI) {
			res += 2 * Math.PI;
		}
		return res;
	}
}
