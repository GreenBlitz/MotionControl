package org.greenblitz.robot.commands.vision;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.greenblitz.motion.app.AdaptivePurePursuitController;
import org.greenblitz.motion.pathing.Path;
import org.greenblitz.robot.OI;
import org.greenblitz.robot.RobotStats;
import org.greenblitz.robot.commands.APPCTestingCommand;
import org.greenblitz.robot.commands.SetCoast;
import org.greenblitz.robot.commands.shifter.AutoChangeShift;

public class AutoVisRocketSame extends CommandGroup {

    public AutoVisRocketSame() {
        addSequential(new SetCoast());
        addParallel(new AutoChangeShift());
        addSequential(new APPCTestingCommand(new AdaptivePurePursuitController(
                new Path<>(OI.getPath("Vis Rocket1.pf1.csv")),
                0.5, RobotStats.Ragnarok.WHEELBASE,
                0.2, false, 0.5, 0.4, 1)));
        addSequential(new DriveToVisionTarget());
        addSequential(new APPCTestingCommand(new AdaptivePurePursuitController(
                new Path<>(OI.getPath("Pure Rocket2.pf1.csv")),
                0.5, RobotStats.Ragnarok.WHEELBASE,
                0.2, false, 0.5, 0.4, 1)));
        addSequential(new APPCTestingCommand(new AdaptivePurePursuitController(
                new Path<>(OI.getPath("Vis Rocket3.pf1.csv")),
                0.5, RobotStats.Ragnarok.WHEELBASE,
                0.2, false, 0.5, 0.4, 1)));
        addSequential(new DriveToVisionTarget());
        addSequential(new APPCTestingCommand(new AdaptivePurePursuitController(
                new Path<>(OI.getPath("Pure Rocket4.pf1.csv")),
                0.5, RobotStats.Ragnarok.WHEELBASE,
                0.2, false, 0.5, 0.4, 1)));
        addSequential(new APPCTestingCommand(new AdaptivePurePursuitController(
                new Path<>(OI.getPath("Vis Rocket5.pf1.csv")),
                0.5, RobotStats.Ragnarok.WHEELBASE,
                0.2, false, 0.5, 0.4, 1)));
        addSequential(new DriveToVisionTarget());
    }
}