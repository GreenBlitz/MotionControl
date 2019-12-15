package org.greenblitz.motion.profiling;

@FunctionalInterface
public interface AccelerationInterpolator {

    double getRealMaxAccel(double currentVelocity, double maximumAsymptoticVelocity, double maximumInitialAccel);

}
