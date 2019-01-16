package org.greenblitz.example.robot;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.greenblitz.example.robot.subsystems.ElevatorPrototype;
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
        SmartDashboard.putNumber("Ticks", ElevatorPrototype.getInstance().getDistance());
        if (ElevatorPrototype.getInstance().getSpeed() >
        SmartDashboard.getNumber("Vel", 0))
            SmartDashboard.putNumber("Vel", ElevatorPrototype.getInstance().getSpeed());
        double acc = ElevatorPrototype.getInstance().getSpeed()/
                ((System.currentTimeMillis() - prevTime)/1000.0);
        if (acc > SmartDashboard.getNumber("Acc", 0))
            SmartDashboard.putNumber("Acc", acc);
        prevTime = System.currentTimeMillis();

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