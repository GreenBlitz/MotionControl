package  controlflow.test;

import  controlflow.ChainFactory;
import  controlflow.IChain;
import  controlflow.ChainFactory.ChainAssembly;

public class MainTest {
	public static void main(String[] args) {
		ChainAssembly asm = ChainFactory.create(new Supplier1(3));
		
		asm.connection("con1").conversion(list ->  ((double)list.get(0) * (double)list.get(1)))
		.supplierFor("root")
		.namedSupplier(new Supplier1(6), "o3")
		.namedConsumer(new ConsumerSupplier1(), "alexey");
		
		asm.connection("con2")
		.supplierFor("alexey")
		.namedConsumer(new Consumer1(), "s2");
		IChain chain = asm.toChain();
		chain.run();
	}
}
