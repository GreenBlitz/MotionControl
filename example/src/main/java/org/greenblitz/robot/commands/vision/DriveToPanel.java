package org.greenblitz.robot.commands.vision;

import edu.wpi.first.wpilibj.command.Command;
import org.greenblitz.motion.pid.MultivariablePIDController;
import org.greenblitz.motion.pid.PIDObject;
import org.greenblitz.robot.OI;
import org.greenblitz.robot.subsystems.Chassis;

public class DriveToPanel extends Command {

    MultivariablePIDController controller;

    public DriveToPanel(){
        requires(Chassis.getInstance());
        controller = new MultivariablePIDController(
                new PIDObject(0.8, 0 ,0),
                new PIDObject(0.8, 0 ,0)
        );
    }

    @Override
    protected void initialize(){
        controller.init(
                new double[] {0, 0},
                new double[] {OI.getInstance().getHatchAngle(), OI.getInstance().getHatchDistance()}
        );
    }

    @Override
    protected void execute() {
        double[] pidVals = controller.calculatePID(
                new double[] {0, 0},
                new double[] {OI.getInstance().getHatchAngle(), OI.getInstance().getHatchDistance()}
        );
        Chassis.getInstance().arcadeDrive(pidVals[1], pidVals[0]);
    }

    @Override
    protected boolean isFinished() {
        return controller.isFinished(new double[] {0, 0},
                new double[] {OI.getInstance().getHatchAngle(), OI.getInstance().getHatchDistance()},
                0.1);
    }

    @Override
    protected void end() {
        Chassis.getInstance().stop();
    }

}
