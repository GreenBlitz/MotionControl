package base.exceptions;

public class ClassNotConstructedException extends IllegalStateException {
	private static final long serialVersionUID = 4357420926888101207L;

	public ClassNotConstructedException() {
        super("Class didn't call construct() method");
    }

    public ClassNotConstructedException(String s) {
        super(s);
    }

    public ClassNotConstructedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClassNotConstructedException(Throwable cause) {
        super(cause);
    }
}
