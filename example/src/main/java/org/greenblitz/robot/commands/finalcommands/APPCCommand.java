package org.greenblitz.robot.commands.finalcommands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.greenblitz.debug.RemoteCSVTarget;
import org.greenblitz.motion.app.AbstractPositionPursuitController;
import org.greenblitz.motion.app.AdaptivePurePursuitController;
import org.greenblitz.motion.base.Position;
import org.greenblitz.motion.pathing.Path;
import org.greenblitz.robot.RobotStats;
import org.greenblitz.robot.subsystems.Chassis;

public class APPCCommand extends Command {

    private Chassis m_chassis;
    private Path m_path;
    private AdaptivePurePursuitController m_controller;

    public APPCCommand(Path<Position> path, double lookAhead,
                       double tolerance, boolean isBackwards,
                       double minSpeed, double maxSpeedDist, double maxSpeed) {
        requires(Chassis.getInstance());
        m_controller = new AdaptivePurePursuitController(path, lookAhead, RobotStats.Ragnarok.WHEELBASE, tolerance, isBackwards,
                minSpeed, maxSpeedDist, maxSpeed);
        m_path = m_controller.getPath();
        m_chassis = Chassis.getInstance();
    }

    @Override
    protected void initialize() {
        m_chassis.setCoast();
    }

    @Override
    protected void execute() {
        double[] moveValues = m_controller.iteration(m_chassis.getLocation());
        m_chassis.tankDrive(moveValues[0], moveValues[1]);
    }

    @Override
    protected void end(){
        SmartDashboard.putNumber("motion final x", m_chassis.getLocation().getX());
        SmartDashboard.putNumber("motion final y", m_chassis.getLocation().getY());
        System.out.println("Finished motion");
    }

    @Override
    protected boolean isFinished() {
        return m_controller.isFinished(m_chassis.getLocation());
    }


}
