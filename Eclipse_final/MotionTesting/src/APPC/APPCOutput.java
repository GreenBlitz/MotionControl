package APPC;

import org.usfirst.frc.team4590.robot.Robot;
import org.usfirst.frc.team4590.robot.RobotStats;

import base.DrivePort;
import base.EnvironmentPort;
import base.Output;

public class APPCOutput implements Output<APPController.APPDriveData> {
	private static final double FULL_POWER = 0.8 * 0.6;
	private static final double ROTATION_FACTOR = RobotStats.VERTICAL_WHEEL_DIST / RobotStats.HORIZONTAL_WHEEL_DIST;

	private EnvironmentPort ePort = EnvironmentPort.DEFAULT;
	private DrivePort dPort = DrivePort.DEFAULT;

	public APPCOutput(EnvironmentPort ePort, DrivePort dPort) {
		this.ePort = ePort;
		this.dPort = dPort;
	}

	public APPCOutput() {
	}

	public void setDrivePort(DrivePort dPort) {
		this.dPort = dPort;
	}

	public void setEnvironmentPort(EnvironmentPort ePort) {
		this.ePort = ePort;
	}

	/**
	 * Drive by curve and max power
	 * 
	 * @param r
	 * @param power
	 * @param curve
	 */
	@Deprecated
	public void curveDrive(DrivePort r, double power, double curve) {
		ePort.putNumber("Curve", curve);
		if (curve == 0) {
			r.tankDrive(power, power, false);
			ePort.putNumber("powerR", power);
			ePort.putNumber("powerL", power);
			return;
		}

		double d = RobotStats.HORIZONTAL_WHEEL_DIST;
		double R = 1 / Math.abs(curve);
		double ratio;
		ratio = (R - d / 2) / (R + d / 2);
		ePort.putNumber("Ratio", ratio);
		Robot.p.println(getClass(), "ratio: " + ratio + ", power: " + power);
		if (curve < 0) {
			r.tankDrive(power, power * ratio, false);
			ePort.putNumber("powerL", power);
			ePort.putNumber("powerR", power * ratio);
		} else {
			r.tankDrive(power * ratio, power, false);
			ePort.putNumber("powerL", power * ratio);
			ePort.putNumber("powerR", power);
		}
	}

	public static void cordDrive(DrivePort r, double maxPower, double power, double[] dXdY) {
		cordDrive(r, maxPower, dXdY[0], dXdY[1]);
	}

	public static double[] calculatePelegDrive(double maxPower, double dX, double dY) {
		// needs a good angular velocity manager
		// dX *= ROTATION_FACTOR; // should be removed if possible
		double left, right;

		/*
		 * left = power * (dY - dX); right = power * (dY + dX); if
		 * (Math.abs(left) > maxPower || Math.abs(right) > maxPower) { double
		 * ratio = maxPower / Math.max(Math.abs(right), Math.abs(left)); left *=
		 * ratio; right *= ratio;
		 */
		double rotationPowerLeft = dX * ROTATION_FACTOR;
		double rotationPowerRight = -rotationPowerLeft;
		// double powerUnscaledLeft = dY + rotationPowerLeft;
		// double powerUnscaledRight = dY + rotationPowerRight;

		double powerUnscaledLeft = dY + sign(dY) * rotationPowerLeft;
		double powerUnscaledRight = dY + sign(dY) * rotationPowerRight;

		if (Math.abs(powerUnscaledLeft) > Math.abs(powerUnscaledRight)) {
			left = maxPower * sign(powerUnscaledLeft);
			right = left * powerUnscaledRight / powerUnscaledLeft;
		} else if (Math.abs(powerUnscaledLeft) < Math.abs(powerUnscaledRight)) {
			right = maxPower * sign(powerUnscaledRight);
			left = right * powerUnscaledLeft / powerUnscaledRight;
		} else {
			// right = powerUnscaledRight != 0 ? maxPower : 0;
			// left = powerUnscaledLeft != 0 ? maxPower : 0;
			left = maxPower * Math.signum(powerUnscaledLeft);
			right = maxPower * Math.signum(powerUnscaledRight);
		}

		Robot.p.warnln(APPCOutput.class, "power: left = " + left + ", right = " + right);
		return new double[] { left, right };
	}

	public static void cordDrive(DrivePort r, double maxPower, double dX, double dY) {
		double[] values = calculatePelegDrive(maxPower, dX, dY);
		r.tankDrive(values[0], values[1], false);
	}

	/**
	 * @param output
	 *            the output to use on the engines. output[0]- power, output[1]-
	 *            curve
	 */
	@Override
	public void use(APPController.APPDriveData output) {
		Robot.p.println(getClass(), "power: " + output.power + ", x diff: " + output.dx + ", y diff: " + output.dy);
		cordDrive(dPort, output.power * FULL_POWER, output.dx, output.dy);
	}

	@Override
	public APPController.APPDriveData noPower() {
		return APPController.APPDriveData.of(.0, .0, .0);
	}

	/**
	 * 
	 * @param d
	 * @param left
	 * @param right
	 */
	public static void tankDrive(DrivePort d, double left, double right) {
		d.tankDrive(FULL_POWER * left, FULL_POWER * right, false);
	}

	/**
	 * 
	 * @param d
	 * @param left
	 * @param right
	 * @param squared
	 */
	public void tankDrive(DrivePort d, double left, double right, boolean squared) {
		d.tankDrive(FULL_POWER * left, FULL_POWER * right, squared);
	}

	/**
	 * 
	 * @param d
	 * @param magnitude
	 * @param curve
	 */
	public void arcadeDrive(DrivePort d, double magnitude, double curve) {
		d.arcadeDrive(FULL_POWER * magnitude, FULL_POWER * curve);
	}
	
	/**
	 * Sometimes we just prefer this version over usual {@link Math#signum(double)}
	 * @param num 
	 * @return {@code num}'s sign
	 */
	private static int sign(double num) {
		return num < 0 ? -1 : 1;
	}
}
