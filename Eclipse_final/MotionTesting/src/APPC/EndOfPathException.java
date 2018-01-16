package APPC;

/**
 * Created by karlo on 09/01/2018.
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
