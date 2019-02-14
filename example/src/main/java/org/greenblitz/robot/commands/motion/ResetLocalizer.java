package org.greenblitz.robot.commands.motion;

import org.greenblitz.robot.commands.PeriodicCommand;
import org.greenblitz.robot.subsystems.Chassis;

public class ResetLocalizer extends PeriodicCommand {


    @Override
    protected void initialize() {
        Chassis.getInstance().resetSensors();
        super.initialize();
    }

    @Override
    protected void periodic() {

    }

    @Override
    protected boolean isFinished() {
        return true;
    }
}
