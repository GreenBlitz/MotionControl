package controlflow;

/**
 * This interface represents an IChainable node which is used as an input for
 * following consumer nodes
 * 
 * @author Memes
 * 
 * @param <S>
 *            supplier type
 */
public interface IChainSupplier<S> extends IChainable {
	/**
	 * This method is called by the containing chain to get the current value of
	 * this supplier
	 * 
	 * @return the current value for this IChainSupplier node
	 */
	S getValue();

	/**
	 * This method is called by the containing chain to construct the call hierarchy
	 * for the control flow execution
	 * 
	 * @param Node
	 *            the node to which you simulate the data transferring
	 * @return whether or not the transferring was successful
	 */
	boolean simulateOutput(IChainable Node);
}
