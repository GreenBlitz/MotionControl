package org.greenblitz.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.greenblitz.motion.Localizer;
import org.greenblitz.motion.app.AdaptivePurePursuitController;
import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Position;
import org.greenblitz.motion.pathing.Path;
import org.greenblitz.robot.OI;
import org.greenblitz.robot.RobotStats;
import org.greenblitz.robot.commands.vision.DriveToVisionTarget;
import org.greenblitz.robot.commands.vision.DriveToVisionTargetMotion;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class MotionAndVision extends CommandGroup {
    public MotionAndVision() {
//        addSequential(new APPCTestingCommand(
//                new AdaptivePurePursuitController(
//                        new Path<>(getPath("Vision Double Hatch Cargoship1.pf1.csv")),
//                        0.5, RobotStats.Ragnarok.WHEELBASE,
//                        0.2, false, 0.5, 0.6, 1)
//                , new Position(-3.073, 1.5)));
        addSequential(new DriveToVisionTarget());
        addSequential(new APPCTestingCommand(
                new AdaptivePurePursuitController(
                        new Path<>(getPath("Double Hatch Cargoship2.pf1.csv")),
                        0.5, RobotStats.Ragnarok.WHEELBASE,
                        0.2, true, 0.5, 0.5, 0.7),
                -3.467, 6.614, null));
        addSequential(new APPCTestingCommand(
                new AdaptivePurePursuitController(
                        new Path<>(getPath("Vision Double Hatch Cargoship3.pf1.csv")),
                        0.5, RobotStats.Ragnarok.WHEELBASE,
                        0.1, false, 0.3, 0.5, 0.6)));
        addSequential(new DriveToVisionTarget());
        addSequential(new APPCTestingCommand(
                new AdaptivePurePursuitController(
                        new Path<>(getPath("Double Hatch Cargoship4.pf1.csv")),
                        0.5, RobotStats.Ragnarok.WHEELBASE,
                        0.1, true, 0.3, 0.5, 0.6),
                -0.651, 0.0, null));
        addSequential(new APPCTestingCommand(
                new AdaptivePurePursuitController(
                        new Path<>(getPath("Vision Double Hatch Cargoship5.pf1.csv")),
                        0.5, RobotStats.Ragnarok.WHEELBASE,
                        0.1, false, 0.3, 0.5, 0.6)));
        addSequential(new DriveToVisionTarget());
    }

    private Position[] getPath(String filename) {
        CSVParser read;
        try {
            read = CSVFormat.DEFAULT.parse(new FileReader(new File("/home/lvuser/deploy/output/" + filename)));
            ArrayList<Position> path = new ArrayList<>();
            List<CSVRecord> records = read.getRecords();
            for (int i = 1; i < records.size() ; i++) {
                path.add(new Position(new Point(Double.parseDouble(records.get(i).get(1)), Double.parseDouble(records.get(i).get(2))).weaverToLocalizerCoords()));
            }
            System.out.println(filename + ": " + path);
            return path.toArray(new Position[path.size()]);
        } catch (Exception e) { e.printStackTrace(); }
        System.out.println("Failed to read file");
        return new Position[0];
    }
}
