package org.greenblitz.motion;

public class PathfinderException extends Exception {

    public PathfinderException() {
        super();
    }

    public PathfinderException(String message) {
        super(message);
    }

    public PathfinderException(String message, Throwable cause) {
        super(message, cause);
    }

    public PathfinderException(Throwable cause) {
        super(cause);
    }

    protected PathfinderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
