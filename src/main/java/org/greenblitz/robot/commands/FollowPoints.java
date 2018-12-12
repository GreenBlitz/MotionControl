package org.greenblitz.robot.commands;

import org.greenblitz.robot.RobotStats;
import org.greenblitz.robot.subsystems.Chassis;

import edu.wpi.first.wpilibj.command.Command;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Trajectory.FitMethod;
import jaci.pathfinder.followers.EncoderFollower;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.modifiers.TankModifier;

/**
 *
 */
public class FollowPoints extends Command {
	
	protected Trajectory.Config config;
	protected Trajectory trajectory;
	protected TankModifier mod;
	protected Trajectory leftTraj;
	protected Trajectory rightTraj;
	
	protected EncoderFollower followerR;
	protected EncoderFollower followerL;

	/**
	 * 
	 * @param fit	CUBIC_SPLINE or somthing else
	 * @param samples number of new points in each segment
	 * @param dt time periond between segments/points
	 * @param waypoints
	 */
    public FollowPoints(FitMethod fit, int samples, double dt, Waypoint[] waypoints) {
        requires(Chassis.getInstance());
        this.config = new Trajectory.Config(fit, samples, dt,
                RobotStats.Picasso.Chassis.MAX_VELOCITY,
                RobotStats.Picasso.Chassis.MAX_ACCELERATION,
                RobotStats.Picasso.Chassis.MAX_JERK);
        this.trajectory = Pathfinder.generate(waypoints, this.config);
        this.mod = new TankModifier(this.trajectory);
        this.mod.modify(RobotStats.Picasso.Chassis.VERTICAL_DISTANCE);
        this.leftTraj  = mod.getLeftTrajectory();
        this.rightTraj = mod.getRightTrajectory();
        
        Chassis.getInstance().resetEncoders();

        this.followerL = new EncoderFollower(leftTraj);
        followerL.configureEncoder(0,
                (int)(RobotStats.Picasso.EncoderRadianScale.LEFT_POWER * 2 * Math.PI),
                RobotStats.Picasso.Chassis.WHEEL_RADIUS);

        followerL.configurePIDVA(1.0, 0.0, 0.0,
                1.0/ RobotStats.Picasso.Chassis.MAX_VELOCITY, 0.0);


        this.followerR = new EncoderFollower(rightTraj);
        followerR.configureEncoder(0,
                (int)(RobotStats.Picasso.EncoderRadianScale.RIGHT_POWER * 2 * Math.PI),
                RobotStats.Picasso.Chassis.WHEEL_RADIUS);

        followerR.configurePIDVA(1.0, 0.0, 0.0,
                1.0/ RobotStats.Picasso.Chassis.MAX_VELOCITY, 0.0);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Chassis.getInstance().tankDrive(
    			0.7*Math.min(followerL.calculate(Chassis.getInstance().getLeftTicks()), 1),
    			0.7*Math.min(followerR.calculate(-Chassis.getInstance().getRightTicks()), 1)
    			);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return this.followerL.isFinished() || this.followerR.isFinished();
    }

    // Called once after isFinished returns true
    protected void end() {
    	Chassis.getInstance().stop();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
        end();
    }
}
