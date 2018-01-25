package base;

import org.usfirst.frc.team4590.robot.RobotMap;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.RobotDrive.MotorType;
import edu.wpi.first.wpilibj.SpeedController;

public class DrivePort {
	public static final DrivePort DEFAULT = new DrivePort(new CANTalon(RobotMap.CHASSIS_FRONT_LEFT_MOTOR_PORT),
			new CANTalon(RobotMap.CHASSIS_REAR_LEFT_MOTOR_PORT), new CANTalon(RobotMap.CHASSIS_FRONT_RIGHT_MOTOR_PORT),
			new CANTalon(RobotMap.CHASSIS_REAR_RIGHT_MOTOR_PORT));

	protected RobotDrive m_robotDrive;

	protected DrivePort() {
	}

	public DrivePort(SpeedController frontLeftMotor, SpeedController rearLeftMotor, SpeedController frontRightMotor,
			SpeedController rearRightMotor) {
		m_robotDrive = new RobotDrive(frontLeftMotor, rearLeftMotor, frontRightMotor, rearRightMotor);
	}

	/**
	 * @param outputMagnitude
	 * @param curve
	 * @see edu.wpi.first.wpilibj.RobotDrive#drive(double, double)
	 */
	public void drive(double outputMagnitude, double curve) {
		m_robotDrive.drive(outputMagnitude, curve);
	}

	/**
	 * @param leftStick
	 * @param rightStick
	 * @see edu.wpi.first.wpilibj.RobotDrive#tankDrive(edu.wpi.first.wpilibj.GenericHID,
	 *      edu.wpi.first.wpilibj.GenericHID)
	 */
	public void tankDrive(GenericHID leftStick, GenericHID rightStick) {
		m_robotDrive.tankDrive(leftStick, rightStick);
	}

	/**
	 * @param leftStick
	 * @param rightStick
	 * @param squaredInputs
	 * @see edu.wpi.first.wpilibj.RobotDrive#tankDrive(edu.wpi.first.wpilibj.GenericHID,
	 *      edu.wpi.first.wpilibj.GenericHID, boolean)
	 */
	public void tankDrive(GenericHID leftStick, GenericHID rightStick, boolean squaredInputs) {
		m_robotDrive.tankDrive(leftStick, rightStick, squaredInputs);
	}

	/**
	 * @param leftStick
	 * @param leftAxis
	 * @param rightStick
	 * @param rightAxis
	 * @see edu.wpi.first.wpilibj.RobotDrive#tankDrive(edu.wpi.first.wpilibj.GenericHID,
	 *      int, edu.wpi.first.wpilibj.GenericHID, int)
	 */
	public void tankDrive(GenericHID leftStick, int leftAxis, GenericHID rightStick, int rightAxis) {
		m_robotDrive.tankDrive(leftStick, leftAxis, rightStick, rightAxis);
	}

	/**
	 * @param leftStick
	 * @param leftAxis
	 * @param rightStick
	 * @param rightAxis
	 * @param squaredInputs
	 * @see edu.wpi.first.wpilibj.RobotDrive#tankDrive(edu.wpi.first.wpilibj.GenericHID,
	 *      int, edu.wpi.first.wpilibj.GenericHID, int, boolean)
	 */
	public void tankDrive(GenericHID leftStick, int leftAxis, GenericHID rightStick, int rightAxis,
			boolean squaredInputs) {
		m_robotDrive.tankDrive(leftStick, leftAxis, rightStick, rightAxis, squaredInputs);
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
	 * @param stick
	 * @param squaredInputs
	 * @see edu.wpi.first.wpilibj.RobotDrive#arcadeDrive(edu.wpi.first.wpilibj.GenericHID,
	 *      boolean)
	 */
	public void arcadeDrive(GenericHID stick, boolean squaredInputs) {
		m_robotDrive.arcadeDrive(stick, squaredInputs);
	}

	/**
	 * @param stick
	 * @see edu.wpi.first.wpilibj.RobotDrive#arcadeDrive(edu.wpi.first.wpilibj.GenericHID)
	 */
	public void arcadeDrive(GenericHID stick) {
		m_robotDrive.arcadeDrive(stick);
	}

	/**
	 * @param moveStick
	 * @param moveAxis
	 * @param rotateStick
	 * @param rotateAxis
	 * @param squaredInputs
	 * @see edu.wpi.first.wpilibj.RobotDrive#arcadeDrive(edu.wpi.first.wpilibj.GenericHID,
	 *      int, edu.wpi.first.wpilibj.GenericHID, int, boolean)
	 */
	public void arcadeDrive(GenericHID moveStick, int moveAxis, GenericHID rotateStick, int rotateAxis,
			boolean squaredInputs) {
		m_robotDrive.arcadeDrive(moveStick, moveAxis, rotateStick, rotateAxis, squaredInputs);
	}

	/**
	 * @param moveStick
	 * @param moveAxis
	 * @param rotateStick
	 * @param rotateAxis
	 * @see edu.wpi.first.wpilibj.RobotDrive#arcadeDrive(edu.wpi.first.wpilibj.GenericHID,
	 *      int, edu.wpi.first.wpilibj.GenericHID, int)
	 */
	public void arcadeDrive(GenericHID moveStick, int moveAxis, GenericHID rotateStick, int rotateAxis) {
		m_robotDrive.arcadeDrive(moveStick, moveAxis, rotateStick, rotateAxis);
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
	 * @param x
	 * @param y
	 * @param rotation
	 * @param gyroAngle
	 * @see edu.wpi.first.wpilibj.RobotDrive#mecanumDrive_Cartesian(double,
	 *      double, double, double)
	 */
	public void mecanumDrive_Cartesian(double x, double y, double rotation, double gyroAngle) {
		m_robotDrive.mecanumDrive_Cartesian(x, y, rotation, gyroAngle);
	}

	/**
	 * @param magnitude
	 * @param direction
	 * @param rotation
	 * @see edu.wpi.first.wpilibj.RobotDrive#mecanumDrive_Polar(double, double,
	 *      double)
	 */
	public void mecanumDrive_Polar(double magnitude, double direction, double rotation) {
		m_robotDrive.mecanumDrive_Polar(magnitude, direction, rotation);
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
	public void setInvertedMotor(MotorType motor, boolean isInverted) {
		m_robotDrive.setInvertedMotor(motor, isInverted);
	}

	/**
	 * @param sensitivity
	 * @see edu.wpi.first.wpilibj.RobotDrive#setSensitivity(double)
	 */
	public void setSensitivity(double sensitivity) {
		m_robotDrive.setSensitivity(sensitivity);
	}

	/**
	 * @param maxOutput
	 * @see edu.wpi.first.wpilibj.RobotDrive#setMaxOutput(double)
	 */
	public void setMaxOutput(double maxOutput) {
		m_robotDrive.setMaxOutput(maxOutput);
	}

	/**
	 * 
	 * @see edu.wpi.first.wpilibj.RobotDrive#free()
	 */
	public void free() {
		m_robotDrive.free();
	}

	/**
	 * @param timeout
	 * @see edu.wpi.first.wpilibj.RobotDrive#setExpiration(double)
	 */
	public void setExpiration(double timeout) {
		m_robotDrive.setExpiration(timeout);
	}

	/**
	 * @return
	 * @see edu.wpi.first.wpilibj.RobotDrive#getExpiration()
	 */
	public double getExpiration() {
		return m_robotDrive.getExpiration();
	}

	/**
	 * @return
	 * @see edu.wpi.first.wpilibj.RobotDrive#isSafetyEnabled()
	 */
	public boolean isSafetyEnabled() {
		return m_robotDrive.isSafetyEnabled();
	}

	/**
	 * @return
	 * @see edu.wpi.first.wpilibj.RobotDrive#getDescription()
	 */
	public String getDescription() {
		return m_robotDrive.getDescription();
	}

	/**
	 * @return
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return m_robotDrive.hashCode();
	}

	/**
	 * @return
	 * @see edu.wpi.first.wpilibj.RobotDrive#isAlive()
	 */
	public boolean isAlive() {
		return m_robotDrive.isAlive();
	}

	/**
	 * @param enabled
	 * @see edu.wpi.first.wpilibj.RobotDrive#setSafetyEnabled(boolean)
	 */
	public void setSafetyEnabled(boolean enabled) {
		m_robotDrive.setSafetyEnabled(enabled);
	}

	/**
	 * 
	 * @see edu.wpi.first.wpilibj.RobotDrive#stopMotor()
	 */
	public void stopMotor() {
		m_robotDrive.stopMotor();
	}

	/**
	 * @return
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return m_robotDrive.toString();
	}

}
