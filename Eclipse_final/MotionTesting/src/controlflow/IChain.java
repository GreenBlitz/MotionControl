package controlflow;

import java.util.List;


/**
 * This interface represents the basic operations chain has - all chains should implement this interface 
 * @author Memes
 * 
 */
public interface IChain {

	/**
	 * This method runs a single loop of the control flow cycle
	 */
	void run();
	
	/**
	 * This method is used as a safety measure to potential parallel(through threads or networking) control flows
	 * @return whether or not this chain is currently in the middle of a cycle
	 */
	boolean isCycleRunning();
	
	/**
	 * 
	 * @return
	 */
	long getLastCycleTime();
	
	default boolean canStart() {
		return !isCycleRunning();
	}
	
	IChainable lookedNode();
	
	List<IChainSupplier<?>> getPureSuppliers();
	
	List<IChainConsumer<?,?>> getPureConsumers();
	
	List<IChainable> getAllElements();
	
	IChain copy();
	
	void forceTerminate();
	
	
}
