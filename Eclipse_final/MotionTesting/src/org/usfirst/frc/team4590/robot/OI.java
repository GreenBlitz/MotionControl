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
	/*
	private static OI instance;
	
	private Joystick MainJS = new Joystick(RobotMap.MAINJS_ID);
	
	private OI() {
		MainJS.addBinding(XboxButtons.R1, new WhenPressedkeyHandler(), new Collect());
		MainJS.addBinding(XboxButtons.L1, new WhenPressedkeyHandler(), new ShootToSwitch());
	}

	public static final void init() {
		instance = new OI();
	}

	public static final OI getInstance() {
		return instance;
	}
	
	public void updateSticks(){
		MainJS.updateKeys();
	}
	
	public static double getRawAxis(XboxJoystick stick,  XboxAxis axis) {
		return stick.key(axis).getState().getState();
	}
	
	public XboxJoystick getMainJS() {
		return MainJS;
	}*/
}
