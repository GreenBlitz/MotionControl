package base;

import java.util.HashSet;
import java.util.Set;

public class Printer {
	private final Set<Class<?>> printable = new HashSet<Class<?>>();

	public enum PrintType {
		OUT, ERR
	}

	public void registerPrintable(Class<?> cls) {
		printable.add(cls);
	}

	public void removePrintable(Class<?> cls) {
		if (contains(cls))
			printable.remove(cls);
	}

	public void print(Class<?> cls, PrintType t, Object o) {
		if (contains(cls)) {
			String print = o.toString();
			if (t == PrintType.OUT) {
				System.out.println("\r\n----------   " + cls.getName() + "   ----------");
				System.out.print(print.endsWith(System.lineSeparator()) ? print : print + System.lineSeparator());
				System.out.println("----------   " + cls.getName() + "   ----------");
			} else {
				System.err.println("\r\n----------   " + cls.getName() + "   ----------");
				System.err.print(print.endsWith(System.lineSeparator()) ? print : print + System.lineSeparator());
				System.err.println("----------   " + cls.getName() + "   ----------");
			}
		}
	}

	public void println(Class<?> cls, PrintType t, Object o) {
		print(cls, t, o + System.lineSeparator());
	}

	public void printf(Class<?> cls, PrintType t, String s, Object... o) {
		print(cls, t, String.format(s, o));
	}

	public void warn(Class<?> cls, PrintType t, String sep, Object s) {
		print(cls, t, "WARNING" + sep + s);
	}

	public void warnln(Class<?> cls, PrintType t, String sep, Object s) {
		println(cls, t, "WARNING" + sep + s + System.lineSeparator());
	}

	public void warnf(Class<?> cls, PrintType t, String sep, String s, Object... o) {
		printf(cls, t, "WARNING" + sep + s, o);
	}

	public void warn(Class<?> cls, PrintType t, Object o) {
		warn(cls, t, " ", o);
	}

	public void warnln(Class<?> cls, PrintType t, Object o) {
		warnln(cls, t, " ", o);
	}

	public void warnf(Class<?> cls, PrintType t, String s, Object... o) {
		warnf(cls, t, " ", s, o);
	}

	public void print(Class<?> cls, Object o) {
		print(cls, PrintType.OUT, o);
	}

	public void println(Class<?> cls, Object o) {
		println(cls, PrintType.OUT, o);
	}

	public void printf(Class<?> cls, String s, Object... o) {
		printf(cls, PrintType.OUT, s, o);
	}

	public void warn(Class<?> cls, Object o) {
		warn(cls, PrintType.OUT, o);
	}

	public void warnln(Class<?> cls, Object o) {
		warnln(cls, PrintType.OUT, o);
	}

	public void warnf(Class<?> cls, String s, Object... o) {
		warnf(cls, PrintType.OUT, s, o);
	}

	public boolean contains(Class<?> cls) {
		return printable.contains(cls);
	}
}
