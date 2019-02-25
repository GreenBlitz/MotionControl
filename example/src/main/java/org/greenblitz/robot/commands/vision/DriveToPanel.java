package org.greenblitz.robot.commands.vision;

import edu.wpi.first.wpilibj.command.Command;
import org.greenblitz.motion.pid.MultivariablePIDController;
import org.greenblitz.motion.pid.PIDObject;
import org.greenblitz.motion.tolerance.AbsoluteTolerance;
import org.greenblitz.robot.OI;
import org.greenblitz.robot.subsystems.Chassis;

public class DriveToPanel extends Command {

    MultivariablePIDController controller;

    public DriveToPanel() {
        requires(Chassis.getInstance());
        controller = new MultivariablePIDController(2);
        controller.setPIDObject(0, new PIDObject(0.8, 0, 0), new AbsoluteTolerance(0.1));
        controller.setPIDObject(1, new PIDObject(0.8, 0, 0), new AbsoluteTolerance(3));
    }

    @Override
    protected void initialize(){
        controller.setGoals(OI.getInstance().getHatchAngle(), OI.getInstance().getHatchDistance());
    }

    @Override
    protected void execute() {
        double[] pidVals = controller.calculate(
                OI.getInstance().getHatchAngle(), OI.getInstance().getHatchDistance());

        Chassis.getInstance().arcadeDrive(pidVals[0], -pidVals[1]);
    }

    @Override
    protected boolean isFinished() {
        return controller.isFinished();
    }

    @Override
    protected void end() {
        Chassis.getInstance().stop();
    }
}
