package org.greenblitz.robot.commands.control;

import edu.wpi.first.wpilibj.command.Command;
import org.greenblitz.robot.subsystems.Chassis;

public class BrakeChassis extends Command {

    public BrakeChassis() {
        requires(Chassis.getInstance());
    }

    @Override
    protected void initialize() {
        Chassis.getInstance().setBrake();
    }

    @Override
    protected boolean isFinished() {
        return true;
    }
}