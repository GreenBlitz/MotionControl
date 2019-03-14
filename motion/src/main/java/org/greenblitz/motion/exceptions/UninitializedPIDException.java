package org.greenblitz.motion.exceptions;

import org.greenblitz.motion.pid.PIDController;

public class UninitializedPIDException extends IllegalStateException {
    public UninitializedPIDException() {
    }

    public UninitializedPIDException(String s) {
        super(s);
    }

    public UninitializedPIDException(String message, Throwable cause) {
        super(message, cause);
    }

    public UninitializedPIDException(Throwable cause) {
        super(cause);
    }

    public UninitializedPIDException(PIDController pid) {
        this("PID " + pid + " isn't initialized yet");
    }
}
