package org.greenblitz.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import org.greenblitz.motion.app.Path;
import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Position;
import org.greenblitz.robot.commands.APPCTestingCommand;
import org.greenblitz.robot.commands.ArcadeDriveByJoystick;
import org.greenblitz.robot.commands.ResetLocalizer;
import org.greenblitz.robot.commands.TankDriveByJoystick;
import org.greenblitz.robot.subsystems.Chassis;
import org.greenblitz.utils.SmartJoystick;

import java.util.ArrayList;

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
        //mainJS.X.whenPressed(new DriveToPanel());
        mainJS.B.whenPressed(new ResetLocalizer());
        ArrayList<Position> lst = new ArrayList<>();
        Point start = new Point(0,0);
        Point secnod = new Point(0,1);
        Point third = new Point(1,0);
        Point end = new Point(1,1);
        for(double i=0; i<=1; i++){
            lst.add(new Position(Point.bezierSample(i, start, secnod, third, end)));
        }
        mainJS.A.whenPressed(new APPCTestingCommand(0.5, RobotStats.Ragnarok.WHEELBASE, new Path(lst), 0.3));
        mainJS.X.whenPressed(new ArcadeDriveByJoystick(mainJS));
        mainJS.R1.whenPressed(new TankDriveByJoystick(mainJS));
        visionTable = NetworkTableInstance.getDefault().getTable("VisionTable");
    }

    public SmartJoystick getMainJS() {
        return mainJS;
    }

    public NetworkTable getVisionTable() {
        return visionTable;
    }

    public double getHatchDistance() {
        return visionTable.getEntry("Hatch::Distance").getDouble(0);
    }

    public double getHatchAngle() {
        return visionTable.getEntry("Hatch::Angle").getDouble(0);
    }
}