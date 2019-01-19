package org.greenblitz.robot.commands.elevator;

import edu.wpi.first.wpilibj.command.Command;
import org.greenblitz.robot.subsystems.ElevatorPrototype;

public class StopElevator extends Command {

    private long inceptionTime;

    public StopElevator() {
        requires(ElevatorPrototype.getInstance());
    }

    @Override
    protected void initialize() { inceptionTime = System.currentTimeMillis(); }

    @Override
    protected void execute() {
        if (System.currentTimeMillis() - inceptionTime < 5000)
            ElevatorPrototype.getInstance().set(-0.4);
        else if (ElevatorPrototype.getInstance().getDistance() > 0.1)
            ElevatorPrototype.getInstance().set(-0.1);
        else
            ElevatorPrototype.getInstance().set(0);
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
