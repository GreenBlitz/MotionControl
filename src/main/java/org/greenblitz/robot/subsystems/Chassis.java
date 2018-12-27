package org.greenblitz.robot.subsystems;

import com.ctre.phoenix.ErrorCode;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.followers.EncoderFollower;
import jaci.pathfinder.modifiers.TankModifier;
import org.greenblitz.motion.base.IChassis;
import org.greenblitz.motion.base.IEncoder;
import org.greenblitz.motion.pathfinder.PathFollower;
import org.greenblitz.robot.CANRobotDrive;
import org.greenblitz.robot.OI;
import org.greenblitz.robot.RobotMap;
import org.greenblitz.robot.RobotStats;
import org.greenblitz.robot.commands.ArcadeDriveByJoystick;
import org.greenblitz.utils.SmartEncoder;

public class Chassis extends Subsystem implements IChassis {

    private static Chassis instance;

    private static final double TICKS_PER_METER_LEFT = RobotStats.Picasso.EncoderMetreScale.LEFT_VELOCITY;
    private static final double TICKS_PER_METER_RIGHT = RobotStats.Picasso.EncoderMetreScale.RIGHT_VELOCITY;

    protected EncoderFollower followerR;

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

    /**
     * @param fit       CUBIC_SPLINE or something else
     * @param samples   number of new points in each segment
     * @param dt        time period between segments/points
     * @param waypoints path waypoints
     */
    public void initPF(Trajectory.FitMethod fit, int samples, double dt, Waypoint[] waypoints) {
        Trajectory.Config config = new Trajectory.Config(fit, samples, dt,
                RobotStats.Picasso.Chassis.MAX_VELOCITY / 2,
                RobotStats.Picasso.Chassis.MAX_ACCELERATION,
                RobotStats.Picasso.Chassis.MAX_JERK);

        Trajectory trajectory = Pathfinder.generate(waypoints, config);
        TankModifier mod = new TankModifier(trajectory);
        mod.modify(RobotStats.Picasso.Chassis.VERTICAL_DISTANCE);
        Trajectory leftTraj = mod.getLeftTrajectory();
        Trajectory rightTraj = mod.getRightTrajectory();

        Chassis.getInstance().resetEncoders();

        PathFollower.EncoderConfig leftConfig = new PathFollower.EncoderConfig((int) (RobotStats.Picasso.EncoderRadianScale.LEFT_POWER * 2 * Math.PI), 1.0, 1.0 / RobotStats.Picasso.Chassis.MAX_VELOCITY);
        PathFollower.EncoderConfig rightConfig = new PathFollower.EncoderConfig((int) (RobotStats.Picasso.EncoderRadianScale.RIGHT_POWER * 2 * Math.PI), 1.0, 1.0 / RobotStats.Picasso.Chassis.MAX_VELOCITY);

        follower = new PathFollower(Chassis.getInstance(), RobotStats.Picasso.Chassis.WHEEL_RADIUS * 2, 20, leftTraj, rightTraj, leftConfig, rightConfig);
    }

    public static void init() {
        instance = new Chassis();
        instance.initPF(Trajectory.FitMethod.HERMITE_CUBIC,
                Trajectory.Config.SAMPLES_HIGH,
                0.05,
                new Waypoint[]{
                        new Waypoint(0, 0, 0),
                        new Waypoint(-1, 2, -Math.PI / 2)
                });
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
        SmartDashboard.putNumber("Chassis right ticks", getRightTicks());
    }

    public void arcadeDrive(double moveValue, double rotateValue) {
        m_robotDrive.arcadeDrive(moveValue, rotateValue);
    }

    public void tankDrive(double leftValue, double rightValue) {
        SmartDashboard.putNumber("left raw value", leftValue);
        SmartDashboard.putNumber("right raw value", rightValue);
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