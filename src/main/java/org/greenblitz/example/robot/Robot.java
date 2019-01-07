package org.greenblitz.example.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;

import org.greenblitz.example.robot.subsystems.Chassis;
import org.greenblitz.motion.app.AdaptivePurePursuitController;
import org.greenblitz.motion.app.Path;
import org.greenblitz.motion.base.Point;

import java.util.ArrayList;

public class Robot extends TimedRobot {

    AdaptivePurePursuitController APPC;

    @Override
    public void robotInit() {
        Chassis.init();
        OI.init();
    }

    @Override
    public void robotPeriodic() {
        updateSubsystems();
    }

    @Override
    public void autonomousInit() {
        Chassis.getInstance().resetSensors();
        ArrayList<Point> list = new ArrayList<Point>();
        list.add(new Point(0,0));
        list.add(new Point(-1.2,1.2));
        list.add(new Point(-1.2,3));
        Path path = new Path(list);

        APPC = new AdaptivePurePursuitController(path, 0.4, Chassis.getInstance().getWheelbaseWidth());
    }

    @Override
    public void autonomousPeriodic() {
        Scheduler.getInstance().run();
        double speedLimit = 0.7;
        double[] veryFastDrive = APPC.iteration(Chassis.getInstance().getLocation());
        Chassis.getInstance().tankDrive(speedLimit * veryFastDrive[0], speedLimit * veryFastDrive[1]);
    }

    @Override
    public void teleopInit() {
        Scheduler.getInstance().removeAll();
        Chassis.getInstance().resetSensors();
    }

    @Override
    public void teleopPeriodic() {
        Scheduler.getInstance().run();
        Chassis.getInstance().update();
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