package org.greenblitz.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.greenblitz.motion.app.AdaptivePurePursuitController;
import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Position;
import org.greenblitz.motion.pathing.Path;
import org.greenblitz.robot.OI;
import org.greenblitz.robot.RobotStats;
import org.greenblitz.robot.commands.vision.DriveToVisionTarget;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class MotionAndVision extends CommandGroup {
    public MotionAndVision() {
        addSequential(new APPCTestingCommand(
                new AdaptivePurePursuitController(
                        new Path<>(
                                getPath("Test Path.pf1.csv")),
                        0.5, RobotStats.Ragnarok.WHEELBASE,
                        0.2, false, 0.3, 0.7, 1)
                , new Position(3.073, 1.5)));
        addSequential(new DriveToVisionTarget());
    }

    private Position[] getPath(String filename) {
        CSVParser read;
        try {
            read = CSVFormat.DEFAULT.parse(new FileReader(new File("/home/lvuser/deploy/output/" + filename)));
            ArrayList<Position> path = new ArrayList<>();
            List<CSVRecord> records = read.getRecords();
            for (int i = 1; i < records.size() ; i++) {
                path.add(new Position(new Point(Double.parseDouble(records.get(i).get(2)), Double.parseDouble(records.get(i).get(1)))));
            }
            System.out.println(path);
            return path.toArray(new Position[path.size()]);
        } catch (Exception e) { e.printStackTrace(); }
        System.out.println("Failed to read file");
        return new Position[0];
    }
}
