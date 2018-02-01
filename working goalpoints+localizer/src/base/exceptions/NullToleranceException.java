package base.exceptions;

public class NullToleranceException extends RuntimeException {
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
