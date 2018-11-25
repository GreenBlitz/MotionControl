package org.greenblitz.robot.subsystems;

import com.ctre.phoenix.ErrorCode;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.greenblitz.robot.CANRobotDrive;
import org.greenblitz.robot.OI;
import org.greenblitz.robot.RobotMap;
import org.greenblitz.robot.commands.ArcadeDriveByJoystick;
import org.greenblitz.utils.SmartEncoder;

public class Chassis extends Subsystem {

    private static Chassis instance;

    private static final double TICKS_PER_METER = 2150;

    private SmartEncoder m_leftEncoder, m_rightEncoder;

    public SmartEncoder getLeftEncoder() {
        return m_leftEncoder;
    }

    public SmartEncoder getRightEncoder() {
        return m_rightEncoder;
    }

    private CANRobotDrive m_robotDrive;

    public static Chassis getInstance() {
        return instance;
    }

    public static void init() {
        instance = new Chassis();
    }

    private Chassis() {
        m_robotDrive = new CANRobotDrive(RobotMap.CHASSIS_FRONT_LEFT_MOTOR_PORT, RobotMap.CHASSIS_REAR_LEFT_MOTOR_PORT,
                RobotMap.CHASSIS_FRONT_RIGHT_MOTOR_PORT, RobotMap.CHASSIS_REAR_RIGHT_MOTOR_PORT);
        m_leftEncoder = new SmartEncoder(m_robotDrive.getTalon(CANRobotDrive.TalonID.REAR_LEFT), TICKS_PER_METER);
        m_rightEncoder = new SmartEncoder(m_robotDrive.getTalon(CANRobotDrive.TalonID.REAR_RIGHT), TICKS_PER_METER);
        m_leftEncoder.reset();
        m_rightEncoder.reset();
    }


    public void initDefaultCommand() {
        setDefaultCommand(new ArcadeDriveByJoystick(OI.getInstance().getMainJS()));
    }


    public void update() {
        SmartDashboard.putString("Chassis current command", getCurrentCommandName());
        SmartDashboard.putNumber("Chassis Distance", getDistance());
        SmartDashboard.putNumber("Chassis left ticks", getLeftTicks());
        SmartDashboard.putNumber("Chassis rightticks", getRightTicks());
    }

    public void arcadeDrive(double moveValue, double rotateValue) {
        m_robotDrive.arcadeDrive(moveValue, rotateValue);
    }

    public void tankDrive(double leftValue, double rightValue) {
        m_robotDrive.tankDrive(leftValue, rightValue);
    }

    public void stop() {
        tankDrive(0, 0);
    }

    public double getDistance() {
        return -m_leftEncoder.getDistance() / 2 + m_rightEncoder.getDistance() / 2;
    }

    public double getSpeed() {
        return (-m_leftEncoder.getSpeed() + m_rightEncoder.getSpeed()) / 2;
    }

    public double getLeftDistance() {
        return -m_leftEncoder.getDistance();
    }

    public double getRightDistance() {
        return m_rightEncoder.getDistance();
    }

    public int getLeftTicks() {
        return -m_leftEncoder.getTicks();
    }

    public int getRightTicks() {
        return m_rightEncoder.getTicks();
    }

    public double getLeftSpeed() {
        return -m_leftEncoder.getSpeed();
    }

    public double getRightSpeed() {
        return m_rightEncoder.getSpeed();
    }

    public void resetSensors() {
        resetEncoders();
    }

    public ErrorCode resetLeftEncoder() {
        return m_leftEncoder.reset();
    }

    public ErrorCode resetRightEncoder() {
        return m_rightEncoder.reset();
    }

    public void resetEncoders() {
        resetLeftEncoder();
        resetRightEncoder();
    }
}