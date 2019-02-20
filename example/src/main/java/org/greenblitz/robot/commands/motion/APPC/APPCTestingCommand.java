package org.greenblitz.robot.commands.motion.APPC;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.greenblitz.debug.RemoteCSVTarget;
import org.greenblitz.motion.app.AbstractPositionPursuitController;
import org.greenblitz.motion.base.Position;
import org.greenblitz.motion.pathing.Path;
import org.greenblitz.robot.subsystems.Chassis;

public class APPCTestingCommand extends Command {

    private Chassis m_chassis;
    private Path m_path;
    private AbstractPositionPursuitController<Position> m_controller;

    public APPCTestingCommand(AbstractPositionPursuitController<Position> controller) {
        requires(Chassis.getInstance());
        m_controller = controller;
        m_path = controller.getPath();
        System.out.println(m_path);
        //m_path.sendToCSV("the_path");
        m_chassis = Chassis.getInstance();
        RemoteCSVTarget.initTarget("m_path", "x", "y");
    }

    @Override
    protected void initialize() {
        m_chassis.setCoast();
        //m_path.sendToCSV("m_path");
    }

    @Override
    protected void execute() {
        double[] moveValues = m_controller.iteration(m_chassis.getLocation());
//        System.out.println("Move vals - " + Arrays.toString(moveValues));
        m_chassis.tankDrive(moveValues[0], moveValues[1]);
    }

    @Override
    protected void end(){
        SmartDashboard.putNumber("motion final x", m_chassis.getLocation().getX());
        SmartDashboard.putNumber("motion final y", m_chassis.getLocation().getY());
        System.out.println("Finished motion");
    }

    protected void savePath(String fileName){
        m_path.saveAsCSV(fileName);
    }

    @Override
    protected boolean isFinished() {
        return m_controller.isFinished(m_chassis.getLocation());
    }
}
