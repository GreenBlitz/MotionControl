package org.greenblitz.motion;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import org.greenblitz.motion.subsystems.Chassis;

import java.util.LinkedList;
import java.util.List;

public class Robot extends IterativeRobot {

    private static final Robot instance = new Robot();

    private List<Command> permanentCommands = new LinkedList<Command>();

    private Command m_autonomousCommand;
    private SendableChooser<Command> m_autonomousChooser;

    private boolean endgame = false;

    public static Robot getInstance() {
        return instance;
    }

    public void addPermanentCommand(Command command) {
        permanentCommands.add(command);
    }

    @Override
    public void robotInit() {
        Chassis.init();
    }

    @Override
    public void robotPeriodic() {
        updateSubsystems();
    }

    @Override
    public void autonomousInit() {
        endgame = false;
        Chassis.getInstance().resetSensors();
        /*Chassis.getInstance().resetLocalizer();
        Chassis.getInstance().enableLocalizer();*/
        Timer.delay(0.02);

        m_autonomousCommand = m_autonomousChooser.getSelected();
        Scheduler.getInstance().add(m_autonomousCommand);
    }

    @Override
    public void autonomousPeriodic() {
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
        Chassis.getInstance().resetGyro();
        Chassis.getInstance().resetEncoders();
    }

    @Override
    public void disabledPeriodic() {
        updateSubsystems();
    }

    public void updateSubsystems() {
        Chassis.getInstance().update();
    }

    public boolean isEndgame() {
        return endgame;
    }

    public void setEndgame(boolean isEndgame) {
        endgame = isEndgame;
    }
}