package org.greenblitz.motion.profiling.exceptions;

/**
 * A Profiling error thrown when the distance to cover between to points is so short, the actuator completes
 * it before reaching the desired speed.
 */
public class NotEnoughAcceleratingSpace extends ProfilingException {
    public NotEnoughAcceleratingSpace() {
    }

    public NotEnoughAcceleratingSpace(String message) {
        super(message);
    }

    public NotEnoughAcceleratingSpace(String message, Throwable cause) {
        super(message, cause);
    }

    public NotEnoughAcceleratingSpace(Throwable cause) {
        super(cause);
    }

    public NotEnoughAcceleratingSpace(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
