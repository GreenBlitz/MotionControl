package org.greenblitz.motion.utils.CTRE;

public class CANRobotDrive {
	private SmartTalon m_frontLeft, m_rearLeft, m_frontRight, m_rearRight;
	private double m_outputScale = 1;
	private double m_powerLimit = 1;
	private int m_frontLeftInverted = 1, m_rearLeftInverted = 1, m_frontRightInverted = -1, m_rearRightInverted = -1;

	public CANRobotDrive(SmartTalon frontLeft, SmartTalon rearLeft, SmartTalon frontRight, SmartTalon rearRight) {
		m_frontLeft = frontLeft;
		m_rearLeft = rearLeft;
		m_frontRight = frontRight;
		m_rearRight = rearRight;
	}

	public CANRobotDrive(int frontLeft, int rearLeft, int frontRight, int rearRight) {
		this(new SmartTalon(frontLeft), new SmartTalon(rearLeft), new SmartTalon(frontRight),
				new SmartTalon(rearRight));
	}

	public static enum TalonID {
		FRONT_LEFT, FRONT_RIGHT, REAR_LEFT, REAR_RIGHT
	}

	public SmartTalon getTalon(TalonID id) {
		switch (id) {
		case FRONT_LEFT:
			return m_frontLeft;
		case FRONT_RIGHT:
			return m_frontRight;
		case REAR_LEFT:
			return m_rearLeft;
		case REAR_RIGHT:
			return m_rearRight;
		}
		return null;
	}

	public void setInvetedMotor(TalonID id, boolean inverted) {
		switch (id) {
		case FRONT_LEFT:
			m_frontLeftInverted = inverted ? -1 : 1;
			break;
		case FRONT_RIGHT:
			m_rearLeftInverted = inverted ? -1 : 1;
			break;
		case REAR_LEFT:
			m_frontRightInverted = inverted ? -1 : 1;
			break;
		case REAR_RIGHT:
			m_rearRightInverted = inverted ? -1 : 1;
			break;
		}
	}

	public void setOutputScale(double maxOutput) {
		m_outputScale = maxOutput;
	}

	public void setPowerLimit(double powerLimit) {
		m_powerLimit = powerLimit;
	}

	public void arcadeDrive(double moveValue, double rotateValue) {
		double leftMotorSpeed;
		double rightMotorSpeed;

		moveValue = limit(-moveValue);
		rotateValue = limit(rotateValue);

		moveValue = Math.copySign(moveValue * moveValue, moveValue);
		rotateValue = Math.copySign(rotateValue * rotateValue, rotateValue);

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
		setLeftRightMotorOutputs(leftMotorSpeed, rightMotorSpeed);
	}

	public void tankDrive(double leftValue, double rightValue) {
		leftValue = limit(-leftValue);
		rightValue = limit(-rightValue);

		leftValue = Math.copySign(leftValue * leftValue, leftValue);
		rightValue = Math.copySign(rightValue * rightValue, rightValue);

		setLeftRightMotorOutputs(leftValue, rightValue);
	}

	private double limit(double value) {
		if (value > 0)
			return Math.min(value, m_powerLimit);
		return Math.max(value, -m_powerLimit);
	}

	public void setLeftRightMotorOutputs(double leftOutput, double rightOutput) {
		m_frontLeft.set(m_frontLeftInverted * limit(leftOutput) * m_outputScale);
		m_rearLeft.set(m_rearLeftInverted * limit(leftOutput) * m_outputScale);
		m_frontRight.set(m_frontRightInverted * limit(rightOutput) * m_outputScale);
		m_rearRight.set(m_rearRightInverted * limit(rightOutput) * m_outputScale);
	}
}