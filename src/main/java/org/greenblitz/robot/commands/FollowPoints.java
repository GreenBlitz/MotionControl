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
                RobotStats.Cerberous.Chassis.MAX_VELOCITY.value,
                RobotStats.Cerberous.Chassis.MAX_ACCELARATION.value,
                RobotStats.Cerberous.Chassis.MAX_JERK.value);
        this.trajectory = Pathfinder.generate(waypoints, this.config);
        this.mod = new TankModifier(this.trajectory);
        this.mod.modify(RobotStats.Cerberous.Chassis.VERTICAL_DISTANCE.value);
        this.leftTraj  = mod.getLeftTrajectory();
        this.rightTraj = mod.getRightTrajectory();
        
        Chassis.getInstance().resetEncoders();

        this.followerL = new EncoderFollower(leftTraj);
        followerL.configureEncoder(0,
                (int)(RobotStats.Cerberous.EncoderRadianScale.LEFT_POWER.value * 2 * Math.PI),
                RobotStats.Cerberous.Chassis.WHEEL_RADIUS.value);

        followerL.configurePIDVA(1.0, 0.0, 0.0,
                1.0/RobotStats.Cerberous.Chassis.MAX_VELOCITY.value, 0.0);


        this.followerR = new EncoderFollower(rightTraj);
        followerR.configureEncoder(0,
                (int)(RobotStats.Cerberous.EncoderRadianScale.RIGHT_POWER.value * 2 * Math.PI),
                RobotStats.Cerberous.Chassis.WHEEL_RADIUS.value);

        followerR.configurePIDVA(1.0, 0.0, 0.0,
                1.0/RobotStats.Cerberous.Chassis.MAX_VELOCITY.value, 0.0);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Chassis.getInstance().tankDrive(
    			0.5*followerL.calculate(Chassis.getInstance().getLeftTicks()),
    			0.5*followerR.calculate(-Chassis.getInstance().getRightTicks())
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
