package org.greenblitz.utils;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class CANRobotDrive {
	private TalonSRX m_frontLeft, m_rearLeft, m_frontRight, m_rearRight;
	private int m_frontLeftInverted = 1, m_rearLeftInverted = 1, m_frontRightInverted = 1, m_rearRightInverted = 1;
	private double m_outputScale = 1;
	private double m_powerLimit = 1;

	public CANRobotDrive(TalonSRX frontLeft, TalonSRX rearLeft, TalonSRX frontRight, TalonSRX rearRight) {
		m_frontLeft = frontLeft;
		m_rearLeft = rearLeft;
		m_frontRight = frontRight;
		m_rearRight = rearRight;
	}

	public CANRobotDrive(int frontLeft, int rearLeft, int frontRight, int rearRight) {
		this(new TalonSRX(frontLeft), new TalonSRX(rearLeft), new TalonSRX(frontRight),
				new TalonSRX(rearRight));
	}

	public enum TalonID {
		FRONT_LEFT, FRONT_RIGHT, REAR_LEFT, REAR_RIGHT
	}

	public TalonSRX getTalon(TalonID id) {
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
			//m_frontLeft.setInverted(inverted);
			m_frontLeftInverted = inverted ? -1 : 1;
			break;
		case FRONT_RIGHT:
			//m_rearRight.setInverted(inverted);
			m_frontRightInverted = inverted ? -1 : 1;
			break;
		case REAR_LEFT:
			//m_rearLeft.setInverted(inverted);
			m_rearLeftInverted = inverted ? -1 : 1;
			break;
		case REAR_RIGHT:
			//m_rearRight.setInverted(inverted);
			m_rearRightInverted = inverted ? -1 : 1;
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
		m_frontLeft.set(ControlMode.PercentOutput, limit(leftOutput) * m_outputScale * m_frontLeftInverted);
		m_rearLeft.set(ControlMode.PercentOutput, limit(leftOutput) * m_outputScale * m_rearLeftInverted);
		m_frontRight.set(ControlMode.PercentOutput, limit(rightOutput) * m_outputScale * m_frontRightInverted);
		m_rearRight.set(ControlMode.PercentOutput, limit(rightOutput) * m_outputScale * m_rearRightInverted);
	}
}