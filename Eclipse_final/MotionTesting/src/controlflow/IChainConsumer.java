package controlflow;

/**
 * 
 * This interface
 * 
 * @author Memes
 *
 * @param <C>
 *            the consumed type
 * @param <T>
 *            processing status type
 */
public interface IChainConsumer<C, T> extends IChainable {

	/**
	 * This method is used to implement custom connectivity and processing for
	 * certain inputs
	 * 
	 * @return should this consumer be processed in the normal transferring system
	 */
	boolean isCustomConsumer();

	/**
	 * This method is called when the consumer node should process a given data
	 * 
	 * @param value
	 *            the value to use for the consumer node cycle
	 * @return some kind of data which represents the completion status of the
	 *         process
	 */
	T processData(C value);

	/**
	 * This method is used to construct the call hierarchy by which its containing
	 * chain executes the node's operation
	 * 
	 * @param node
	 *            the node to get input from
	 * @return whether or not the simulated input was accepted by this consumer node
	 */
	boolean simulateInput(IChainable node);

	/**
	 * This method is used by the containing chain to determine whether it should
	 * continue to look for outputs for this nodes or not
	 * 
	 * @return whether or not all simulated inputs required for this consumer to run
	 *         have been resolved
	 */
	boolean hasSimulatedInput();

}
