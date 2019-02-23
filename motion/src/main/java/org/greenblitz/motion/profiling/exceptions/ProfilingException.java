package org.greenblitz.motion.profiling.exceptions;

/**
 * An error which is thrown when profiling fails for whatever reason
 */
public class ProfilingException extends RuntimeException {

    public ProfilingException() {
        super();
    }

    public ProfilingException(String message) {
        super(message);
    }

    public ProfilingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProfilingException(Throwable cause) {
        super(cause);
    }

    protected ProfilingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
