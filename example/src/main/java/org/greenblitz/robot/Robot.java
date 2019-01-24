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
        /*Scheduler.getInstance().removeAll();
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
        ));*/
        long period = 20;
        double dt = period / 1000.0;

        var waypoints = new Waypoint[]{
                Converters.fromPosition(new Position(0, 0, 0)),
                Converters.fromPosition(new Position(1, 2, 0))};
        var trajConfig = new Trajectory.Config(
                Trajectory.FitMethod.HERMITE_CUBIC,
                Trajectory.Config.SAMPLES_HIGH,
                dt,
                2,
                30,
                1000);
        var traj = Pathfinder.generate(waypoints, trajConfig);
        var mod = new TankModifier(traj);
        mod.modify(RobotStats.Ragnarok.WHEELBASE);
        var left = mod.getLeftTrajectory();
        var right = mod.getRightTrajectory();

        EncoderFollower leftFollower = new EncoderFollower(left);
        leftFollower.configureEncoder(
                0,
                (int) (RobotStats.Ragnarok.EncoderTicksPerMeter.LEFT_POWER * RobotStats.Ragnarok.WHEEL_RADIUS * 2 * Math.PI),
                RobotStats.Ragnarok.WHEEL_RADIUS * 2
        );
        leftFollower.configurePIDVA(0, 0, 0, 1 / 2.0, 0);

        EncoderFollower rightFollower = new EncoderFollower(right);
        rightFollower.configureEncoder(
                0,
                (int) (RobotStats.Ragnarok.EncoderTicksPerMeter.RIGHT_POWER * RobotStats.Ragnarok.WHEEL_RADIUS * 2 * Math.PI),
                RobotStats.Ragnarok.WHEEL_RADIUS * 2
        );
        rightFollower.configurePIDVA(0, 0, 0, 1 / 2.0, 0);

        t.schedule(
                new TimerTask() {
                    boolean end = false;

                    @Override
                    public void run() {
                        if (!end) {
                            var left = leftFollower.calculate(Chassis.getInstance().getLeftTicks());
                            var right = rightFollower.calculate(Chassis.getInstance().getRightTicks());
                            SmartDashboard.putNumber("left raw", left);
                            SmartDashboard.putNumber("right raw", right);
                            Chassis.getInstance().tankDrive(left, right);
                            if (!leftFollower.isFinished()) {
                                SmartDashboard.putNumber(
                                        "left vel diff",
                                        Chassis.getInstance().getLeftSpeed() - leftFollower.getSegment().velocity);
                            }
                            if (!rightFollower.isFinished()) {
                                SmartDashboard.putNumber(
                                        "right vel diff",
                                        Chassis.getInstance().getRightSpeed() - rightFollower.getSegment().velocity);
                            }
                            if (leftFollower.isFinished() && rightFollower.isFinished()) {
                                end = true;
                                System.out.println(Chassis.getInstance().getLocation());
                            }
                        }
                    }
                },
                0,
                period);
    }

    @Override
    public void autonomousPeriodic(){
        Scheduler.getInstance().run();
    }

    @Override
    public void disabledInit(){
        Scheduler.getInstance().removeAll();
        System.out.println(Chassis.getInstance().getLocation());
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