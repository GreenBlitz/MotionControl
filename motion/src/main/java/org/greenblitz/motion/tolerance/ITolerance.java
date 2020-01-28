package org.greenblitz.motion.tolerance;

@FunctionalInterface
public interface ITolerance {
    boolean onTarget(double goal, double current);
}
