package  controlflow.test;

import  controlflow.IChainConsumer;
import  controlflow.IChainSupplier;
import  controlflow.IChainable;

//This class is an output and input - like controller n' Shit
public class ConsumerSupplier1 implements IChainConsumer<Double, Boolean>, IChainSupplier<Double>{

	private double value; // The Output Value
	
	private boolean simInp = false; //Have I received Input in the simulation
	
	@Override
	public void finalizeSimulation() {
		simInp = false; //Resetting the simulation variable
	}

	@Override
	public boolean isCustomConsumer() {
		//you should return false
		return false;
	}

	@Override
	public Boolean processData(Double value) {
		//The Proccessing of the last Data
		System.out.println("value = " + value + "*2;");
		this.value = value * 2;
		return true;
	}

	@Override
	public boolean simulateInput(IChainable node) {
		simInp = true; //He got his simulated input so he should return true
		return true;
	}

	@Override
	public boolean hasSimulatedInput() {
		//For multiple Input controllers use a set and add each chainable node - return true when the set size is equal to the amount of inputs
		return simInp;
	}

	@Override
	public Double getValue() {
		//The Value which he passes onwards
		return value;
	}

	@Override
	public boolean simulateOutput(IChainable Node) {
		//It can output shit only if it received input
		return simInp;
	}

}
