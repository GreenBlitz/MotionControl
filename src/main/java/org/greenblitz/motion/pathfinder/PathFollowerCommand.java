package org.greenblitz.motion.pathfinder;

import edu.wpi.first.wpilibj.command.Command;

public class PathFollowerCommand extends Command {

    private PathFollower m_controller;

    public PathFollowerCommand(PathFollower controller) {
        this.m_controller = controller;
    }

    @Override
    protected void initialize() {
        System.out.println("Started path follower");
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
