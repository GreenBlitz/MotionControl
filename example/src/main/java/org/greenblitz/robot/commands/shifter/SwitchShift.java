package org.greenblitz.robot.commands.shifter;

import edu.wpi.first.wpilibj.command.Command;
import org.greenblitz.robot.subsystems.Shifter;

/**
 * This command switches the ShifterState from the state it is currently in.
 * This command uses the Shifter subsystem.
 * The command will stop as soon as the shift is switched.
 */

public class SwitchShift extends Command {

    public SwitchShift() {
        requires(Shifter.getInstance());
    }

    @Override
    protected void execute() {
        Shifter.getInstance().setShift(Shifter.getInstance().getCurrentShift() == Shifter.ShifterState.POWER ?
                Shifter.ShifterState.SPEED : Shifter.ShifterState.POWER);
    }

    @Override
    protected boolean isFinished() {
        return true;
    }
}
