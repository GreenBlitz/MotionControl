package org.greenblitz.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.AnalogOutput;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.greenblitz.debug.RemoteCSVTarget;
import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Position;
import org.greenblitz.robot.commands.APPCTestingCommand;
import org.greenblitz.robot.subsystems.Chassis;


import java.util.Timer;

public class Robot extends TimedRobot {

    private AnalogInput colorSensor;

    @Override
    public void robotInit() {
        Chassis.init();
        Chassis.getInstance().setCoast();
        OI.init();
      //  colorSensor = new AnalogInput(0);
    }

    @Override
    public void robotPeriodic() {
        updateSubsystems();
    }


    @Override
    public void teleopInit() {
        RemoteCSVTarget.initTarget("location", "x", "y");
        Scheduler.getInstance().removeAll();
        prevTime = System.currentTimeMillis();
        Chassis.getInstance().setLocation(new Position(3.073, 0, 0));
    }

    long prevTime;

    @Override
    public void teleopPeriodic() {
        Scheduler.getInstance().run();
        SmartDashboard.putNumber("left ticks", Chassis.getInstance().getLeftTicks());
        SmartDashboard.putNumber("right ticks", Chassis.getInstance().getRightTicks());
        SmartDashboard.putNumber("left distance", Chassis.getInstance().getLeftDistance());
        SmartDashboard.putNumber("right distance", Chassis.getInstance().getRightDistance());
       // SmartDashboard.putNumber("Color Sensor", colorSensor.getValue());
    }

    Timer t = new Timer();
    @Override
    public void autonomousInit() {
        Scheduler.getInstance().removeAll();
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