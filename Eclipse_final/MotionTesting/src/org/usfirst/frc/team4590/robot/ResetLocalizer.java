package org.usfirst.frc.team4590.robot;

import edu.wpi.first.wpilibj.command.Command;
import gbmotion.appc.Localizer;

public class ResetLocalizer extends Command {

	private Localizer fuckme;
	
	public ResetLocalizer(Localizer localizer) {
		fuckme = localizer;
	}
	
    protected void execute() {
    	fuckme.reset();
    }

    protected boolean isFinished() {
    	return true;
    }
}