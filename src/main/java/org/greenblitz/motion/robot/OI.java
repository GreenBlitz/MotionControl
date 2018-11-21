package org.greenblitz.motion.robot;

public class OI {

    private static OI instance;

    private SmartJoystick mainJS;

    public static OI getInstance() {
        return instance;
    }

    public static void init() {
        instance = new OI();
    }

    private OI() {
        mainJS = new SmartJoystick(RobotMap.MAINJS_ID);
        mainJS.setAxisInverted(SmartJoystick.JoystickAxis.LEFT_Y, true);
        mainJS.setAxisInverted(SmartJoystick.JoystickAxis.RIGHT_Y, true);

    }

    public SmartJoystick getMainJS() {
        return mainJS;
    }

}