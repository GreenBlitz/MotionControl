package org.greenblitz.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import jaci.pathfinder.Trajectory;
import org.greenblitz.motion.Localizer;
import org.greenblitz.motion.RobotStats;
import org.greenblitz.motion.base.IChassis;
import org.greenblitz.motion.base.IEncoder;
import org.greenblitz.motion.pathfinder.PathFollower;
import org.greenblitz.robot.OI;
import org.greenblitz.robot.RobotMap;
import org.greenblitz.robot.RobotPath;
import org.greenblitz.robot.commands.ArcadeDriveByJoystick;
import org.greenblitz.utils.CANRobotDrive;
import org.greenblitz.utils.SmartEncoder;

public class Chassis extends Subsystem implements IChassis {

    private static final double POWER_LIMIT = 0.7;

    private static Chassis instance;

    private static final double TICKS_PER_METER_LEFT = RobotStats.Picasso.EncoderMetreScale.LEFT_POWER;
    private static final double TICKS_PER_METER_RIGHT = RobotStats.Picasso.EncoderMetreScale.RIGHT_POWER;

    private PathFollower follower;

    private SmartEncoder m_leftEncoder, m_rightEncoder;

    @Override
    public IEncoder getLeftEncoder() {
        return m_leftEncoder;
    }

    @Override
    public IEncoder getRightEncoder() {
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
        m_robotDrive = new CANRobotDrive(RobotMap.ChassisPort.FRONT_LEFT, RobotMap.ChassisPort.REAR_LEFT,
                RobotMap.ChassisPort.FRONT_RIGHT, RobotMap.ChassisPort.REAR_RIGHT);
        m_leftEncoder = new SmartEncoder(m_robotDrive.getTalon(CANRobotDrive.TalonID.REAR_LEFT), TICKS_PER_METER_LEFT);
        m_rightEncoder = new SmartEncoder(m_robotDrive.getTalon(CANRobotDrive.TalonID.REAR_RIGHT), TICKS_PER_METER_RIGHT);
        m_rightEncoder.invert();
        m_leftEncoder.reset();
        m_rightEncoder.reset();

        initMotion(RobotPath.getTestTrajectory());
    }

    private void initMotion(Trajectory[] trajectories) {
        PathFollower.EncoderConfig m_leftConfig = new PathFollower.EncoderConfig((int) (m_leftEncoder.getTicksPerMeter() * RobotStats.Picasso.Chassis.WHEEL_CIRCUMFERENCE), 1.0, 1 / RobotStats.Picasso.Chassis.MAX_VELOCITY);
        PathFollower.EncoderConfig m_rightConfig = new PathFollower.EncoderConfig((int) (m_rightEncoder.getTicksPerMeter() * RobotStats.Picasso.Chassis.WHEEL_CIRCUMFERENCE), 1.0, 1 / RobotStats.Picasso.Chassis.MAX_VELOCITY);
        follower = new PathFollower(this, RobotStats.Picasso.Chassis.WHEEL_DIAMETER, 20, trajectories[0], trajectories[1], m_leftConfig, m_rightConfig);
    }

    public void initDefaultCommand() {
        setDefaultCommand(new ArcadeDriveByJoystick(OI.getInstance().getMainJS()));
    }


    public void update() {
        SmartDashboard.putString("Chassis current command", getCurrentCommandName());
        SmartDashboard.putNumber("Chassis Distance", getDistance());
        SmartDashboard.putNumber("Chassis left ticks", getLeftTicks());
        SmartDashboard.putNumber("Chassis right ticks", getRightTicks());
    }

    public void arcadeDrive(double moveValue, double rotateValue) {
        if (Math.abs(moveValue) > POWER_LIMIT)
            moveValue = Math.signum(moveValue) * POWER_LIMIT;
        //if (Math.abs(rotateValue) > POWER_LIMIT)
        //    rotateValue = Math.signum(rotateValue) * POWER_LIMIT;
        m_robotDrive.arcadeDrive(-moveValue, rotateValue);
    }

    public void tankDrive(double leftValue, double rightValue) {
        m_robotDrive.tankDrive(-leftValue, -rightValue);
    }

    public void stop() {
        tankDrive(0, 0);
    }

    public double getDistance() {
        return m_leftEncoder.getDistance() / 2 + m_rightEncoder.getDistance() / 2;
    }

    public double getSpeed() {
        return (m_leftEncoder.getSpeed() + m_rightEncoder.getSpeed()) / 2;
    }

    public double getLeftDistance() {
        return m_leftEncoder.getDistance();
    }

    public double getRightDistance() {
        return m_rightEncoder.getDistance();
    }

    public int getLeftTicks() {
        return m_leftEncoder.getTicks();
    }

    public int getRightTicks() {
        return m_rightEncoder.getTicks();
    }

    public double getLeftSpeed() {
        return m_leftEncoder.getSpeed();
    }

    public double getRightSpeed() {
        return m_rightEncoder.getSpeed();
    }

    public void resetSensors() {
        resetEncoders();
        Localizer.getInstance().reset();
    }

    public void resetLeftEncoder() {
        m_leftEncoder.reset();
    }

    public void resetRightEncoder() {
        m_rightEncoder.reset();
    }

    public void resetEncoders() {
        resetLeftEncoder();
        resetRightEncoder();
    }

    public void forceEncodersReset() {
        do {
            resetLeftEncoder();
            resetRightEncoder();
        } while(m_leftEncoder.getTicks() != 0 || m_rightEncoder.getTicks() != 0);
    }

    public PathFollower getPathFollowerController() {
        return follower;
    }
}