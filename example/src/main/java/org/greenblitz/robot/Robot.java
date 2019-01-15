package org.greenblitz.example.robot;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.followers.EncoderFollower;
import jaci.pathfinder.modifiers.TankModifier;
import org.greenblitz.example.robot.subsystems.Chassis;
import org.greenblitz.motion.app.AdaptivePurePursuitController;
import org.greenblitz.motion.app.Path;
import org.greenblitz.motion.base.Position;

import java.util.Timer;
import java.util.TimerTask;

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


    Timer t = new Timer();

    @Override
    public void autonomousInit() {
/*        Chassis.getInstance().resetSensors();
        final double S_R_T_I_I_I = 1/(1/(1/Math.sqrt(2)));
        Position[] list = new Position[]{
                new Position(0, 0),
                new Position(-1,2),
                new Position(-2, 4),
                new Position(0, 4),
                new Position(2, 4),
                //new Position(0, 8),
                /*new Position(2, 4/2),
                new Position(3, 6.828/2),
                new Position(4, 8/2)
                new Position(5, -6.282/2),
                new Position(6, 4/2),
                new Position(7, 1.172/2),
                new Position(2.828/2, -1.172/2),
                new Position(4/2, -4/2),
                new Position(2.828/2, -6.828/2),
                new Position(0, -8/2),
        };

        Path path = new Path(list);
        path.interpolate(10);
        System.out.println(path.getPath());
        APPC = new AdaptivePurePursuitController(path, 0.8, Chassis.getInstance().getWheelbaseWidth(), false);*/
        long period = 20;
        double dt = period / 1000.0;
        var waypoints = new Waypoint[]{new Waypoint(0, 0, 0), new Waypoint(2, 2, 0)};
        var trajConfig = new Trajectory.Config(
                Trajectory.FitMethod.HERMITE_CUBIC,
                Trajectory.Config.SAMPLES_HIGH,
                dt,
                RobotStats.Picasso.Chassis.MAX_VELOCITY / 2.2,
                RobotStats.Picasso.Chassis.MAX_ACCELERATION,
                RobotStats.Picasso.Chassis.MAX_JERK);
        var traj = Pathfinder.generate(waypoints, trajConfig);
        var mod = new TankModifier(traj);
        mod.modify(RobotStats.Picasso.Chassis.HORIZONTAL_DISTANCE);
        var left = mod.getLeftTrajectory();
        var right = mod.getRightTrajectory();

        EncoderFollower leftFollower = new EncoderFollower(left);
        leftFollower.configureEncoder(
                0,
                (int) (RobotStats.Picasso.EncoderMetreScale.LEFT_POWER * RobotStats.Picasso.Chassis.WHEEL_CIRCUMFERENCE),
                RobotStats.Picasso.Chassis.WHEEL_DIAMETER
        );
        leftFollower.configurePIDVA(0, 0, 0, 1 / RobotStats.Picasso.Chassis.MAX_VELOCITY, 0);

        EncoderFollower rightFollower = new EncoderFollower(right);
        rightFollower.configureEncoder(
                0,
                (int) (RobotStats.Picasso.EncoderMetreScale.RIGHT_POWER * RobotStats.Picasso.Chassis.WHEEL_CIRCUMFERENCE),
                RobotStats.Picasso.Chassis.WHEEL_DIAMETER
        );
        rightFollower.configurePIDVA(0, 0, 0, 1 / RobotStats.Picasso.Chassis.MAX_VELOCITY, 0);

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
    public void autonomousPeriodic() {
        /*Scheduler.getInstance().run();
        double speedLimit = 0.9;
        double[] veryFastDrive = APPC.iteration(Chassis.getInstance().getLocation());
        if (veryFastDrive != null)
            Chassis.getInstance().tankDrive(speedLimit * veryFastDrive[0], speedLimit * veryFastDrive[1]);
        else
            //Chassis.getInstance().setBrake();
        SmartDashboard.putNumber("Left intended power", veryFastDrive != null ? speedLimit * veryFastDrive[0] : 0);
        SmartDashboard.putNumber("Right intended power", veryFastDrive != null ? speedLimit * veryFastDrive[1] : 0);
        SmartDashboard.putBoolean("Is running?", veryFastDrive != null);*/
    }

    @Override
    public void testInit() {
        Chassis.getInstance().resetSensors();
        final double S_R_T_I_I_I = 1 / (1 / (1 / Math.sqrt(2)));
        Position[] list = new Position[]{
                new Position(0, 0),
                new Position(-(1 - S_R_T_I_I_I), S_R_T_I_I_I),
                new Position(-1, 1),
                new Position(-(1 + S_R_T_I_I_I), S_R_T_I_I_I),
                new Position(2, 0)
        };

        Path path = new Path(list);

        APPC = new AdaptivePurePursuitController(path, 0.5, Chassis.getInstance().getWheelbaseWidth(), false);
    }

    @Override
    public void testPeriodic() {
        Scheduler.getInstance().run();
        double speedLimit = 1;
        double[] veryFastDrive = APPC.iteration(Chassis.getInstance().getLocation());
        if (veryFastDrive != null)
            Chassis.getInstance().tankDrive(speedLimit * veryFastDrive[0], speedLimit * veryFastDrive[1]);
        else
            //Chassis.getInstance().setBrake();
            SmartDashboard.putNumber("Left intended power", veryFastDrive != null ? speedLimit * veryFastDrive[0] : 0);
        SmartDashboard.putNumber("Right intended power", veryFastDrive != null ? speedLimit * veryFastDrive[1] : 0);
        SmartDashboard.putBoolean("Is running?", veryFastDrive != null);
    }

    @Override
    public void teleopInit() {
        Scheduler.getInstance().removeAll();
        Chassis.getInstance().resetSensors();
        //Chassis.getInstance().setCoast();
    }

    @Override
    public void teleopPeriodic() {
        Scheduler.getInstance().run();
        Chassis.getInstance().update();
    }

    @Override
    public void disabledInit() {
        Chassis.getInstance().stop();
        t.purge();
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