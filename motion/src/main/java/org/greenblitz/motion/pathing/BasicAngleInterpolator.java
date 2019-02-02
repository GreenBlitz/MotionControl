package org.greenblitz.motion.pathing;

import org.greenblitz.motion.base.Position;
import org.greenblitz.motion.pathing.Path;

import java.util.List;

public class BasicAngleInterpolator {

    /**
     * Chooses reasonable angles for all points using the position of adjacent points.
     * Doesn't affect the angle of the first and last point.
     */
    public static Path<Position> interpolateAngles(Path<Position> original) {
        List<Position> m_path = original.getPath();
        for (int i = 1; i < m_path.size() - 1; i++) {
            m_path.get(i).setAngle(Math.atan2(
                    m_path.get(i + 1).getY() - m_path.get(i - 1).getY(),
                    m_path.get(i + 1).getX() - m_path.get(i - 1).getX()
            ));
        }
        return new Path<>(m_path);
    }

}
