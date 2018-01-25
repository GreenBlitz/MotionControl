package APPC;

import org.usfirst.frc.team4590.robot.RobotStats;

import base.DrivePort;
import base.EnvironmentPort;
import base.Output;

public class APPCOutput implements Output<APPController.APPDriveData> {
	private static double fullPower = 0.8;

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
		System.out.println("ratio:" + ratio + " power:" + power);
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

	/**
	 * @param output
	 *            the output to use on the engines. output[0]- power, output[1]-
	 *            curve
	 */
	@Override
	public void use(APPController.APPDriveData output) {
		System.out.println("power: " + output.power + ", curve: " + output.curve);
		curveDrive(dPort, output.power * fullPower, output.curve * fullPower);
	}

	@Override
	public APPController.APPDriveData noPower() {
		return new APPController.APPDriveData(.0, .0);
	}

	/**
	 * 
	 * @param d
	 * @param left
	 * @param right
	 */
	public void tankDrive(DrivePort d, double left, double right) {
		d.tankDrive(fullPower * left, fullPower * right, false);
	}

	/**
	 * 
	 * @param d
	 * @param left
	 * @param right
	 * @param squared
	 */
	public void tankDrive(DrivePort d, double left, double right, boolean squared) {
		d.tankDrive(fullPower * left, fullPower * right, squared);
	}

	/**
	 * 
	 * @param d
	 * @param magnitude
	 * @param curve
	 */
	public void arcadeDrive(DrivePort d, double magnitude, double curve) {
		d.arcadeDrive(fullPower * magnitude, fullPower * curve);
	}
}
