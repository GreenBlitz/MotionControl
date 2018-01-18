package base.exceptions;

/**
 * Created by karlo on 25/12/2017.
 * This will be thrown when a method is not implemented
 */
public class NotImplementedException extends IllegalStateException {
    public NotImplementedException() {
    }

    public NotImplementedException(String s) {
        super(s);
    }

    public NotImplementedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotImplementedException(Throwable cause) {
        super(cause);
    }
}
