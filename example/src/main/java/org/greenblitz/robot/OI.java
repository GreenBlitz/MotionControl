package org.greenblitz.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.greenblitz.motion.app.AdaptivePolynomialPursuitController;
import org.greenblitz.motion.app.AdaptivePurePursuitController;
import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Position;
import org.greenblitz.motion.pathing.Path;
import org.greenblitz.motion.pathing.PolynomialInterpolator;
import org.greenblitz.robot.commands.APPCTestingCommand;
import org.greenblitz.robot.commands.ArcadeDriveByJoystick;
import org.greenblitz.robot.commands.ResetLocalizer;
import org.greenblitz.robot.commands.TankDriveByJoystick;
import org.greenblitz.utils.SmartJoystick;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;
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
        mainJS.B.whenPressed(new ResetLocalizer());
        Point p1 = new Point(0, 0);
        Point p2 = new Point(0, 1);
        Point p3 = new Point(1, 0);
        Point p4 = new Point(1, 2);

        ArrayList<Position> lst = new ArrayList<>();
        for (double i = 0; i <= 1; i++) {
            lst.add(new Position(Point.bezierSample(i, getPath("Double Hatch Cargoship1.pf1.csv"))));
        }
        // for (double i = 0; i <= 1; i++) {
        //     lst.add(new Position(Point.bezierSample(i, p1, p2, p3, p4)));
        // }
        mainJS.A.whenPressed(new APPCTestingCommand(
                new AdaptivePurePursuitController(
                new Path(lst),
                        0.5, RobotStats.Ragnarok.WHEELBASE,
                        0.1, false, 0.3, 0.5, 0.7)
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

    private static Point[] getPath(String filename) {
        CSVParser read;
            try {
                File f= new File(Paths.class.getResource("output\\" + filename).toURI());
                read = CSVFormat.EXCEL.parse(new FileReader(f)); 
                ArrayList<Point> path = new ArrayList<>();
                for (var record : read) 
                    path.add(new Point(Double.parseDouble(record.get("x")), Double.parseDouble(record.get("y"))));
                return (Point[])path.toArray();          
            } catch (Exception e) {}
        return new Point[0];
    } 
}