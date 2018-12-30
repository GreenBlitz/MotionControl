package org.greenblitz.motion.pathfinder;

import edu.wpi.first.wpilibj.command.Command;

public class PathFollowerCommand extends Command {

    private PathFollower m_controller;

    public PathFollowerCommand(PathFollower controller) {
        this.m_controller = controller;
    }

    @Override
    protected void initialize() {
        m_controller.start();
    }

    @Override
    protected void execute() {
        if (isFinished())
            end(); // If we don't do this, stop won't be called when using from OI (i.e whenPressed, whileHeld, etc.)
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
