package org.greenblitz.robot;

import org.greenblitz.utils.SmartJoystick;
import org.greenblitz.robot.commands.ArcadeDriveByJoystick;
import org.greenblitz.robot.commands.FindMaxValues;
import org.greenblitz.robot.commands.elevator.ManualElevator;
import org.greenblitz.robot.commands.elevator.MoveByProfileElevator;
import org.greenblitz.robot.commands.elevator.StopElevator;
import org.greenblitz.robot.subsystems.Chassis;

public class OI {

    private static OI instance;

    private SmartJoystick mainJS;

    public static OI getInstance() {
        if (instance == null) init();
        return instance;
    }

    public static void init() {
        instance = new OI();
    }

    private OI() {
        mainJS = new SmartJoystick(org.greenblitz.robot.RobotMap.JoystickID.MAIN);
        //mainJS.setAxisInverted(SmartJoystick.JoystickAxis.LEFT_Y, true);
        //mainJS.setAxisInverted(SmartJoystick.JoystickAxis.RIGHT_Y, true);
        //mainJS.A.whenPressed(new FindMaxValues());
        //mainJS.B.whenPressed(new ArcadeDriveByJoystick(mainJS));
        //mainJS.Y.whenPressed(null);
        mainJS.A.whileHeld(new ManualElevator(mainJS));
        mainJS.B.whenPressed(new MoveByProfileElevator());
        mainJS.Y.whenPressed(new StopElevator());

    }

    public SmartJoystick getMainJS() {
        return mainJS;
    }

}