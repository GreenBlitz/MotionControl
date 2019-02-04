package org.greenblitz.robot.commands.shifter;

import edu.wpi.first.wpilibj.command.Command;
import org.greenblitz.robot.subsystems.Chassis;
import org.greenblitz.robot.subsystems.Shifter;

public class AutoChangeShift extends Command {

    private static final double TO_POWER_THRESHOLD = 1.2,
                                TO_SPEED_THRESHOLD = 1.6;


    public AutoChangeShift () {
        requires(Shifter.getInstance());
    }

    @Override
    protected void execute() {
        if (Chassis.getInstance().getAbsoluteSpeed() > TO_SPEED_THRESHOLD &&
                Shifter.getInstance().getCurrentShift() == Shifter.ShifterState.POWER)
            Shifter.getInstance().setShift(Shifter.ShifterState.SPEED);
        if (Chassis.getInstance().getAbsoluteSpeed() < TO_POWER_THRESHOLD &&
                Shifter.getInstance().getCurrentShift() == Shifter.ShifterState.SPEED)
            Shifter.getInstance().setShift(Shifter.ShifterState.POWER);
    }

    @Override
    protected boolean isFinished() {
        return false;
    }
}