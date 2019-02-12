package org.greenblitz.robot.commands.vision;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import org.greenblitz.motion.app.AdaptivePurePursuitController;
import org.greenblitz.motion.pathing.Path;
import org.greenblitz.robot.OI;
import org.greenblitz.robot.RobotStats;
import org.greenblitz.robot.commands.APPCTestingCommand;
import org.greenblitz.robot.commands.BrakeChassis;
import org.greenblitz.robot.commands.SetLocalizerLocation;
import org.greenblitz.robot.commands.shifter.AutoChangeShift;
import org.greenblitz.robot.commands.shifter.SetShift;
import org.greenblitz.robot.subsystems.Shifter;

public class AutoPart2 extends CommandGroup {
    public AutoPart2() {
        addSequential(new SetLocalizerLocation(-3.467, 6.614, null));
        addSequential(new SetShift(Shifter.ShifterState.POWER));
        addSequential(new APPCTestingCommand(
                new AdaptivePurePursuitController(
                        new Path<>(OI.getPath("Double Hatch Cargoship2.pf1.csv")),
                        0.5, RobotStats.Ragnarok.WHEELBASE,
                        0.2, true, 0.5, 0.3, 1)));
        addParallel(new AutoChangeShift());
        addSequential(new APPCTestingCommand(
                new AdaptivePurePursuitController(
                        new Path<>(OI.getPath("Vision Double Hatch Cargoship3.pf1.csv")),
                        0.5, RobotStats.Ragnarok.WHEELBASE,
                        0.25, false, 0.4, 1, 1)));
        addSequential(new DriveToVisionTarget());
    }
}
