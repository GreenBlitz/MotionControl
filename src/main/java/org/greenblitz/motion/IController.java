package org.greenblitz.motion;

/**
 * Represnts a controller
 * @author Alexey
 *
 * @param <IN> The input type, usually some sort of custom class
 * @param <OUT> The output type, usually some sort of custom class
 */
@FunctionalInterface
public interface IController<IN, OUT> {
	
	/**
	 * Run the controller
	 * @param input The relevant data (e.g. position, gyro angle, velocity)
	 * @return The value relevant for the command (e.g. motor values, input modifiers)
	 */
	public OUT execute(IN input);
	
}
