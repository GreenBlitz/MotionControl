package org.greenblitz.robot.commands.vision;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import org.greenblitz.motion.app.AdaptivePurePursuitController;
import org.greenblitz.motion.pathing.Path;
import org.greenblitz.robot.OI;
import org.greenblitz.robot.RobotStats;
import org.greenblitz.robot.commands.APPCTestingCommand;
import org.greenblitz.robot.commands.SetLocalizerLocation;
import org.greenblitz.robot.commands.shifter.AutoChangeShift;
import org.greenblitz.robot.subsystems.Chassis;

public class AutoPart1 extends CommandGroup {
    public AutoPart1() {
        addSequential(new Command() {
            @Override
            protected void initialize() {
                Chassis.getInstance().setCoast();
            }

            @Override
            protected boolean isFinished() {
                return true;
            }
        });
        addParallel(new AutoChangeShift());
        addSequential(new SetLocalizerLocation(-3.073, 1.5, 0.0));
        addSequential(new APPCTestingCommand(
                new AdaptivePurePursuitController(
                        new Path<>(OI.getPath("Vision Double Hatch Cargoship1.pf1.csv")),
                        0.5, RobotStats.Ragnarok.WHEELBASE,
                        0.2, false, 0.5, 0.4, 1)));
        addSequential(new DriveToVisionTarget());
    }
}
