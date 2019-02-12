package org.greenblitz.robot.commands.vision;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoFull extends CommandGroup {
    public AutoFull() {
        addSequential(new AutoPart1());
        addSequential(new AutoPart2());
        addSequential(new AutoPart3());
    }
}
