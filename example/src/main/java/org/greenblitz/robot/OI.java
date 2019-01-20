package org.greenblitz.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import org.greenblitz.motion.profiling.ActuatorLocation;
import org.greenblitz.robot.commands.elevator.ManualElevator;
import org.greenblitz.robot.commands.elevator.MoveByProfileElevator;
import org.greenblitz.robot.commands.elevator.StopElevator;
import org.greenblitz.robot.commands.vision.DriveToPanel;
import org.greenblitz.utils.SmartJoystick;

public class OI {

    private static OI instance;

    private NetworkTable visionTable;

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
        mainJS.B.whenPressed(new MoveByProfileElevator(
                new ActuatorLocation(0 ,0),
                new ActuatorLocation(1, 0)
        ));
        mainJS.Y.whenPressed(new StopElevator());
        mainJS.X.whenPressed(new DriveToPanel());
        visionTable = NetworkTableInstance.getDefault().getTable("VisionTable");
    }

    public SmartJoystick getMainJS() {
        return mainJS;
    }

    public NetworkTable getVisionTable() {
        return visionTable;
    }

    public double getHatchDistance(){
        return visionTable.getEntry("Hatch::Distance").getDouble(0);
    }

    public double getHatchAngle(){
        return visionTable.getEntry("Hatch::Angle").getDouble(0);
    }
}