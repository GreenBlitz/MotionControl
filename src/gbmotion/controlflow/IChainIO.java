package gbmotion.controlflow;

/**
 *
 * This interface is used to create connector-style nodes in the Chaining
 * control flow
 * 
 * @author Memes
 *
 * @param <S>
 *            supplied type
 * @param <C>
 *            consumed type
 * @param <T>
 *            process status type
 */
public interface IChainIO<S, C, T> extends IChainConditional, IChainSupplier<S>, IChainConsumer<C, T> {

	default boolean hasRequirement() {
		return hasInput();
	}

	/**
	 * This method is called by hasRequirement by default and is used to determine
	 * whether or not this node is ready to go forward in the control flow
	 * 
	 * @return whether or not this Input-Output Connection has received data from
	 *         all its required inputs
	 */
	boolean hasInput();

}
