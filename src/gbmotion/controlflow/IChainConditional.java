package gbmotion.controlflow;

public interface IChainConditional extends IChainable{
	
	/**
	 * This method is called by the containing chain to determine whether or not this node is ready to be used in the control flow
	 * @return whether or not this node's requirement to work is fulfilled
	 */
	boolean hasRequirement();
	
}
