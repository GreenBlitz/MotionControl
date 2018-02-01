package base;

/**
 *
 * Represents controller usage
 */
@FunctionalInterface
public interface Output<T> {
	/**
	 * Applies the output to the actuators of the robot
	 * 
	 * @param output
	 *            the value to use
	 */
	void use(T output);

	/**
	 * Returns the value that stops the relevant actuators
	 * 
	 * @return
	 */
	default T noPower() {
		return null;
	}

	/**
	 * Stop the relevant actuator, default behaviour is to use
	 * {@link Output#noPower()}
	 */
	default void stop() {
		use(noPower());
	}
}
