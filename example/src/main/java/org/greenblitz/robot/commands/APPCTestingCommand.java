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
    private Position m_startLoc;
    private Double m_startX;
    private Double m_startY;
    private Double m_startA;
    private AbstractPositionPursuitController<Position> m_controller;

    public APPCTestingCommand(AbstractPositionPursuitController<Position> controller, Position startLoc) {
        requires(Chassis.getInstance());
        m_controller = controller;
        m_path = controller.getM_path();
        System.out.println(m_path);
        //m_path.sendToCSV("the_path");
        m_chassis = Chassis.getInstance();
        m_startLoc = startLoc;
        RemoteCSVTarget.initTarget("m_path", "x", "y");
        m_startX = null;
        m_startY = null;
        m_startA = null;

    }

    public APPCTestingCommand(AbstractPositionPursuitController<Position> controller){
        this(controller, null, null ,null);

    }

    public APPCTestingCommand(AbstractPositionPursuitController<Position> controller, Double startX, Double startY, Double startA){
        this(controller, null);
        m_startX = startX;
        m_startY = startY;
        m_startA = startA;
    }

    @Override
    protected void initialize() {
        if (m_startLoc == null) {
            if (m_startX == null) {
                m_startX = Localizer.getInstance().getLocation().getX();
                System.out.println("Null startX. set to: " + m_startX);
            }
            if (m_startY == null) {
                m_startY = Localizer.getInstance().getLocation().getY();
                System.out.println("Null startY. set to: " + m_startY);
            }
            if (m_startA == null) {
                m_startA = Localizer.getInstance().getLocation().getAngle();
                System.out.println("Null startA. set to: " + m_startA);
            }
            m_startLoc = new Position(m_startX, m_startY, m_startA);
        }
        System.out.println(m_startLoc);
        m_chassis.setCoast();
        Localizer.getInstance().reset(m_chassis.getLeftDistance(), m_chassis.getRightDistance(), m_startLoc);
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
        SmartDashboard.putNumber("APPC final x", m_chassis.getLocation().getX());
        SmartDashboard.putNumber("APPC final y", m_chassis.getLocation().getY());
        System.out.println("Finished APPC");
    }

    protected void savePath(String fileName){
        m_path.saveAsCSV(fileName);
    }

    @Override
    protected boolean isFinished() {
        return m_controller.isFinished(m_chassis.getLocation());
    }
}
