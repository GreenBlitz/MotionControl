package controlflow;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.Function;

public class ChainFactory {
	
	protected static final class FactoryChain implements IChain{

		
		private long m_lastCycleStart = 0;
		
		private List<FactoryChainOperation> m_operations;
		
		public FactoryChain(List<FactoryChainOperation> ops) {
			m_operations = ops;
		}

		@Override
		public void run() {
			m_lastCycleStart = System.currentTimeMillis();
			Stack<String> stackTrace = new Stack<String>();
			for (FactoryChainOperation op : m_operations){
				switch (op.m_type){
				case MOVE_VALUE:

					IChainSupplier<?> s = (IChainSupplier<?>) op.m_nodes.get(0);					
					IChainConsumer<?,?> c = (IChainConsumer<?,?>) op.m_nodes.get(1);

					try {
						c.processData(unsafeRun(s, stackTrace, c));
					} catch (Exception e){
						System.err.println("An Exception was thrown while running this factory chain");
						System.err.println("Here is the normal system stack trace\n-------------------------------------------------------------------------------------");
						e.printStackTrace();
						System.err.println("-------------------------------------------------------------------------------------");
						System.err.println("Here is the chain operation StackTrace:");
						System.err.println("-------------------------------------------------------------------------------------");
						
						for (int idx = 0;; idx++){
							StackTraceElement trace = e.getStackTrace()[idx];
							if (trace.getMethodName().equals("run") && trace.getClassName().equals(getClass().getName()) ){
								break;
							} 
							stackTrace.push("	at " + trace.toString());
						}
						while (!stackTrace.isEmpty()){
							System.err.println(stackTrace.pop());
						}
						System.err.println("-------------------------------------------------------------------------------------");
					}
					break;
				default:
					break;
				}
			}
		}
		
		
		/**
		 * tHiS cODe IS exTremly SAfE doNT wOrrY
		 */
		private <T> T unsafeRun(IChainSupplier<?> sup, Stack<String> stackTrace, IChainConsumer<?, ?> c){
			stackTrace.push("	at " + sup.getClass().getName() + ".getValue()");
			T value = (T) sup.getValue();
			stackTrace.push("	at " + c.getClass().getName() + ".processData()");
			return value;
		}

		@Override
		public boolean isCycleRunning() {
			return false;
		}

		@Override
		public long getLastCycleTime() {
			return m_lastCycleStart;
		}

		@Override
		public IChainable lookedNode() {
			return null;
		}

		@Override
		public List<IChainSupplier<?>> getPureSuppliers() {
			return null;
		}

		@Override
		public List<IChainConsumer<?, ?>> getPureConsumers() {
			return null;
		}

		@Override
		public List<IChainable> getAllElements() {
			return null;
		}

		@Override
		public IChain copy() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void forceTerminate() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	protected static class FactoryConnector<S, C> extends AbstractConnector<S, C>{

		private Function<List<S>, C> lambda;
		
		public FactoryConnector(int inputCount, Function<List<S>, C> lamb/*ToSlaughter*/) {
			super(inputCount);
			lambda = lamb;
		}
		
		public FactoryConnector(){
			super(1);
			lambda = list -> (C) list.get(0);
			
		}
		
		public C getValue(){
			return lambda.apply(m_supplied);
		}
		
	}
	

	@SuppressWarnings("all")
	public static final class ChainAssembly{
		
		public final class AssembledConnection{
			private List<FactoryNode> m_allSupplierNodes;
			private List<FactoryNode> m_allConsumerNodes;
			private Function<List<?>, ?> m_lambda;
			
			public AssembledConnection(){
				m_allSupplierNodes = new LinkedList<>();
				m_allConsumerNodes = new LinkedList<>();
			}
			
			public AssembledConnection conversion(Function<List<?>, ?> lambda){
				m_lambda = lambda;
				return this;
			}
			
			
			
			public AssembledConnection consumer(IChainConsumer<?, ?> consumer){ 
				FactoryNode node = new FactoryNode(consumer);
				m_allConsumerNodes.add(node); 
				m_nodes.add(node);
				return this; }
			
			public AssembledConnection supplier(IChainSupplier<?> supplier){ 
				FactoryNode node = new FactoryNode(supplier);
				m_allSupplierNodes.add(node);
				m_nodes.add(node);
				return this; }
			
			public AssembledConnection namedConsumer(IChainConsumer<?, ?> consumer, String name){ 
				FactoryNode node = new FactoryNode(consumer, name);
				m_allConsumerNodes.add(node); 
				m_nodes.add(node);
				return this; }
			
			public AssembledConnection namedSupplier(IChainSupplier<?> supplier, String name){ 
				FactoryNode node = new FactoryNode(supplier, name);
				m_allSupplierNodes.add(node); 
				m_nodes.add(node);
				return this; }
			
			public AssembledConnection consumerFor(String node){
				m_allConsumerNodes.add(node(node)); 
				return this; }
			
			public AssembledConnection supplierFor(String node){
				m_allSupplierNodes.add(node(node)); 
				return this; }
			
			public List<FactoryNode> getAllConnections(){ List<FactoryNode> list = new LinkedList<>();
				list.addAll(m_allConsumerNodes);
				list.addAll(m_allSupplierNodes);
				return list; }
			
			public List<FactoryNode> getAllConsumers(){	return m_allConsumerNodes; }
			
			public List<FactoryNode> getAllSuppliers(){ return m_allSupplierNodes; }
			
			public ChainAssembly assembly(){
				return ChainAssembly.this;
			}
		}
		
		public final class FactoryNode{
			private String m_name;
			private IChainable m_node;
	
			
			public FactoryNode(IChainable node){
				m_node = node;
			}
			
			public FactoryNode(IChainable node, String name){
				m_node = node;
				m_name = name;
			}
			
			public AssembledConnection connection(String name){
				return ChainAssembly.this.connection(name);
			}
			
		
		
		}
		
		private List<FactoryNode> m_nodes;
		private Map<String, AssembledConnection> m_conns;
		public ChainAssembly(IChainSupplier<?> input) {
			m_nodes = new LinkedList<>();
			m_nodes.add(new FactoryNode(input,"root"));
			m_conns = new HashMap<>();
		}
		
		public FactoryNode node(String name){
			for (FactoryNode cur : m_nodes){
				if (cur.m_name != null && cur.m_name.equals(name)){
					return cur;
				}
			}
			return null;
		}
		
		public FactoryNode node(int index){
			return m_nodes.get(index < 0 ? index + m_nodes.size() : index);
		}
		
		public ChainAssembly newNode(IChainable node){
			m_nodes.add(new FactoryNode(node));
			return this;
		}
		
		public ChainAssembly newNode(IChainable node, String name){
			m_nodes.add(new FactoryNode(node, name));
			return this;
		}
		
		public AssembledConnection connection(String name){
			if (!m_conns.containsKey(name)){
				AssembledConnection con = new AssembledConnection();
				m_conns.put(name, con);
			}
			return m_conns.get(name);
		}
		
		
		
		
		
		public FactoryChain toChain(){
			List<FactoryNode> pureInputs = new LinkedList<>();
			List<FactoryNode> pureOutputs = new LinkedList<>();
			List<FactoryNode> pureIO = new LinkedList<>();
			List<AssembledConnection> connections = new LinkedList<>();
			Map<FactoryNode, List<AssembledConnection>> mapOut = new HashMap<>();
			Map<FactoryNode, List<AssembledConnection>> mapIn = new HashMap<>();
			Map<AssembledConnection, IConnector<?, ?>> mapCon = new HashMap<>();
			
			for (Map.Entry<String,AssembledConnection> entry : m_conns.entrySet()){
				AssembledConnection con = entry.getValue();
				for (FactoryNode node : con.getAllSuppliers()){
					if (!mapIn.containsKey(node)){
						mapIn.put(node, new LinkedList<>());
					}
				    mapIn.get(node).add(con);

					
				}
				for (FactoryNode node : con.getAllConsumers()){
					if (!mapOut.containsKey(node)){
						mapOut.put(node, new LinkedList<>());
					}
					mapOut.get(node).add(con);
					
				}
				
				connections.add(con);
				mapCon.put(con, con.m_lambda == null ? new FactoryConnector() : new FactoryConnector(con.getAllSuppliers().size(), con.m_lambda));
		
			}
			
			for (FactoryNode node : m_nodes){
				System.out.println(node.m_name);
				if (!mapIn.containsKey(node)){
					mapIn.put(node, new LinkedList<>());
				}
				if (!mapOut.containsKey(node)){
					mapOut.put(node, new LinkedList<>());
				}
				
				int inCons = mapIn.get(node).size();
				int outCons = mapOut.get(node).size();
				System.out.println("aaa");
				if (inCons == 0 && outCons >= 1){
					pureOutputs.add(node);
				} else if (inCons >= 1 && outCons == 0){
					pureInputs.add(node);
				} else if (inCons >= 1 && outCons >= 1){
					pureIO.add(node);
				} else {
					System.out.println("!WARNING! Unconnected node was added to a chain factory - the node was from type '" + node.m_node.getClass().getName() + "'");
				}
			}
			List<FactoryChainOperation> ops = new LinkedList<>();
			

			
			while (!pureInputs.isEmpty() || !pureOutputs.isEmpty() || !pureIO.isEmpty() || !mapCon.isEmpty()){
				List<FactoryNode> cpyPureInputs = new LinkedList<>();
				cpyPureInputs.addAll(pureInputs);
				for (FactoryNode input : cpyPureInputs){
					for (AssembledConnection ass : mapIn.get(input)){
						IConnector<?, ?> con = mapCon.get(ass);
						
						IChainSupplier<?> sup = (IChainSupplier<?>) input.m_node;
						boolean inputStatus = sup.simulateOutput(con);
						boolean outputStatus = inputStatus && con.simulateInput(sup);
						if (outputStatus){
							pureInputs.remove(input);
							sup.finalizeSimulation();
							List<IChainable> l = new LinkedList<>();
							l.add(input.m_node);
							l.add(con);
								ops.add(new FactoryChainOperation(l, ChainOperationType.MOVE_VALUE));
						}
					}
				}
				for(AssembledConnection ass : connections){
					boolean shouldRemove = false;
					if (!mapCon.containsKey(ass)) continue;
					IConnector<?, ?> con = mapCon.get(ass);	
					if (con.hasSimulatedInput()){
						System.out.println("fuck");
						shouldRemove = true;
						for (FactoryNode consumer : ass.m_allConsumerNodes){
							IChainConsumer<?, ?> consu = (IChainConsumer<?, ?>) consumer.m_node;
							boolean inputStatus = con.simulateOutput(consu);
				
							boolean outputStatus = inputStatus && consu.simulateInput(con);
							if (outputStatus){
								List<IChainable> l = new LinkedList<>();
								l.add(con);
								l.add(consu);
								ops.add(new FactoryChainOperation(l, ChainOperationType.MOVE_VALUE));
								if (consu.hasSimulatedInput()){
									if (pureIO.contains(consumer)){
										pureInputs.add(consumer);
										pureIO.remove(consumer);
									} else {
										pureOutputs.remove(consumer);
										
									}
								}
							} else 
								shouldRemove = false;

						}
					} 
					if (shouldRemove){
						mapCon.remove(ass);
						con.finalizeSimulation();
					}
				}
			
			}
			return new FactoryChain(ops);
		}

		
		
		
	}
	
	public static enum ChainOperationType{
		MOVE_VALUE
	}
	
	public static final class FactoryChainOperation{
		private List<IChainable> m_nodes;
		private ChainOperationType m_type;
		private Object value;
		public FactoryChainOperation(List<IChainable> nodes, ChainOperationType type){
			m_nodes = nodes;
			m_type = type;
		}
		
		public void run(){
			
		}
		
	}
	
	
	public static final ChainAssembly create(IChainSupplier<?> sup) {
		return new ChainAssembly(sup);
	}
}
