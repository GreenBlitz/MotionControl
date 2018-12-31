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
        System.out.println("Path follower command has started!");
        m_controller.start();
    }

    @Override
    protected void execute() {
    }

    @Override
    protected void end() {
        System.out.println("Path follower command has finished!");
        m_controller.stop();
    }

    @Override
    protected boolean isFinished() {
        return m_controller.isFinished();
    }
}
