package org.greenblitz.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.greenblitz.debug.RemoteCSVTarget;
import org.greenblitz.motion.Localizer;
import org.greenblitz.motion.app.AbstractPositionPursuitController;
import org.greenblitz.motion.base.Position;
import org.greenblitz.motion.pathing.Path;
import org.greenblitz.robot.subsystems.Chassis;

import java.util.Arrays;

public class APPCTestingCommand extends Command {

    private Chassis m_chassis;
    private Path m_path;
    private Position startLoc;
    private AbstractPositionPursuitController<Position> m_controller;

    public APPCTestingCommand(AbstractPositionPursuitController<Position> controller, Position startLoc) {
        requires(Chassis.getInstance());
        m_controller = controller;
        m_path = controller.getM_path();
        System.out.println(m_path);
        m_path.sendToCSV("the_path");
        m_chassis = Chassis.getInstance();
        this.startLoc = startLoc;
        RemoteCSVTarget.initTarget("m_path", "x", "y");
    }

    public APPCTestingCommand(AbstractPositionPursuitController<Position> controller){
        this(controller, Localizer.getInstance().getLocation());
    }

    @Override
    protected void initialize() {
        m_chassis.setCoast();
        Localizer.getInstance().reset(m_chassis.getLeftDistance(), m_chassis.getRightDistance(), startLoc);
        m_path.sendToCSV("m_path");
        super.initialize();
    }

    @Override
    protected void execute() {
        double[] moveValues = m_controller.iteration(m_chassis.getLocation());
        System.out.println("Move vals - " + Arrays.toString(moveValues));
        m_chassis.tankDrive(moveValues[0], moveValues[1]);
    }

    @Override
    protected void end(){
        SmartDashboard.putNumber("APPC final x", m_chassis.getLocation().getX());
        SmartDashboard.putNumber("APPC final y", m_chassis.getLocation().getY());
    }

    protected void savePath(String fileName){
        m_path.saveAsCSV(fileName);
    }

    @Override
    protected boolean isFinished() {
        return m_controller.isFinished(m_chassis.getLocation());
    }
}
