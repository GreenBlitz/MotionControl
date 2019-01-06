package org.greenblitz.example.robot.commands;

import org.greenblitz.example.robot.subsystems.Chassis;
import org.greenblitz.motion.pathfinder.PathFollower;

public class PathFollowerCommand extends PeriodicCommand {

    private PathFollower m_controller;

    public PathFollowerCommand(PathFollower controller, long period) {
        super(period);
        requires(Chassis.getInstance());
        this.m_controller = controller;
    }

    public PathFollowerCommand(PathFollower controller) {
        this(controller, 20);
    }

    @Override
    protected void periodic() {
        m_controller.update(
                Chassis.getInstance().getLeftTicks(),
                Chassis.getInstance().getRightTicks());
    }

    @Override
    protected boolean isFinished() {
        return m_controller.isFinished();
    }

    @Override
    protected void end() {
        super.end();
        System.out.println(Chassis.getInstance().getLocation());
    }
}
