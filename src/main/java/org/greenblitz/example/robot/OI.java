package org.greenblitz.example.robot;

import org.greenblitz.example.robot.commands.APPCTestingCommand;
import org.greenblitz.example.robot.commands.ArcadeDriveByJoystick;
import org.greenblitz.example.robot.commands.FindMaxValues;
import org.greenblitz.example.robot.commands.PathFollowerCommand;
import org.greenblitz.example.robot.commands.elevator.ManualElevator;
import org.greenblitz.example.robot.commands.elevator.MoveByProfileElevator;
import org.greenblitz.example.robot.commands.elevator.StopElevator;
import org.greenblitz.example.robot.subsystems.Chassis;
import org.greenblitz.example.utils.SmartJoystick;
import org.greenblitz.motion.base.Point;

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