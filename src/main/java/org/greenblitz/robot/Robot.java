package org.greenblitz.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import jaci.pathfinder.Trajectory;
import org.greenblitz.motion.Localizer;

import org.greenblitz.robot.subsystems.Chassis;
import org.greenblitz.utils.Navx;

public class Robot extends IterativeRobot {


    @Override
    public void robotInit() {
        Chassis.init();

        Localizer.getInstance().configure(RobotStats.Picasso.Chassis.HORIZONTAL_DISTANCE, Chassis.getInstance().getLeftEncoder(), Chassis.getInstance().getRightEncoder(), null);//Navx.getInstance());
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
        Localizer.getInstance().reset();

    }

    @Override
    public void autonomousPeriodic() {
        Scheduler.getInstance().run();
    }

    @Override
    public void teleopInit() {
        Scheduler.getInstance().removeAll();
        Chassis.getInstance().resetSensors();
        Localizer.getInstance().reset();
    }

    @Override
    public void teleopPeriodic() {
        Scheduler.getInstance().run();
    }

    @Override
    public void disabledInit() {
        Chassis.getInstance().stop();
        Chassis.getInstance().forceEncodersReset();
        Chassis.getInstance().resetSensors();
    }

    @Override
    public void disabledPeriodic() {
        updateSubsystems();
    }

    public void updateSubsystems() {
        Chassis.getInstance().update();
    }
}