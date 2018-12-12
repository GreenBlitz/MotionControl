package org.greenblitz.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.greenblitz.robot.subsystems.Chassis;
import org.greenblitz.robot.SmartJoystick;

public class ArcadeDriveByJoystick extends Command {
    private SmartJoystick m_stick;
    private final double mult = 1;

    public ArcadeDriveByJoystick(SmartJoystick stick) {
        requires(Chassis.getInstance());
        m_stick = stick;
    }

    protected void execute() {
        SmartDashboard.putNumber("forwards", m_stick.getAxisValue(SmartJoystick.JoystickAxis.LEFT_Y) * mult);
        SmartDashboard.putNumber("rotational", m_stick.getAxisValue(SmartJoystick.JoystickAxis.RIGHT_X) * mult);
        Chassis.getInstance().arcadeDrive(m_stick.getAxisValue(SmartJoystick.JoystickAxis.LEFT_Y) * mult,
                m_stick.getAxisValue(SmartJoystick.JoystickAxis.RIGHT_X) * mult);
    }

    protected boolean isFinished() {
        return false;
    }

    @Override
    protected void end() {
        Chassis.getInstance().stop();
    }
}