package org.greenblitz.robot;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import org.greenblitz.motion.base.Position;
import org.greenblitz.robot.commands.APPCTestingCommand;
import org.greenblitz.robot.subsystems.ElevatorPrototype;

import java.util.Timer;

public class Robot extends TimedRobot {

    @Override
    public void robotInit() {
        ElevatorPrototype.init();
        OI.init();
    }

    @Override
    public void robotPeriodic() {
        updateSubsystems();
    }


    Timer t = new Timer();

    @Override
    public void teleopInit() {
        Scheduler.getInstance().removeAll();
        ElevatorPrototype.getInstance().resetEncoder();
        prevTime = System.currentTimeMillis();
    }

    long prevTime;

    @Override
    public void teleopPeriodic() {
        Scheduler.getInstance().run();

    }

    @Override
    public void autonomousInit() {
        Scheduler.getInstance().removeAll();
        Scheduler.getInstance().add(new APPCTestingCommand(0.5, RobotStats.Picasso.Chassis.HORIZONTAL_DISTANCE,
                new Position(0, 0, 0),
                new Position(0, 1, Math.PI/2),
                new Position(1, 1, 0),
                new Position(1, 2, 0)
        ));
    }

    @Override
    public void autonomousPeriodic(){
        Scheduler.getInstance().run();
    }

    @Override
    public void disabledInit(){
        Scheduler.getInstance().removeAll();
    }

    @Override
    public void disabledPeriodic() {
        updateSubsystems();
    }

    public void updateSubsystems() {
        ElevatorPrototype.getInstance().update();
    }

    public static void main(String[] args) {
        RobotBase.startRobot(Robot::new);
    }
}