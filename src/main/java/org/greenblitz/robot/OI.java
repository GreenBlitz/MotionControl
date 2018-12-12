package org.greenblitz.robot;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import org.greenblitz.motion.Localizer;
import org.greenblitz.robot.commands.ArcadeDriveByJoystick;
import org.greenblitz.robot.commands.FindMaxValues;
import org.greenblitz.robot.commands.FollowPoints;

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

        mainJS.X.whenPressed(new FollowPoints(
                Trajectory.FitMethod.HERMITE_CUBIC,
                10000,
                2,
                new Waypoint[]{
                        new Waypoint(0, 0, 0),
                        new Waypoint(1, 1, 0),
                        new Waypoint(3, 0, 0)
                }));

    }

    public SmartJoystick getMainJS() {
        return mainJS;
    }

}