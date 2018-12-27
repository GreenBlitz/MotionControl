package org.greenblitz.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.followers.EncoderFollower;
import jaci.pathfinder.modifiers.TankModifier;
import org.greenblitz.motion.Localizer;
import org.greenblitz.motion.pathfinder.PathFollower;
import org.greenblitz.robot.subsystems.Chassis;

import java.util.Timer;

public class Robot extends IterativeRobot {


    @Override
    public void robotInit() {
        Chassis.init();

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
    }

    @Override
    public void autonomousPeriodic() {
        //  System.out.println(Localizer.getInstance().getLocation());
        Scheduler.getInstance().run();
    }

    @Override
    public void teleopInit() {
        Scheduler.getInstance().removeAll();
        Chassis.getInstance().resetSensors();
    }

    @Override
    public void teleopPeriodic() {
        Scheduler.getInstance().run();
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