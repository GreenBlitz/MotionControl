package org.greenblitz.motion.tolerance;

public interface ITolerance {
    boolean onTarget(double goal, double current);
}
