package org.greenblitz.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.greenblitz.motion.pathfinder.PathFollower;
import org.greenblitz.robot.subsystems.Chassis;

public class PathFollowerCommand extends Command {

    private PathFollower m_controller;

    public PathFollowerCommand(PathFollower controller) {
        requires(Chassis.getInstance());
        this.m_controller = controller;
    }

    @Override
    protected void initialize() {
        m_controller.start();
    }

    @Override
    protected void end() {
        m_controller.stop();
    }

    @Override
    protected boolean isFinished() {
        return m_controller.isFinished();
    }
}
