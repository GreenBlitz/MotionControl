package org.greenblitz.robot.commands.vision;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.greenblitz.robot.OI;
import org.greenblitz.robot.subsystems.Chassis;

public class DriveToVisionTarget extends Command implements PIDSource, PIDOutput {

    private static final double kP = 1.1, kI = 0, kD = 0, turnkP = 0.035;
    private PIDController m_controller;
    private static final long TIME_ON_TARGET = 100;
    private long m_onTarget = -1;
    private static final double MINIMUM_OUTPUT = 0.15;

    public DriveToVisionTarget() {
        requires(Chassis.getInstance());
        m_controller = new PIDController(kP, kI, kD, this, this);
    }

    @Override
    protected void initialize() {
        m_onTarget = -1;
        m_controller.setAbsoluteTolerance(0.1);
        m_controller.setSetpoint(0.75);
        m_controller.setOutputRange(-0.8, 0.8);
        m_controller.enable();
    }

    @Override
    protected void execute() {
        if (m_controller.onTarget())
            if (m_onTarget == -1)
                m_onTarget = System.currentTimeMillis();
            else
                m_onTarget = -1;
    }

    @Override
    protected boolean isFinished() {
        return m_controller.onTarget() && System.currentTimeMillis() - m_onTarget > TIME_ON_TARGET;
    }

    @Override
    protected void end() {
        m_controller.disable();
        Chassis.getInstance().stop();
    }

    @Override
    public void pidWrite(double output) {
        if (OI.getInstance().foundHatch()) {
            if (Math.abs(output) < MINIMUM_OUTPUT)
                output = Math.signum(output) * MINIMUM_OUTPUT;
            SmartDashboard.putNumber("PID Output", output);
            Chassis.getInstance().arcadeDrive(-output, turnkP * (OI.getInstance().getHatchAngle()));
        }
        else {
            Chassis.getInstance().setBrake();
            Chassis.getInstance().arcadeDrive(0, 0);
        }
    }

    @Override
    public void setPIDSourceType(PIDSourceType pidSource) {}

    @Override
    public PIDSourceType getPIDSourceType() {
        return PIDSourceType.kDisplacement;
    }

    @Override
    public double pidGet() {
        return OI.getInstance().getHatchDistance();
    }
}