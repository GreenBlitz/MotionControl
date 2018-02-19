package gbmotion.VelocityManager;

import gbmotion.controlflow.ChainFactory;
import gbmotion.controlflow.ChainFactory.ChainAssembly;
import gbmotion.controlflow.IChainConsumer;
import gbmotion.controlflow.IChainSupplier;

public class MotorManager {

	public static ChainAssembly build(IChainSupplier<Double> desiredVInput, IChainSupplier<Double> actualVInput,
			VoltageController voltageController, IChainConsumer<Double, Boolean> actuator, ChainAssembly factory,
			String suffix) {

		factory.newNode(desiredVInput, "desired " + suffix);
		factory.newNode(actualVInput, "velocity " + suffix);
		factory.newNode(voltageController, "voltage " + suffix);
		factory.newNode(actuator, "actuator " + suffix);

		factory.connection("desiredToVoltage " + suffix).supplierFor("desired " + suffix)
				.consumerFor("voltage " + suffix);

		factory.connection("actualToVoltage " + suffix).supplierFor("velocity " + suffix)
				.consumerFor("voltage " + suffix);

		factory.connection("voltageToPower " + suffix).conversion((voltage) -> (Double) voltage.get(0) / 12.0)
				.supplierFor("voltage").consumerFor("actuator");

		return factory;
	}

	public static ChainAssembly build(IChainSupplier<Double> desiredVInput, IChainSupplier<Double> actualVInput,
			VoltageController voltageController, IChainConsumer<Double, Boolean> actuator, String suffix) {
		ChainAssembly factory = ChainFactory.create(null);
		return build(desiredVInput, actualVInput, voltageController, actuator, factory, suffix);
	}

	public static ChainAssembly build(IChainSupplier<Double> desiredVInput, IChainSupplier<Double> actualVInput,
			VoltageController voltageController, IChainConsumer<Double, Boolean> actuator) {
		ChainAssembly factory = ChainFactory.create(null);
		return build(desiredVInput, actualVInput, voltageController, actuator, factory, " sub-chain #1");
	}

}
