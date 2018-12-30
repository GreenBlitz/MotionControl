package org.greenblitz.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.followers.EncoderFollower;
import jaci.pathfinder.modifiers.TankModifier;
import org.greenblitz.motion.Localizer;
import org.greenblitz.motion.RobotStats;
import org.greenblitz.motion.pathfinder.PathFollower;
import org.greenblitz.robot.subsystems.Chassis;

import java.util.Timer;

public class Robot extends IterativeRobot {

    protected Trajectory.Config config;
    protected Trajectory trajectory;
    protected TankModifier mod;
    protected Trajectory leftTraj;
    protected Trajectory rightTraj;

    protected EncoderFollower followerR;
    protected EncoderFollower followerL;

    private PathFollower follower;

    private Timer notifier = new Timer();

    /**
     * @param fit       CUBIC_SPLINE or somthing else
     * @param samples   number of new points in each segment
     * @param dt        time periond between segments/points
     * @param waypoints
     */
    public void initPF(Trajectory.FitMethod fit, int samples, double dt, Waypoint[] waypoints) {
        this.config = new Trajectory.Config(fit, samples, dt,
                RobotStats.Picasso.Chassis.MAX_VELOCITY / 2,
                RobotStats.Picasso.Chassis.MAX_ACCELERATION,
                RobotStats.Picasso.Chassis.MAX_JERK);

        this.trajectory = Pathfinder.generate(waypoints, this.config);
        this.mod = new TankModifier(this.trajectory);
        this.mod.modify(RobotStats.Picasso.Chassis.VERTICAL_DISTANCE);
        this.leftTraj = mod.getLeftTrajectory();
        this.rightTraj = mod.getRightTrajectory();

        Chassis.getInstance().resetEncoders();

        this.followerL = new EncoderFollower(leftTraj);
        followerL.configureEncoder(0,
                (int) (RobotStats.Picasso.EncoderRadianScale.LEFT_POWER * 2 * Math.PI),
                RobotStats.Picasso.Chassis.WHEEL_RADIUS * 2);

        followerL.configurePIDVA(1.0, 0.0, 0.0,
                1.0 / RobotStats.Picasso.Chassis.MAX_VELOCITY, 0.0);


        this.followerR = new EncoderFollower(rightTraj);
        followerR.configureEncoder(0,
                (int) (RobotStats.Picasso.EncoderRadianScale.RIGHT_POWER * 2 * Math.PI),
                RobotStats.Picasso.Chassis.WHEEL_RADIUS * 2);

        followerR.configurePIDVA(1.0, 0.0, 0.0,
                1.0 / RobotStats.Picasso.Chassis.MAX_VELOCITY, 0.0);

        PathFollower.EncoderConfig leftConfig = new PathFollower.EncoderConfig(  )

        follower = new PathFollower(Chassis.getInstance(), RobotStats.Picasso.Chassis.WHEEL_RADIUS * 2, 20, leftTraj, rightTraj, leftConfig, rightConfig);
    }

    @Override
    public void robotInit() {
        Chassis.init();
        initPF(Trajectory.FitMethod.HERMITE_CUBIC,
                Trajectory.Config.SAMPLES_HIGH,
                0.05,
                new Waypoint[]{
                        new Waypoint(0, 0, 0),
                        new Waypoint(-1, 2, -Math.PI/2)
                });
        Localizer.getInstance().configure(RobotStats.Picasso.Chassis.HORIZONTAL_DISTANCE, Chassis.getInstance().getLeftEncoder(), Chassis.getInstance().getRightEncoder());
        Localizer.startLocalizer();
    }

    private static String segToString(Trajectory.Segment seg) {
        return String.format("x: %f, y: %f, heading: %f, velocity: %f, acceleration: %f", seg.x, seg.y, seg.heading, seg.velocity, seg.acceleration);
    }

    private static String trajToString(Trajectory traj) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < traj.length(); i++) {
            sb.append(segToString(traj.get(i))).append('\n');
        }
        return sb.toString();
    }

    @Override
    public void robotPeriodic() {
        updateSubsystems();
    }

    @Override
    public void autonomousInit() {
        Chassis.getInstance().resetSensors();
        notifier.schedule(generateFollower(), 0, (long) (1000 * 0.05));
    }

    @Override
    public void autonomousPeriodic() {
        //  System.out.println(Localizer.getInstance().getLocation());
    }

    @Override
    public void teleopInit() {
        Scheduler.getInstance().removeAll();
        Chassis.getInstance().resetSensors();
    }

    double vel = 0;

    @Override
    public void teleopPeriodic() {
        Scheduler.getInstance().run();
        double currentVel = Chassis.getInstance().getSpeed();
        if (Math.abs(currentVel) > vel) {
            vel = currentVel;
            SmartDashboard.putNumber("Max velocity", vel);
        }
    }

    @Override
    public void disabledInit() {
        Chassis.getInstance().stop();
        Chassis.getInstance().resetEncoders();
        Localizer.getInstance().reset();
    }

    @Override
    public void disabledPeriodic() {
        updateSubsystems();
    }

    public void updateSubsystems() {
        Chassis.getInstance().update();
    }
}