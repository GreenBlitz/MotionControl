package org.greenblitz.motion.pathing;

import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Position;
import org.greenblitz.motion.pathing.Path;

import java.util.ArrayList;
import java.util.List;

public class PolynomialInterpolator {

    private static final double HALF_PI = Math.PI / 2;

    /**
     * Given a set op points (the current m_path) it will add point between given points to complete a m_path.
     * This is done using cubic splines (and thus the angle of the point matters).
     *
     * @param samples The number of new point to add between each old pair
     */
    public static Path  interpolatePoints(Path original, int samples){
        List<Position> newPath = new ArrayList<>();
        List<Position> m_path = original.getPath();
        if (m_path.size() == 0) {
            return new Path();
        }
        newPath.add(m_path.get(0));

        double v1, v2, x1, x2, y1, y2, a, b, c, d, denominator, addAngle;

        for (int i = 0; i < m_path.size() - 1; i++) {
            Position first = m_path.get(i);
            Position last = m_path.get(i + 1);
            first = first.frcToMathCoords();
            last = last.frcToMathCoords();
            if (Point.fuzzyEquals(first, last, 10E-4))
                continue;
            addAngle = 0;
            if (Point.isFuzzyEqual(first.getX(), last.getX(), 10E-4)) {
                first.rotate(HALF_PI);
                first.changeAngleBy(HALF_PI);
                last.rotate(HALF_PI);
                last.changeAngleBy(HALF_PI);
                addAngle += HALF_PI;
            }

            double rotateAng = 0;

            if (Point.isFuzzyEqual(first.getAngle()%(HALF_PI), 0, 5E-2)
                    && !Point.isFuzzyEqual(first.getAngle()%(2*HALF_PI), 0, 5E-2)){
                rotateAng = HALF_PI - last.getAngle() + 1;
                if (Point.isFuzzyEqual(rotateAng, 0, 1E-3))
                    rotateAng = 1.1;
            } else if(Point.isFuzzyEqual(last.getAngle()%(HALF_PI), 0, 5E-2)
                    && !Point.isFuzzyEqual(last.getAngle()%(2*HALF_PI), 0, 5E-2)){
                rotateAng = HALF_PI - first.getAngle() + 1;
                if (Point.isFuzzyEqual(rotateAng, 0, 1E-3))
                    rotateAng = 1.1;
            }

            addAngle += rotateAng;
            first.rotate(rotateAng);
            first.changeAngleBy(rotateAng);
            last.rotate(rotateAng);
            last.changeAngleBy(rotateAng);

            x1 = first.getX();
            x2 = last.getX();
            y1 = first.getY();
            y2 = last.getY();
            v1 = Math.tan(first.getAngle());
            v2 = Math.tan(last.getAngle());

            denominator = Math.pow(x1 - x2, 3);

            // Dirug of the following matrix:
            // x1**3 x1**2 x1 1 | y1
            // x2**3 x2**2 x2 1 | y2
            // 3*x1**2 2*x1 1 0 | v1
            // 3*x2**2 2*x2 1 0 | v2
            // gives this:
            a = (v1 * x1 - v1 * x2 + v2 * x1 - v2 * x2 - 2 * y1 + 2 * y2)
                    / denominator;
            b = (-v1 * x1 * x1 - v1 * x1 * x2 + 2 * v1 * x2 * x2 - 2 * v2 * x1 * x1 + v2 * x1 * x2
                    + v2 * x2 * x2 + 3 * x1 * y1 - 3 * x1 * y2 + 3 * x2 * y1 - 3 * x2 * y2)
                    / denominator;
            c = (2 * v1 * x1 * x1 * x2 - v1 * x1 * x2 * x2 - v1 * Math.pow(x2, 3) + v2 * Math.pow(x1, 3)
                    + v2 * x1 * x1 * x2 - 2 * v2 * x1 * x2 * x2 - 6 * x1 * x2 * y1 + 6 * x1 * x2 * y2)
                    / denominator;
            d = (-v1 * x1 * x1 * x2 * x2 + v1 * x1 * Math.pow(x2, 3) - v2 * Math.pow(x1, 3) * x2 + v2 * x1 * x1 * x2 * x2
                    + Math.pow(x1, 3) * y2 - 3 * x1 * x1 * x2 * y2 + 3 * x1 * x2 * x2 * y1 - Math.pow(x2, 3) * y1)
                    / denominator;

            for (double j = 1; j <= samples; j++) {
                double section = j / samples;
                double currentX = x1 + (x2 - x1) * section;
                Position newPoint = new Position(currentX,
                        a * Math.pow(currentX, 3) + b * Math.pow(currentX, 2) + c * currentX + d,
                        Math.atan(3 * a * Math.pow(currentX, 2) + 2 * b * currentX + c));
                newPoint.rotate(-addAngle);
                newPoint.changeAngleBy(-addAngle);
                newPath.add(newPoint.mathToFrcCoords());
            }
        }
        return new Path(newPath);
    }

}
