package gbmotion.controlflow;

public interface IChainable {
	
	
	default boolean isNodeBusy() {
		return false;
	}
	
	void finalizeSimulation();
}
