package base;

/**
 *
 * Represents a generalized input for controller
 */
@FunctionalInterface
public interface Input<T> {
	/**
	 * 
	 * @return the input form the sensors
	 */
	T recieve();
}
