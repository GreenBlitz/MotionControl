package  controlflow.test;

import  controlflow.IChainConsumer;
import  controlflow.IChainSupplier;
import  controlflow.IChainable;

//This is a pure input
public class Supplier1 implements IChainSupplier<Double>{

	private double value;
	public Supplier1(double val){
		value = val;
	}
	
	
	@Override
	public void finalizeSimulation() {
		//Here you should reset any simulation variables
	}

	@Override
	public Double getValue() {
		System.out.println("value=" + value);
		return value;
	}

	@Override
	public boolean simulateOutput(IChainable Node) {
		//It can always output shit
		return true;
	}

}
