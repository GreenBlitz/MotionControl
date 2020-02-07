package org.greenblitz.motion.profiling.kinematics;

import org.greenblitz.motion.base.Vector2D;

/**
 * @author alexey
 */
@FunctionalInterface
public interface IConverter {

    /**
     *
     * @param byLinAng A vector where the first element is linear velocity and the second is linear accel
     * @return the velocities for the left and right motor respectively that match the given linear velocity
     * and angular velocity
     */
    Vector2D convert(Vector2D byLinAng);

}
