package org.greenblitz.example.robot.commands.elevator;

import edu.wpi.first.wpilibj.command.Command;
import org.greenblitz.example.robot.subsystems.ElevatorPrototype;

public class StopElevator extends Command {

    public StopElevator() {
        requires(ElevatorPrototype.getInstance());
    }

    @Override
    protected void execute() {
        ElevatorPrototype.getInstance().set(-0.4);
    }

    @Override
    protected boolean isFinished() {
        return false;
    }

    @Override
    protected void end() {
        ElevatorPrototype.getInstance().stop();
    }
}
