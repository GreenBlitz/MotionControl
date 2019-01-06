package org.greenblitz.example.utils;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class CANRobotDrive {
	private WPI_TalonSRX m_frontLeft, m_rearLeft, m_frontRight, m_rearRight;
	private double m_outputScale = 1;
	private double m_powerLimit = 1;

	public CANRobotDrive(WPI_TalonSRX frontLeft, WPI_TalonSRX rearLeft, WPI_TalonSRX frontRight, WPI_TalonSRX rearRight) {
		m_frontLeft = frontLeft;
		m_rearLeft = rearLeft;
		m_frontRight = frontRight;
		m_rearRight = rearRight;
	}

	public CANRobotDrive(int frontLeft, int rearLeft, int frontRight, int rearRight) {
		this(new WPI_TalonSRX(frontLeft), new WPI_TalonSRX(rearLeft), new WPI_TalonSRX(frontRight),
				new WPI_TalonSRX(rearRight));
	}

	public enum TalonID {
		FRONT_LEFT, FRONT_RIGHT, REAR_LEFT, REAR_RIGHT
	}

	public WPI_TalonSRX getTalon(TalonID id) {
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

	public void setInvertedMotor(TalonID id, boolean inverted) {
		switch (id) {
		case FRONT_LEFT:
			m_frontLeft.setInverted(inverted);
			break;
		case FRONT_RIGHT:
			m_rearRight.setInverted(inverted);
			break;
		case REAR_LEFT:
			m_rearLeft.setInverted(inverted);
			break;
		case REAR_RIGHT:
			m_rearRight.setInverted(inverted);
			break;
		}
	}

	public void invert(TalonID id) {
	    setInvertedMotor(id, true);
    }

    public void clear(TalonID id) {
	    setInvertedMotor(id, false);
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

		moveValue = limit(moveValue);
		rotateValue = limit(rotateValue);

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
		setLeftRightMotorOutputs(leftValue, rightValue);
	}

	private double limit(double value) {
		if (value > 0)
			return Math.min(value, m_powerLimit);
		return Math.max(value, -m_powerLimit);
	}

	public void setLeftRightMotorOutputs(double leftOutput, double rightOutput) {
		m_frontLeft.set(limit(leftOutput) * m_outputScale);
		m_rearLeft.set(limit(leftOutput) * m_outputScale);
		m_frontRight.set(limit(rightOutput) * m_outputScale);
		m_rearRight.set(limit(rightOutput) * m_outputScale);
	}
}