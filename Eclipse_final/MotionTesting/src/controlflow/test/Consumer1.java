package controlflow.test;

import controlflow.IChainConsumer;
import controlflow.IChainable;

//This is a pure consumer like motors n' shit
public class Consumer1 implements IChainConsumer<Double, Boolean> {

	private double value;

	private boolean simInp = false;

	@Override
	public void finalizeSimulation() {
		simInp = false;
	}

	@Override
	public boolean isCustomConsumer() {
		return false;
	}

	@Override
	public Boolean processData(Double value) {
		this.value = value;
		System.out.println("my value is " + value);
		return true;
	}

	@Override
	public boolean simulateInput(IChainable node) {
		simInp = true;
		return true;
	}

	@Override
	public boolean hasSimulatedInput() {
		return simInp;
	}

}
