package gbmotion.base;

import org.usfirst.frc.team4590.robot.RobotMap;

import gbmotion.util.CANRobotDrive;
import gbmotion.util.CANRobotDrive.TalonID;
import gbmotion.util.RobotStats;
import gbmotion.util.SmartEncoder;
import gbmotion.util.SmartTalon;

public class DrivePort {

	public static final DrivePort DEFAULT = new DrivePort(new SmartTalon(RobotMap.CHASSIS_FRONT_LEFT_MOTOR_PORT),
			new SmartTalon(RobotMap.CHASSIS_REAR_LEFT_MOTOR_PORT),
			new SmartTalon(RobotMap.CHASSIS_FRONT_RIGHT_MOTOR_PORT),
			new SmartTalon(RobotMap.CHASSIS_REAR_RIGHT_MOTOR_PORT));

	protected CANRobotDrive m_robotDrive;
	private SmartEncoder m_leftEncoder;
	private SmartEncoder m_rightEncoder;

	protected DrivePort() {
	}

	/**
	 * 
	 * @param frontLeftMotor
	 * @param rearLeftMotor
	 * @param frontRightMotor
	 * @param rearRightMotor
	 */
	public DrivePort(SmartTalon frontLeftMotor, SmartTalon rearLeftMotor, SmartTalon frontRightMotor,
			SmartTalon rearRightMotor) {
		m_robotDrive = new CANRobotDrive(frontLeftMotor, rearLeftMotor, frontRightMotor, rearRightMotor);
		m_leftEncoder = new SmartEncoder(rearLeftMotor, RobotStats.Cerberous.EncoderMetreScale.LEFT_POWER.value,
				RobotStats.Cerberous.EncoderMetreScale.LEFT_VELOCITY.value);
		m_rightEncoder = new SmartEncoder(rearRightMotor, RobotStats.Cerberous.EncoderMetreScale.RIGHT_POWER.value,
				RobotStats.Cerberous.EncoderMetreScale.RIGHT_VELOCITY.value);
	}

	/**
	 * Right encoder is the closer to the robot safety switch, Left is the other
	 * @param dir Direction- true for right, false for left
	 * @return The encoder in given direction
	 */
	public SmartEncoder getEncoder(boolean dir) {
		return dir ? m_rightEncoder : m_leftEncoder;
	}
	
	/**
	 * @param leftValue
	 * @param rightValue
	 * @param squaredInputs
	 * @see edu.wpi.first.wpilibj.RobotDrive#tankDrive(double, double, boolean)
	 */
	public void tankDrive(double leftValue, double rightValue, boolean squaredInputs) {
		m_robotDrive.tankDrive(leftValue, rightValue, squaredInputs);
	}

	/**
	 * @param leftValue
	 * @param rightValue
	 * @see edu.wpi.first.wpilibj.RobotDrive#tankDrive(double, double)
	 */
	public void tankDrive(double leftValue, double rightValue) {
		m_robotDrive.tankDrive(leftValue, rightValue);
	}

	/**
	 * @param moveValue
	 * @param rotateValue
	 * @param squaredInputs
	 * @see edu.wpi.first.wpilibj.RobotDrive#arcadeDrive(double, double,
	 *      boolean)
	 */
	public void arcadeDrive(double moveValue, double rotateValue, boolean squaredInputs) {
		m_robotDrive.arcadeDrive(moveValue, rotateValue, squaredInputs);
	}

	/**
	 * @param moveValue
	 * @param rotateValue
	 * @see edu.wpi.first.wpilibj.RobotDrive#arcadeDrive(double, double)
	 */
	public void arcadeDrive(double moveValue, double rotateValue) {
		m_robotDrive.arcadeDrive(moveValue, rotateValue);
	}

	/**
	 * @param obj
	 * @return
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		return m_robotDrive.equals(obj);
	}

	/**
	 * @param leftOutput
	 * @param rightOutput
	 * @see edu.wpi.first.wpilibj.RobotDrive#setLeftRightMotorOutputs(double,
	 *      double)
	 */
	public void setLeftRightMotorOutputs(double leftOutput, double rightOutput) {
		m_robotDrive.setLeftRightMotorOutputs(leftOutput, rightOutput);
	}

	/**
	 * @param motor
	 * @param isInverted
	 * @see edu.wpi.first.wpilibj.RobotDrive#setInvertedMotor(edu.wpi.first.wpilibj.RobotDrive.MotorType,
	 *      boolean)
	 */
	public void setInvertedMotor(TalonID motor, boolean isInverted) {
		m_robotDrive.setInvetedMotor(motor, isInverted);
	}

	/**
	 * @param maxOutput
	 * @see edu.wpi.first.wpilibj.RobotDrive#setMaxOutput(double)
	 */
	public void setPowerLimit(double maxOutput) {
		m_robotDrive.setPowerLimit(maxOutput);
	}

	public SmartEncoder getEndoder(boolean dir) {
		if (dir)
			return m_rightEncoder;
		else
			return m_leftEncoder;
	}

	/**
	 * @return
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return m_robotDrive.toString();
	}
}
