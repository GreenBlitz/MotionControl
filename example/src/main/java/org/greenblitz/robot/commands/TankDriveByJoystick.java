package org.greenblitz.example.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.greenblitz.example.robot.subsystems.Chassis;
import org.greenblitz.example.utils.SmartJoystick;

public class TankDriveByJoystick extends Command {
    private SmartJoystick m_stick;

    public TankDriveByJoystick(SmartJoystick stick) {
        requires(Chassis.getInstance());
        m_stick = stick;
    }

    protected void execute() {
        Chassis.getInstance().tankDrive(m_stick.getAxisValue(SmartJoystick.JoystickAxis.LEFT_Y),
                m_stick.getAxisValue(SmartJoystick.JoystickAxis.RIGHT_Y));
    }

    protected boolean isFinished() {
        return false;
    }

    @Override
    protected void end() {
        Chassis.getInstance().stop();
    }
}