package APPC;

/**
 *
 * Thrown when a path has ended
 */
public class EndOfPathException extends ArrayIndexOutOfBoundsException{
    /**
     * @since 1.0
     */
    private static final long serialVersionUID = 1L;

    public EndOfPathException() {
    }

    public EndOfPathException(int index) {
        super(index);
    }

    public EndOfPathException(String s) {
        super(s);
    }
}
