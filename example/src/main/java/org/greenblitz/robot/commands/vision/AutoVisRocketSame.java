package org.greenblitz.robot.commands.vision;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.greenblitz.motion.app.AdaptivePurePursuitController;
import org.greenblitz.motion.pathing.Path;
import org.greenblitz.robot.OI;
import org.greenblitz.robot.RobotStats;
import org.greenblitz.robot.commands.motion.APPCTestingCommand;
import org.greenblitz.robot.commands.control.SetCoast;

public class AutoVisRocketSame extends CommandGroup {

    public AutoVisRocketSame() {
        addSequential(new SetCoast());
//        addParallel(new AutoChangeShift());
        addSequential(new APPCTestingCommand(new AdaptivePurePursuitController(
                new Path<>(OI.getPath("Vis Rocket1.pf1.csv")),
                0.8, RobotStats.Ragnarok.WHEELBASE,
                0.15, false, 0.6, 0.2, 1)));
        addSequential(new DriveToVisionTarget());
        addSequential(new APPCTestingCommand(new AdaptivePurePursuitController(
                new Path<>(OI.getPath("Pure Rocket2.pf1.csv")),
                0.8, RobotStats.Ragnarok.WHEELBASE,
                0.2, true, 0.5, 0.05, 1)));
        addSequential(new APPCTestingCommand(new AdaptivePurePursuitController(
                new Path<>(OI.getPath("Vis Rocket3.pf1.csv")),
                0.8, RobotStats.Ragnarok.WHEELBASE,
                0.15, false, 0.6, 0.2, 1)));
        addSequential(new DriveToVisionTarget());
        addSequential(new APPCTestingCommand(new AdaptivePurePursuitController(
                new Path<>(OI.getPath("Pure Rocket Same-Side4.pf1.csv")),
                0.8, RobotStats.Ragnarok.WHEELBASE,
                0.2, true, 0.5, 0.05, 1)));
        addSequential(new APPCTestingCommand(new AdaptivePurePursuitController(
                new Path<>(OI.getPath("Vis Rocket Same-Side5.pf1.csv")),
                0.5, RobotStats.Ragnarok.WHEELBASE,
                0.15, false, 0.5, 0.2, 1)));
        addSequential(new DriveToVisionTarget());
    }
}