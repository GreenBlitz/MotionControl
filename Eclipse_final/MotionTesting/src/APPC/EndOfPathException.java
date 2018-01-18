package APPC;

/**
 *
 * Thrown when a path has ended
 */
public class EndOfPathException extends ArrayIndexOutOfBoundsException{
    public EndOfPathException() {
    }

    public EndOfPathException(int index) {
        super(index);
    }

    public EndOfPathException(String s) {
        super(s);
    }
}
