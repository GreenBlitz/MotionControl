package org.greenblitz.robot;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.followers.EncoderFollower;
import jaci.pathfinder.modifiers.TankModifier;
import org.greenblitz.motion.app.Localizer;
import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Position;
import org.greenblitz.motion.pathfinder.Converters;
import org.greenblitz.robot.commands.APPCTestingCommand;
import org.greenblitz.robot.subsystems.Chassis;

import java.util.Timer;
import java.util.TimerTask;

public class Robot extends TimedRobot {

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
    public void teleopInit() {
        Scheduler.getInstance().removeAll();
        prevTime = System.currentTimeMillis();
    }

    long prevTime;

    @Override
    public void teleopPeriodic() {
        Scheduler.getInstance().run();
        SmartDashboard.putNumber("left ticks", Chassis.getInstance().getLeftTicks());
        SmartDashboard.putNumber("right ticks", Chassis.getInstance().getRightTicks());
        SmartDashboard.putNumber("left distance", Chassis.getInstance().getLeftDistance());
        SmartDashboard.putNumber("right distance", Chassis.getInstance().getRightDistance());
    }

    Timer t = new Timer();
    @Override
    public void autonomousInit() {
        Scheduler.getInstance().removeAll();
        Point start = new Point(0,0);
        Point a = new Point(0,1);
        Point b = new Point(1,1);
        Point end = new Point(1,2);
        Scheduler.getInstance().add(new APPCTestingCommand(0.5, RobotStats.Ragnarok.WHEELBASE,
                new Position(Point.bezierSample(0, start, a, b, end)),
                new Position(Point.bezierSample(0.1, start, a, b, end)),
                new Position(Point.bezierSample(0.2, start, a, b, end)),
                new Position(Point.bezierSample(0.3, start, a, b, end)),
                new Position(Point.bezierSample(0.4, start, a, b, end)),
                new Position(Point.bezierSample(0.5, start, a, b, end)),
                new Position(Point.bezierSample(0.6, start, a, b, end)),
                new Position(Point.bezierSample(0.7, start, a, b, end)),
                new Position(Point.bezierSample(0.8, start, a, b, end)),
                new Position(Point.bezierSample(0.9, start, a, b, end)),
                new Position(Point.bezierSample(1, start, a, b, end))
        ));
    }

    @Override
    public void autonomousPeriodic(){
        Scheduler.getInstance().run();
    }

    @Override
    public void disabledInit(){
        Scheduler.getInstance().removeAll();
        System.out.println(Chassis.getInstance().getLocation());
        Chassis.getInstance().resetSensors();
    }

    @Override
    public void disabledPeriodic() {
        updateSubsystems();
    }

    public void updateSubsystems() {
        Chassis.getInstance().update();
    }

    public static void main(String[] args) {
        RobotBase.startRobot(Robot::new);
    }
}