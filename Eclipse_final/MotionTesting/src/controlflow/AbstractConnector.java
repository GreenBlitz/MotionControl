package controlflow;

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author Memes
 *
 * 
 */
public class AbstractConnector<S,C> implements IConnector<C,S> {

	private final int m_inputCount;
	
	
	
	protected List<S> m_supplied = new LinkedList<S>();
	protected C m_consumed;
	
	private List<IChainable> m_simSupNodes = new LinkedList<>();
	private List<IChainable> m_simConNodes = new LinkedList<>();
	
	
	public AbstractConnector(int inputCount) {
		m_inputCount = inputCount;
	}
	
	
	@Override
	public boolean hasInput() {
		return m_inputCount == m_supplied.size();
	}

	@Override
	public Boolean processData(S value) {
		if (m_supplied.size() == m_inputCount){
			m_supplied.clear();
		}
		m_supplied.add(value);
		return true;
	}
	
	@Override
	public C getValue() {
		return m_consumed;
	}

	@Override
	public boolean simulateOutput(IChainable node) {
		return !m_simConNodes.contains(node) && m_simConNodes.add(node);
	}

	@Override
	public boolean simulateInput(IChainable node) {
		return !m_simSupNodes.contains(node) && m_simSupNodes.add(node);
	}

	@Override
	public boolean hasSimulatedInput() {
		return m_simSupNodes.size() == m_inputCount;
	}
	


	@Override
	public boolean hasRequirement() {
		return false;
	}


	@Override
	public void finalizeSimulation() {
		m_simSupNodes = null;
	}


	@Override
	public boolean isCustomConsumer() {
		return false;
	}
}
