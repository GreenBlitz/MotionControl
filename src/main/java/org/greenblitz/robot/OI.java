package org.greenblitz.robot;

import org.greenblitz.robot.commands.ArcadeDriveByJoystick;
import org.greenblitz.robot.commands.FindMaxValues;

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
        mainJS = new SmartJoystick(RobotMap.JoystickID.MAIN);
        mainJS.setAxisInverted(SmartJoystick.JoystickAxis.LEFT_Y, true);
        mainJS.setAxisInverted(SmartJoystick.JoystickAxis.RIGHT_Y, true);

        mainJS.A.whenPressed(new FindMaxValues());
        mainJS.B.whenPressed(new ArcadeDriveByJoystick(mainJS));
    }

    public SmartJoystick getMainJS() {
        return mainJS;
    }

}