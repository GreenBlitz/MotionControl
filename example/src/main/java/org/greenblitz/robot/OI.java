package org.greenblitz.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import org.greenblitz.motion.app.AdaptivePolynomialPursuitController;
import org.greenblitz.motion.app.AdaptivePurePursuitController;
import org.greenblitz.motion.base.Position;
import org.greenblitz.motion.pathing.Path;
import org.greenblitz.motion.pathing.PolynomialInterpolator;
import org.greenblitz.robot.commands.APPCTestingCommand;
import org.greenblitz.robot.commands.ArcadeDriveByJoystick;
import org.greenblitz.robot.commands.ResetLocalizer;
import org.greenblitz.robot.commands.TankDriveByJoystick;
import org.greenblitz.utils.SmartJoystick;
import org.opencv.core.Mat;

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
        mainJS.A.whenPressed(new APPCTestingCommand(
                new AdaptivePolynomialPursuitController(PolynomialInterpolator.interpolatePoints(new Path(
                        new Position(0, 0),
                        new Position(0.1, 0.9, Math.PI / 4),
                        new Position(0.65, 1, Math.PI / 2)),
                        50),

                        0.5, RobotStats.Ragnarok.WHEELBASE,
                        0.10, false, 0.1, 0.6)
        ));
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