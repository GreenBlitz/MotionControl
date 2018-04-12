package gbmotion.base;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import gbmotion.controlflow.IChainSupplier;
import gbmotion.controlflow.IChainable;

public class SamplingSupplier<K extends Enum<K>, C> implements IChainSupplier<Map<K, C>> {
	Supplier<C>[] m_suppliers;
	K[] m_inputs;

	public SamplingSupplier(Supplier<C>[] functions, K[] inputs) {
		m_suppliers = functions;
		m_inputs = inputs;
	}

	@Override
	public void finalizeSimulation() {}

	@Override
	public Map<K, C> getValue() {
		Map<K, C> ret = new HashMap<>();
		for (int i = 0; i < m_suppliers.length; i++)
			ret.put(m_inputs[i], m_suppliers[i].get());
		return ret;
	}

	@Override
	public boolean simulateOutput(IChainable Node) {
		try {
			for (Supplier<C> sup : m_suppliers)
				sup.get();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
