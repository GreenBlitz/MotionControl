package APPC;

import org.usfirst.frc.team4590.robot.RobotStats;

import base.Output;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class APPCOutput implements Output<APPController.APPDriveData> {
	private RobotDrive m_robotDrive;

	private static double fullPower = 0.8;

	public APPCOutput(RobotDrive robotDrive) {
		m_robotDrive = robotDrive;

	}

	public void curveDrive(RobotDrive r, double power, double curve) {
		SmartDashboard.putNumber("Curve", curve);
		if (curve == 0) {
			r.tankDrive(power, power, false);
			SmartDashboard.putNumber("powerR", power);
			SmartDashboard.putNumber("powerL", power);
			return;
		}
		// test
		double d = RobotStats.HORIZONTAL_WHEEL_DIST;
		double R = 1 / Math.abs(curve);
		double ratio;
		ratio = (R - d / 2) / (R + d / 2);
		SmartDashboard.putNumber("Ratio", ratio);
		System.out.println("ratio:" + ratio + " power:" + power);
		if (curve < 0) {
			r.tankDrive(power, power * ratio, false);
			SmartDashboard.putNumber("powerL", power);
			SmartDashboard.putNumber("powerR", power * ratio);
		} else {
			r.tankDrive(power * ratio, power, false);
			SmartDashboard.putNumber("powerL", power * ratio);
			SmartDashboard.putNumber("powerR", power);
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
		curveDrive(m_robotDrive, output.power * fullPower, output.curve * fullPower);
	}

	@Override
	public APPController.APPDriveData noPower() {
		// TODO Auto-generated method stub
		return new APPController.APPDriveData(.0, .0);
	}

	public void tankDrive(double left, double right) {
		m_robotDrive.tankDrive(fullPower * left, fullPower * right, false);
	}

	public void tankDrive(double left, double right, boolean squared) {
		m_robotDrive.tankDrive(fullPower * left, fullPower * right, squared);
	}

	public void arcadeDrive(double magnitude, double curve) {
		m_robotDrive.arcadeDrive(fullPower * magnitude, fullPower * curve);
	}
}
