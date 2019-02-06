package org.greenblitz.robot.commands.shifter;

import edu.wpi.first.wpilibj.command.Command;
import org.greenblitz.robot.subsystems.Shifter;

public class SetShift extends Command {

    private static Shifter.ShifterState m_shifterState;

    public SetShift(Shifter.ShifterState state) {
        requires(Shifter.getInstance());
        m_shifterState = state;
    }

    @Override
    protected void execute() {
        Shifter.getInstance().setShift(m_shifterState);
    }

    @Override
    protected boolean isFinished() {
        return true;
    }
}
