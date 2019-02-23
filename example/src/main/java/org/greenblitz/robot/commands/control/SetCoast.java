package org.greenblitz.robot.commands.control;

import edu.wpi.first.wpilibj.command.Command;
import org.greenblitz.robot.subsystems.Chassis;

public class SetCoast extends Command {

    public SetCoast() {
        requires(Chassis.getInstance());
    }

    @Override
    protected void initialize() {
        Chassis.getInstance().setCoast();
    }

    @Override
    protected boolean isFinished() {
        return true;
    }
}