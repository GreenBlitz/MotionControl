package org.greenblitz.robot.commands.vision;

import edu.wpi.first.wpilibj.command.Command;
import org.greenblitz.motion.pid.MultivariablePIDController;
import org.greenblitz.motion.pid.PIDObject;
import org.greenblitz.motion.tolerance.AbsoluteTolerance;
import org.greenblitz.motion.tolerance.ITolerance;
import org.greenblitz.robot.OI;
import org.greenblitz.robot.subsystems.Chassis;

public class DriveToVisionTargetMotion extends Command {

    private static final PIDObject LINEAR_PID = new PIDObject(-0.9), ANGULAR_PID = new PIDObject(0.04);
    private static final ITolerance LINEAR_TOLERANCE = new AbsoluteTolerance(0.1);
    private static final ITolerance ANGULAR_TOLERANCE = new AbsoluteTolerance(3);
    private MultivariablePIDController m_controller;
    private static final long TIME_ON_TARGET = 100;
    private long m_onTarget = -1;
    private static final double MINIMUM_OUTPUT = 0.15;

    public DriveToVisionTargetMotion() {
        requires(Chassis.getInstance());
        m_controller = new MultivariablePIDController(2);
        m_controller.setPIDObject(0, LINEAR_PID, LINEAR_TOLERANCE);
        m_controller.setPIDObject(1, ANGULAR_PID, ANGULAR_TOLERANCE);
    }

    @Override
    protected void initialize() {
        m_onTarget = -1;
        m_controller.setGoals(0.75, 0);
    }

    @Override
    protected void execute() {
        double[] outputs = m_controller.calculate(OI.getInstance().getHatchDistance(), OI.getInstance().getHatchAngle());
        Chassis.getInstance().arcadeDrive(outputs[0], outputs[1]);

        if (m_controller.isFinished())
            if (m_onTarget == -1)
                m_onTarget = System.currentTimeMillis();
            else
                m_onTarget = -1;
    }

    @Override
    protected boolean isFinished() {
        return m_controller.isFinished() && (System.currentTimeMillis() - m_onTarget > TIME_ON_TARGET);
    }

    @Override
    protected void end() {
        Chassis.getInstance().stop();
        System.out.println("Finished Vision");
    }

}