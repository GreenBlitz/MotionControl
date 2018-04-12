package gbmotion.appc;

import edu.wpi.first.wpilibj.command.Command;

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