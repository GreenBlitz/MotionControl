package gbmotion.VelocityManager;

import org.usfirst.frc.team4590.robot.RobotStats;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import gbmotion.util.Tuple;

public class VelocityManagerTester {

	private RobotDrive m_drive;
	private Joystick m_stick;
	private VoltageController m_velocityManagerLeft;
	private VoltageController m_velocityManagerRight;
	private Tuple<Encoder, Boolean> m_leftEncoder;
	private Tuple<Encoder, Boolean> m_rightEncoder;

	private int lastEncoderValueLeft = 0;
	private int lastEncoderValueRight = 0;
	private long lastTest = System.currentTimeMillis();
	private double deltaTime;

	public VelocityManagerTester(RobotDrive drive, Joystick stick, int pastTimeImportance,
			Tuple<Encoder, Boolean> encoderLeft, Tuple<Encoder, Boolean> encoderRight) {
		m_drive = drive;
		m_leftEncoder = encoderLeft;
		m_rightEncoder = encoderRight;
		m_stick = stick;
		m_velocityManagerLeft = new VoltageController(VoltageController.TimeOption.ASAP, 0, 0, pastTimeImportance);
		m_velocityManagerRight = m_velocityManagerLeft = new VoltageController(VoltageController.TimeOption.ASAP, 0, 0,
				pastTimeImportance);
		m_velocityManagerRight.resetTimeInterval();
		m_velocityManagerLeft.resetTimeInterval();
		Thread testingThread = new Thread(this::run);
		testingThread.start();
	}

	private Tuple<Double, Double> simulateArcadeDrive(double moveValue, double rotateValue) {

		double leftMotorSpeed;
		double rightMotorSpeed;

		if (moveValue >= 0.0) {
			moveValue = moveValue * moveValue;
		} else {
			moveValue = -(moveValue * moveValue);
		}
		if (rotateValue >= 0.0) {
			rotateValue = rotateValue * rotateValue;
		} else {
			rotateValue = -(rotateValue * rotateValue);
		}

		if (moveValue > 0.0) {
			if (rotateValue > 0.0) {
				leftMotorSpeed = moveValue - rotateValue;
				rightMotorSpeed = Math.max(moveValue, rotateValue);
			} else {
				leftMotorSpeed = Math.max(moveValue, -rotateValue);
				rightMotorSpeed = moveValue + rotateValue;
			}
		} else {
			if (rotateValue > 0.0) {
				leftMotorSpeed = -Math.max(-moveValue, rotateValue);
				rightMotorSpeed = moveValue + rotateValue;
			} else {
				leftMotorSpeed = moveValue - rotateValue;
				rightMotorSpeed = -Math.max(-moveValue, -rotateValue);
			}
		}

		return new Tuple<Double, Double>(leftMotorSpeed, rightMotorSpeed);
	}

	private double getRightRate() {
		int ticks = m_rightEncoder._1.get();
		int deltaTicks = ticks - lastEncoderValueRight;
		lastEncoderValueRight = ticks;
		ticks *= m_rightEncoder._2 ? -1 : 1;
		
		return (deltaTicks * RobotStats.ENCODER_TICKS_PER_RADIAN) / deltaTime;
	}

	private double getLeftRate() {
		int ticks = m_leftEncoder._1.get();
		int deltaTicks = ticks - lastEncoderValueLeft;
		lastEncoderValueLeft = ticks;
		ticks *= m_leftEncoder._2 ? -1 : 1;
		
		return (deltaTicks * RobotStats.ENCODER_TICKS_PER_RADIAN) / deltaTime;
	}

	private void run() {
		long timeNow = System.currentTimeMillis();
		deltaTime = (timeNow - lastTest) / 1000.0;
		lastTest = timeNow;
		
		double velocity = m_stick.getRawAxis(0);
		double curve = m_stick.getRawAxis(0);
		Tuple<Double, Double> motorValues = simulateArcadeDrive(velocity, curve);
		double leftMotorPower = m_velocityManagerLeft.getOptimalPower(motorValues._1 * 3 / Math.PI, getLeftRate(), 0);
		double rightMotorPower = m_velocityManagerRight.getOptimalPower(motorValues._2 * 3 / Math.PI, getRightRate(),
				0);

		m_drive.tankDrive(leftMotorPower, rightMotorPower);
	}

}
