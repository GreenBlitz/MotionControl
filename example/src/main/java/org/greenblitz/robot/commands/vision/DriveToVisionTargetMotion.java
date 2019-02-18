package org.greenblitz.robot.commands.vision;

import edu.wpi.first.wpilibj.command.Command;
import org.greenblitz.motion.pid.MultivariablePIDController;
import org.greenblitz.motion.pid.PIDObject;
import org.greenblitz.robot.OI;
import org.greenblitz.robot.subsystems.Chassis;

public class DriveToVisionTargetMotion extends Command {

    private static final PIDObject LinearPID = new PIDObject(-0.9),
    AngularPID = new PIDObject(0.04);
    private MultivariablePIDController m_controller;
    private static final long TIME_ON_TARGET = 100;
    private long m_onTarget = -1;
    private static final double MINIMUM_OUTPUT = 0.15;

    public DriveToVisionTargetMotion() {
        requires(Chassis.getInstance());
        m_controller = new MultivariablePIDController(LinearPID, AngularPID);
    }

    @Override
    protected void initialize() {
        m_onTarget = -1;
        m_controller.init(
                new double[]{0.75, 0},
                new double[]{OI.getInstance().getHatchDistance(), OI.getInstance().getHatchAngle()}
        );
    }

    @Override
    protected void execute() {
        double[] outputs = m_controller.calculatePID(new double[]{0.75, 0},
                                  new double[]{OI.getInstance().getHatchDistance(), OI.getInstance().getHatchAngle()});
        Chassis.getInstance().arcadeDrive(outputs[0], outputs[1]);

        if (m_controller.isFinished(new double[]{0.75, 0},
                new double[]{OI.getInstance().getHatchDistance(), OI.getInstance().getHatchAngle()},
                new double[]{0.1, 5}
        ))
            if (m_onTarget == -1)
                m_onTarget = System.currentTimeMillis();
            else
                m_onTarget = -1;
    }

    @Override
    protected boolean isFinished() {
        return m_controller.isFinished(new double[]{0.75, 0},
                new double[]{OI.getInstance().getHatchDistance(), OI.getInstance().getHatchAngle()},
                new double[]{0.1, 3}
        ) && System.currentTimeMillis() - m_onTarget > TIME_ON_TARGET;
    }

    @Override
    protected void end() {
        Chassis.getInstance().stop();
        System.out.println("Finished Vision");
    }

}