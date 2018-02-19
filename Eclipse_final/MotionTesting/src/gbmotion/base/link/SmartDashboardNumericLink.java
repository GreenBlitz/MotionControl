package gbmotion.base.link;

import gbmotion.controlflow.IChainConsumer;
import gbmotion.controlflow.IChainable;

public class SmartDashboardNumericLink<T extends Number> implements IChainConsumer<T, Boolean> {
	@Override
	public void finalizeSimulation() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isCustomConsumer() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Boolean processData(T value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean simulateInput(IChainable node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasSimulatedInput() {
		// TODO Auto-generated method stub
		return false;
	}

}
