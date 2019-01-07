package org.greenblitz.motion.app;

import org.greenblitz.motion.base.Point;

import java.util.ArrayList;

public class Path {

    private ArrayList<Point> m_path;

    @SuppressWarnings("unchecked")
    public Path(ArrayList<Point> path) {
        m_path = (ArrayList<Point>) path.clone();
    }

    public static double[] intersections(Point robot, double radius, Point segStart, Point segEnd) {
        Point segment = Point.subtract(segEnd, segStart);
        Point robToSeg = Point.subtract(segStart, robot);

        //squared equation a*t^2 + b*t + c = 0
        double a = Point.dotProduct(segment, segment),
                b = 2 * Point.dotProduct(robToSeg, segment),
                c = Point.dotProduct(robToSeg, robToSeg) - radius * radius;
        double discriminant = b * b - 4 * a * c;

        if (discriminant < 0) return null;

        double sqrtDis = Math.sqrt(discriminant);
        return new double[]{(-b + sqrtDis) / (2 * a), (-b - sqrtDis) / (2 * a)};
    }

    public Point intersection(Point robotLoc, double lookAhead, double epsilon) {
        if (Point.distSqared(m_path.get(m_path.size() - 1), robotLoc) <= epsilon)
            return null;
        if (Point.distSqared(m_path.get(m_path.size() - 1), robotLoc) <= lookAhead)
            return m_path.get(m_path.size() - 1);
        Point closest = m_path.get(m_path.size() - 1);
        double[] potInt;//potential intersections
        for (int ind = m_path.size() - 2; ind >= 0; ind--) {
            if (Point.distSqared(m_path.get(ind), robotLoc) < Point.distSqared(closest, robotLoc))
                closest = m_path.get(ind);
            potInt = intersections(robotLoc, lookAhead, m_path.get(ind), m_path.get(ind + 1));
            if (potInt == null)
                continue;
            if (potInt[0] >= 0 && potInt[0] <= 1)
                return Point.weightedAvg(m_path.get(ind), m_path.get(ind + 1), potInt[0]);
            if (potInt[1] >= 0 && potInt[1] <= 1)
                return Point.weightedAvg(m_path.get(ind), m_path.get(ind + 1), potInt[1]);
        }
        return closest;
    }

}
