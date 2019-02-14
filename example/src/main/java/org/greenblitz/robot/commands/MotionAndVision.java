package org.greenblitz.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.greenblitz.motion.app.AdaptivePurePursuitController;
import org.greenblitz.motion.pathing.Path;
import org.greenblitz.robot.OI;
import org.greenblitz.robot.RobotStats;
import org.greenblitz.robot.commands.motion.APPCTestingCommand;
import org.greenblitz.robot.commands.motion.SetLocalizerLocation;
import org.greenblitz.robot.commands.vision.DriveToVisionTarget;

public class MotionAndVision extends CommandGroup {
    public MotionAndVision() {
        addSequential(new SetLocalizerLocation(-3.073, 1.5, 0.0));
        addSequential(new APPCTestingCommand(
                new AdaptivePurePursuitController(
                        new Path<>(OI.getPath("Vision Double Hatch Cargoship1.pf1.csv")),
                        0.5, RobotStats.Ragnarok.WHEELBASE,
                        0.2, false, 0.5, 0.6, 1)));
        addSequential(new DriveToVisionTarget());
        addSequential(new SetLocalizerLocation(-3.467, 6.614, null));
        addSequential(new APPCTestingCommand(
                new AdaptivePurePursuitController(
                        new Path<>(OI.getPath("Double Hatch Cargoship2.pf1.csv")),
                        0.5, RobotStats.Ragnarok.WHEELBASE,
                        0.2, true, 0.5, 0.5, 0.7)));
        addSequential(new APPCTestingCommand(
                new AdaptivePurePursuitController(
                        new Path<>(OI.getPath("Vision Double Hatch Cargoship3.pf1.csv")),
                        0.5, RobotStats.Ragnarok.WHEELBASE,
                        0.1, false, 0.3, 0.5, 0.6)));
        addSequential(new DriveToVisionTarget());
        addSequential(new SetLocalizerLocation(-0.651, 0.0, null));
        addSequential(new APPCTestingCommand(
                new AdaptivePurePursuitController(
                        new Path<>(OI.getPath("Double Hatch Cargoship4.pf1.csv")),
                        0.5, RobotStats.Ragnarok.WHEELBASE,
                        0.1, true, 0.3, 0.5, 0.6)));
        addSequential(new APPCTestingCommand(
                new AdaptivePurePursuitController(
                        new Path<>(OI.getPath("Vision Double Hatch Cargoship5.pf1.csv")),
                        0.5, RobotStats.Ragnarok.WHEELBASE,
                        0.1, false, 0.3, 0.5, 0.6)));
        addSequential(new DriveToVisionTarget());
    }
}