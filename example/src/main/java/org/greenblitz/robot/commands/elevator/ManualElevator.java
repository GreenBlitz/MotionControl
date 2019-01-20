package org.greenblitz.robot.commands.elevator;

import edu.wpi.first.wpilibj.command.Command;
import org.greenblitz.robot.subsystems.ElevatorPrototype;
import org.greenblitz.utils.SmartJoystick;

public class ManualElevator extends Command {

    private SmartJoystick m_joystick;

    public ManualElevator(SmartJoystick joystick) {
        requires(ElevatorPrototype.getInstance());
        m_joystick = joystick;
    }

    @Override
    protected void execute() {
        ElevatorPrototype.getInstance().set(Math.min(
                m_joystick.getAxisValue(SmartJoystick.JoystickAxis.LEFT_Y), -0.1));
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
