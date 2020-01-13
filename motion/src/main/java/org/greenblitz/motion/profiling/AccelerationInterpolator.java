package org.greenblitz.motion.profiling;


/**
 * A function the approximates the function Am(v). which is the function of maximum possible acceleration by
 * current velocity.
 * @author Alexey
 */
@FunctionalInterface
public interface AccelerationInterpolator {

    /**
     *
     * @param currentVelocity The current velocity in any consistent units (recommended m/s)
     * @param maximumAsymptoticVelocity The maximum velocity the robot can reach and keep, when applying the same force
     *                                  over a long period of time. Should be in consistent units (recommended m/s)
     * @param maximumInitialAccel The acceleration achieved from standstill in the first instant (about 0.1s) of applying some
     *                            constant force to the robot. Should be in consistent units (recommended m/s^2)
     * @return The current maximum acceleration which is possible to achieve
     */
    double getRealMaxAccel(double currentVelocity, double maximumAsymptoticVelocity, double maximumInitialAccel);

}
