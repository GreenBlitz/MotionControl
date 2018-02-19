package gbmotion.base.link;

import edu.wpi.first.wpilibj.NamedSendable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import gbmotion.controlflow.IChainConsumer;
import gbmotion.controlflow.IChainable;

public class SmartDashboardLink<T extends NamedSendable> implements IChainConsumer<T,Boolean>{

	boolean m_hasSimulatedInput = false;
	
	@Override
	public void finalizeSimulation() {
		m_hasSimulatedInput = false;
	}

	@Override
	public boolean isCustomConsumer() {
		return false;
	}

	@Override
	public Boolean processData(T value) {
		SmartDashboard.putData(value);
		return Boolean.TRUE;
	}
	
	@Override
	public boolean simulateInput(IChainable node) {
		m_hasSimulatedInput = true;
		return true;
	}

	@Override
	public boolean hasSimulatedInput() {
		return m_hasSimulatedInput;
	}
	
}
