package gbmotion.base.link.smartdashboard;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import gbmotion.controlflow.IChainConsumer;
import gbmotion.controlflow.IChainable;
import gbmotion.util.Tuple;

public class SmartDashboardNumericConsumer<T extends Number> implements IChainConsumer<Tuple<String, T>, Boolean> {
	private boolean m_hasSimulatedInput = false;
	
	@Override
	public void finalizeSimulation() {
		m_hasSimulatedInput = false;
	}

	@Override
	public boolean isCustomConsumer() {
		return false;
	}

	@Override
	public Boolean processData(Tuple<String, T> value) {
		SmartDashboard.putNumber(value._1, ((Number) value._2).doubleValue());
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
