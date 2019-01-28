package org.greenblitz.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.greenblitz.debug.RemoteCSVTarget;
import org.greenblitz.motion.app.AbstractPositionPursuitController;
import org.greenblitz.motion.pathing.Path;
import org.greenblitz.motion.base.Point;
import org.greenblitz.robot.subsystems.Chassis;

public class APPCTestingCommand extends Command {

    private Chassis m_chassis;
    private Path m_path;
    private AbstractPositionPursuitController m_controller;

    public APPCTestingCommand(AbstractPositionPursuitController controller) {
        m_controller = controller;
        m_path = controller.getM_path();
        m_chassis = Chassis.getInstance();
        RemoteCSVTarget.initTarget("m_path", "x", "y");
    }

    @Override
    protected void initialize() {
        m_chassis.setBrake();
        m_path.sendToCSV("m_path");
        super.initialize();
    }

    @Override
    protected void execute() {
        double[] moveValues = m_controller.iteration(m_chassis.getLocation());
        m_chassis.tankDrive(moveValues[0], moveValues[1]);
    }

    @Override
    protected void end(){
        SmartDashboard.putNumber("APPC final x", m_chassis.getLocation().getX());
        SmartDashboard.putNumber("APPC final y", m_chassis.getLocation().getY());
        m_chassis.setCoast();
    }

    protected void savePath(String fileName){
        m_path.saveAsCSV(fileName);
    }

    @Override
    protected boolean isFinished() {
        return m_controller.isFinished(m_chassis.getLocation()) &&
                Point.isFuzzyEqual(Chassis.getInstance().getSpeed(), 0, 0.5);
    }
}
