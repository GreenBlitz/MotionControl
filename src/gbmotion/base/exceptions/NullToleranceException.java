package gbmotion.base.exceptions;

public class NullToleranceException extends RuntimeException {
	private static final long serialVersionUID = 8518422509730034031L;

	public NullToleranceException() {
    }

    public NullToleranceException(String message) {
        super(message);
    }

    public NullToleranceException(String message, Throwable cause) {
        super(message, cause);
    }

    public NullToleranceException(Throwable cause) {
        super(cause);
    }

    public NullToleranceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
