package base;

import java.util.HashSet;
import java.util.Set;

public class Printer {
	private final Set<Class<?>> printable = new HashSet<Class<?>>();
	
	public void registerPrintable(Class<?> cls) {
		printable.add(cls);
	}
	
	public void removePrintable(Class<?> cls) {
		if (printable.contains(cls)) printable.remove(cls);
	}
	
	public void println(Class<?> cls, Object o) {
		if (printable.contains(cls)) System.out.println(o);
	}
	
	public void print(Class<?> cls, Object o) {
		if (printable.contains(cls)) System.out.print(o);
	}
	
	public void printf(Class<?> kys, String s, Object... o) {
		if (printable.contains(kys)) System.out.printf(s, o);
	}
	
	public void printerrf(Class<?> cls, String s, Object... o) {
		if (printable.contains(cls)) System.err.printf(s, o);
	}
}
