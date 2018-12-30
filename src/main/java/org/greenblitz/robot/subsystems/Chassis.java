package org.greenblitz.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import org.greenblitz.motion.RobotStats;
import org.greenblitz.motion.base.IChassis;
import org.greenblitz.motion.base.IEncoder;
import org.greenblitz.motion.pathfinder.GenerateTrajectory;
import org.greenblitz.motion.pathfinder.PathFollower;
import org.greenblitz.motion.pathfinder.PathfinderException;
import org.greenblitz.robot.OI;
import org.greenblitz.robot.RobotMap;
import org.greenblitz.robot.RobotPath;
import org.greenblitz.robot.commands.ArcadeDriveByJoystick;
import org.greenblitz.utils.CANRobotDrive;
import org.greenblitz.utils.SmartEncoder;

public class Chassis extends Subsystem implements IChassis {

    private static Chassis instance;

    private static final double TICKS_PER_METER_LEFT = RobotStats.Picasso.EncoderMetreScale.LEFT_VELOCITY;
    private static final double TICKS_PER_METER_RIGHT = RobotStats.Picasso.EncoderMetreScale.RIGHT_VELOCITY;

    private PathFollower follower;


    private SmartEncoder m_leftEncoder, m_rightEncoder;

    public IEncoder getLeftEncoder() {
        return m_leftEncoder;
    }

    public IEncoder getRightEncoder() {
        return m_rightEncoder;
    }

    private CANRobotDrive m_robotDrive;

    public static Chassis getInstance() {
        return instance;
    }

    public static void init() {
        instance = new Chassis();
        try {
            SmartDashboard.putBoolean("Is using correct waypoints.", true);
            instance.follower = new PathFollower(GenerateTrajectory.unsafeGenerate(
                    new Waypoint[]{
                            new Waypoint(0, 0, 0),
                            new Waypoint(1, 1, Math.toRadians(45)),
                    }, Trajectory.FitMethod.HERMITE_QUINTIC,
                    10000,
                    0.05
            ),
                    instance,
                    RobotStats.Picasso.Chassis.WHEEL_RADIUS*2,
                    50,
                    new PathFollower.EncoderConfig(
                            (int)(RobotStats.Picasso.EncoderRadianScale.LEFT_VELOCITY*2*
                                    Math.PI),
                            1, 1.0/RobotStats.Picasso.Chassis.MAX_VELOCITY)
                    );
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private Chassis() {
        m_robotDrive = new CANRobotDrive(RobotMap.ChassisPort.FRONT_LEFT, RobotMap.ChassisPort.REAR_LEFT,
                RobotMap.ChassisPort.FRONT_RIGHT, RobotMap.ChassisPort.REAR_RIGHT);
        m_leftEncoder = new SmartEncoder(m_robotDrive.getTalon(CANRobotDrive.TalonID.REAR_LEFT), TICKS_PER_METER_LEFT);
        m_rightEncoder = new SmartEncoder(m_robotDrive.getTalon(CANRobotDrive.TalonID.REAR_RIGHT), TICKS_PER_METER_RIGHT);
        m_rightEncoder.invert();
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
        m_robotDrive.arcadeDrive(-moveValue, rotateValue);
    }

    public void tankDrive(double leftValue, double rightValue) {
        m_robotDrive.tankDrive(-leftValue, -rightValue);
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

    public PathFollower getPathFollowerController() {
        return follower;
    }
}