package org.greenblitz.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.greenblitz.debug.RemoteCSVTarget;
import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Position;
import org.greenblitz.robot.commands.APPCTestingCommand;
import org.greenblitz.robot.subsystems.Chassis;
import org.greenblitz.robot.subsystems.Shifter;


import java.util.Timer;

public class Robot extends TimedRobot {

    private AnalogInput colorSensor;
    private Relay LEDs;

    @Override
    public void robotInit() {
        Chassis.init();
        Chassis.getInstance().setCoast();
        OI.init();
      //  colorSensor = new AnalogInput(0);
        LEDs = new Relay(0, Relay.Direction.kForward);
        LEDs.setSafetyEnabled(false);
        OI.getInstance().getVisionTable().getEntry("LEDs").setBoolean(true);
    }

    @Override
    public void robotPeriodic() {
        updateSubsystems();
//        LEDs.set(OI.getInstance().getVisionTable().getEntry("LEDs").getValue().getBoolean() ?
//                 Relay.Value.kOn : Relay.Value.kOff);
        LEDs.set(Relay.Value.kOn);
    }


    @Override
    public void teleopInit() {
        RemoteCSVTarget.initTarget("location", "x", "y");
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
        Shifter.getInstance().update();
    }

    public static void main(String[] args) {
        RobotBase.startRobot(Robot::new);
    }
}