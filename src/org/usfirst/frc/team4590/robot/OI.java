package org.usfirst.frc.team4590.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import gbmotion.appc.Localizer;
import gbmotion.appc.ResetLocalizer;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {
	
	private static OI instance;
	
	private Joystick stick;
	
	private static Localizer fuckmetosend;
	
	public static OI getInstance() {
		if (instance == null)
			instance = new OI();
		return instance;
	}
	
	public static void init(Localizer localizer) {
		fuckmetosend = localizer;
		instance = new OI();
	}
	
	public Joystick getJoystick() {
		return stick;
	}
	
	private OI() {
		stick = new Joystick(0);
		
		JoystickButton A = new JoystickButton(stick, 1);	
		
		A.whenPressed(new ResetLocalizer(fuckmetosend));
	}
}
