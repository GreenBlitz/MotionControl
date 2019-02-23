package org.greenblitz.robot.commands;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.greenblitz.robot.OI;
import org.greenblitz.robot.subsystems.Chassis;

/**
 * This command uses the P part of the PID control loop to turn an exact angle.
 * The command uses the Chassis subsystem.
 * For the PID controller, we use a kP value of 0.8, a setpoint of angle/90 drive through a parameter in the constructor, an output range of -0.8 to0.8 and a tolerance of 5 degrees per 90 degrees.
 * The PID controller uses the gyro angle as the input and the arcadeDrive() function as the output.
 * The command will stop as soon it is within range (+- 5 degrees of the setpoint) for 4 or more iterations.
 *
 * @see Command
 * @see PIDSource
 * @see PIDOutput
 * @see Chassis
 */

public class DriveConstantToVisionTarget extends Command implements PIDSource, PIDOutput {

    private static final double kP = 0.05, kI = 0, kD = 0;
    private static final long TIME_ON_TARGET = 200;
    private PIDController m_controller;
    private long m_onTarget = -1;
    private static final double MINIMUM_OUTPUT = 0.35;
    private boolean m_shouldRun = true;

    public DriveConstantToVisionTarget() {
        requires(Chassis.getInstance());
        m_controller = new PIDController(kP, kI, kD, this, this);
    }

    protected void initialize() {
        m_onTarget = -1;
        m_controller.setAbsoluteTolerance(0.1);
        m_controller.setSetpoint(0.5);
        m_controller.setOutputRange(-0.8, 0.8);
        m_controller.enable();
    }

    @Override
    protected void execute() {
        if (m_shouldRun) {
            //Count time on target.
            if (m_controller.onTarget())
                if (m_onTarget == -1)
                    m_onTarget = System.currentTimeMillis();
                else
                    m_onTarget = -1;
        }
    }

    protected boolean isFinished() {
        return !m_shouldRun || m_controller.onTarget() && System.currentTimeMillis() - m_onTarget > TIME_ON_TARGET;
    }

    protected void end() {
        m_controller.disable();
        Chassis.getInstance().stop();
        Chassis.getInstance().setCoast();
        System.out.println("finished");
    }

    @Override
    public void pidWrite(double output) {
        if (Math.abs(output) < MINIMUM_OUTPUT)
            output = Math.signum(output) * MINIMUM_OUTPUT;
        SmartDashboard.putNumber("PID Output", output);
        Chassis.getInstance().arcadeDrive(0.65, output);

        //Engage brake mode when on target.
//        if (m_controller.onTarget() && Chassis.getInstance().getSpeed() != 0)
//            Chassis.getInstance().setBrake();
//        else
//            Chassis.getInstance().setCoast();
    }

    @Override
    public void setPIDSourceType(PIDSourceType pidSource) {
    }

    @Override
    public PIDSourceType getPIDSourceType() {
        return PIDSourceType.kDisplacement;
    }

    @Override
    public double pidGet() {
        return OI.getInstance().getHatchAngle();
    }
}