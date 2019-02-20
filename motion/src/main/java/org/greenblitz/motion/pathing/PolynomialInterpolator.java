package org.greenblitz.motion.pathing;

import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Position;

import java.util.ArrayList;
import java.util.List;

public class PolynomialInterpolator {

    /**
     * Given a set op points (the current m_path) it will autocompleteAdd point between given points to complete a m_path.
     * This is done using cubic splines (and thus the angle of the point matters).
     *
     * @param samples The number of new point to autocompleteAdd between each old pair
     */
    public static Path<Position> interpolatePoints(Path<Position> original, int samples){
        List<Position> newPath = new ArrayList<>();
        List<Position> m_path = original.getPath();
        if (m_path.size() == 0) {
            return new Path();
        }
        newPath.add(m_path.get(0));

        double v1, v2, x2, y2, a, b, c, addAngle;

        for (int i = 0; i < m_path.size() - 1; i++) {
            Position first = m_path.get(i);
            Position last = m_path.get(i + 1);
            first = first.localizerToMathCoords();
            last = last.localizerToMathCoords();

            if (Point.fuzzyEquals(first, last, 10E-4))
                continue;

            Point deltaVec = Point.subtract(last, first).rotate(-first.getAngle());
            addAngle = -first.getAngle();
            v1 = 0;
            v2 = Math.tan(last.getAngle() - first.getAngle());
            if (v2 > 1000){
                v2 = -1;
                v1 = 1;
                deltaVec.rotate(Math.PI / 4);
                addAngle += Math.PI / 4;
            }
            x2 = deltaVec.getX();
            y2 = deltaVec.getY();

            // Dirug of the following matrix:
            // 0**3 0**2 0 1 | 0
            // x2**3 x2**2 x2 1 | y2
            // 3*0**2 2*0 1 0 | v1
            // 3*x2**2 2*x2 1 0 | v2
            // gives this:
            a = (-v1 * x2 - v2 * x2 + 2 * y2) / -Math.pow(x2, 3);

            b = (2 * v1 * x2 + v2 * x2 - 3 * y2) / -Math.pow(x2, 2);

            c = v1;
            
            // d = 0;

            for (double j = 1; j <= samples; j++) {
                double section = j / samples;
                double currentX = x2 * section;

                Position newPoint = new Position(currentX,
                        a * Math.pow(currentX, 3) + b * Math.pow(currentX, 2) + c * currentX,
                        Math.atan(3 * a * Math.pow(currentX, 2) + 2 * b * currentX + c));

                newPoint.rotate(-addAngle);
                newPoint.changeAngleBy(-addAngle);
                newPoint.translate(first);
                newPath.add(newPoint.mathToFrcCoords());
            }
        }
        return new Path<>(newPath);
    }

}
