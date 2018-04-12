package gbmotion.controlflow;

/**
 * 
 * This interface should be implemented by classes which want to have a simple
 * implementation for transferring data between nodes
 * 
 * @author Memes
 *
 * @param <S>
 *            supplied type
 * @param <C>
 *            consumed type
 * 
 */
public interface IConnector<S, C> extends IChainIO<S, C, Boolean> {

	/**
	 * Don't worry about it - it's fine guys!
	 */
	default boolean isCustomConsumer() {
		return false;
	}

}
