package org.greenblitz.motion;

import edu.wpi.first.wpilibj.command.Command;
import jaci.pathfinder.Trajectory;

/**
 * This is the Ben-Guruion controller. It makes a path and it always stays on it by changing it.
 * BGC, Just like Ben-Guruion, is never wrong. It's the truth that's wrong.
 * TODO - Atsmon doesn't approve the name
 */
public class BGController extends Command {

    protected Trajectory current;

    public BGController(Trajectory initial){
        this.current = initial;
    }

    @Override
    protected boolean isFinished() {
        return true;
    }

    @Override
    protected void initialize() {

    }

    @Override
    protected void execute() {

    }

    @Override
    protected void end() {

    }
}
