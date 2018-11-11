package org.usfirst.frc.team4590.motion;

@FunctionalInterface
public interface IController<IN, OUT> {
	
	public OUT execute(IN input);
	
}
