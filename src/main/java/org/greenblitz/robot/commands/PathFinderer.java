package org.greenblitz.robot.commands;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.followers.EncoderFollower;
import jaci.pathfinder.modifiers.TankModifier;
import org.greenblitz.robot.RobotStats;
import org.greenblitz.robot.subsystems.Chassis;

import java.util.Timer;
import java.util.TimerTask;

public class PathFinderer extends TimerTask {

    private PathFinderer instance = null;

    private PathFinderer getInstance() {
        if (instance == null) instance = new PathFinderer();
        return instance;
    }

    protected Trajectory.Config config;
    protected Trajectory trajectory;
    protected TankModifier mod;
    protected Trajectory leftTraj;
    protected Trajectory rightTraj;

    protected EncoderFollower followerR;
    protected EncoderFollower followerL;

    Timer t;

    /**
     * @param fit       CUBIC_SPLINE or somthing else
     * @param samples   number of new points in each segment
     * @param dt        time periond between segments/points
     * @param waypoints
     * @param limiter   a constant to multimply the max vel, max acc... by.
     */
    public void config(Trajectory.FitMethod fit, int samples, double dt, Waypoint[] waypoints, double limiter) {
        this.config = new Trajectory.Config(fit, samples, dt,
                RobotStats.Picasso.Chassis.MAX_VELOCITY * limiter,
                RobotStats.Picasso.Chassis.MAX_ACCELERATION * limiter,
                RobotStats.Picasso.Chassis.MAX_JERK * limiter);
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


    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return this.followerL.isFinished() && this.followerR.isFinished();
    }

    @Override
    public void run() {
        Chassis.getInstance().tankDrive(
                followerL.calculate(Chassis.getInstance().getLeftTicks()),
                followerR.calculate(Chassis.getInstance().getRightTicks()));
        System.out.printf("%d, %d\n", Chassis.getInstance().getLeftTicks(), Chassis.getInstance().getRightTicks());
        if(isFinished()){
            t.cancel();
            t.purge();
        }
    }

    public void startPathFinderer(Trajectory.FitMethod fit, int samples, double dt, Waypoint[] waypoints, double limiter) {
        config(fit, samples, dt, waypoints, limiter);
        t = new Timer();
        t.schedule(getInstance(), 0, (long) (dt * 1000));
    }

}
