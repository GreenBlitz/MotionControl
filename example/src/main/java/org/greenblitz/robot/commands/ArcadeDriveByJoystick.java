package org.greenblitz.example.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.greenblitz.example.robot.subsystems.Chassis;
import org.greenblitz.example.utils.SmartJoystick;

public class ArcadeDriveByJoystick extends Command {
    private SmartJoystick m_stick;

    public ArcadeDriveByJoystick(SmartJoystick stick) {
        requires(Chassis.getInstance());
        m_stick = stick;
    }

    protected void execute() {
        SmartDashboard.putNumber("forwards", m_stick.getAxisValue(SmartJoystick.JoystickAxis.LEFT_Y));
        SmartDashboard.putNumber("rotational", m_stick.getAxisValue(SmartJoystick.JoystickAxis.RIGHT_X));
        Chassis.getInstance().arcadeDrive(m_stick.getAxisValue(SmartJoystick.JoystickAxis.LEFT_Y),
                m_stick.getAxisValue(SmartJoystick.JoystickAxis.RIGHT_X));
    }

    protected boolean isFinished() {
        return false;
    }

    @Override
    protected void end() {
        Chassis.getInstance().stop();
    }
}