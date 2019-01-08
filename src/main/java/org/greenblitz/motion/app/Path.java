package org.greenblitz.motion.app;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import jaci.pathfinder.Trajectory;
import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Path {

    private List<Position> m_path;

    @SuppressWarnings("unchecked")
    public Path(List<Position> path) {
        m_path = path;
    }

    public Path(Position... points){
        m_path = Arrays.asList(points);
    }

    public void interpolatePoints(int samples) {
        List<Position> newPath = new ArrayList<>();
        if (m_path.size() == 0){
            return;
        }
        newPath.add(m_path.get(0));
        for (int i = 0; i < m_path.size() - 1; i++) {
            Position first = m_path.get(i);
            Position last = m_path.get(i + 1);
            boolean eqX = false;
            if (Point.isFuzzyEqual(first.getX(), last.getX(), 10E-4) &&
                    Point.isFuzzyEqual(first.getY(), last.getY(), 10E-4))
                continue;
            if (Point.isFuzzyEqual(first.getX(), last.getX(), 10E-4)) {
                first.rotate(Math.PI / 2.0);
                last.rotate(Math.PI / 2.0);
                eqX = true;
            }
            double x1 = first.getX();
            double x2 = last.getX();
            double y1 = first.getY();
            double y2 = last.getY();
            double v1 = Math.tan(first.getAngle());
            double v2 = Math.tan(last.getAngle());

            double denominator = Math.pow(x1 - x2, 3);

            // Matrix magic gives this;
            double a = (v1 * x1 - v1 * x2 + v2 * x1 - v2 * x2 - 2 * y1 + 2 * y2)
                    / denominator;
            double b = (-v1 * x1 * x1 - v1 * x1 * x2 + 2 * v1 * x2 * x2 - 2 * v2 * x1 * x1 + v2 * x1 * x2
                    + v2 * x2 * x2 + 3 * x1 * y1 - 3 * x1 * y2 + 3 * x2 * y1 - 3 * x2 * y2)
                    / denominator;
            double c = (2 * v1 * x1 * x1 * x2 - v1 * x1 * x2 * x2 - v1 * Math.pow(x2, 3) + v2 * Math.pow(x1, 3)
                    + v2 * x1 * x1 * x2 - 2 * v2 * x1 * x2 * x2 - 6 * x1 * x2 * y1 + 6 * x1 * x2 * y2)
                    / denominator;
            double d = (-v1 * x1 * x1 * x2 * x2 + v1 * x1 * Math.pow(x2, 3) - v2 * Math.pow(x1, 3) * x2 + v2 * x1 * x1 * x2 * x2
                    + Math.pow(x1, 3) * y2 - 3 * x1 * x1 * x2 * y2 + 3 * x1 * x2 * x2 * y1 - Math.pow(x2, 3) * y1)
                    / denominator;

            for (double j = 1; j <= samples; j++){
                double section = j / samples;
                double currentX = x1 + (x2 - x1)*section;
                Position newPoint = new Position(currentX,
                        a*Math.pow(currentX, 3) + b*Math.pow(currentX, 2) + c*currentX + d,
                        Math.atan(3*a*Math.pow(currentX, 2) + 2*b*currentX + c));
                if (eqX)
                    newPoint.rotate(-Math.PI / 2);
                newPath.add(newPoint);
            }
        }
        m_path = newPath;
    }

    public void interpolateAngles() {
        for (int i = 1; i < m_path.size() - 1; i++) {
            m_path.get(i).setAngle(Math.atan2(
                    m_path.get(i + 1).getY() - m_path.get(i - 1).getY(),
                    m_path.get(i + 1).getX() - m_path.get(i - 1).getX()
            ));
        }
    }

    public void interpolate(int samples) {
        interpolateAngles();
        interpolatePoints(samples);
    }

    public static Path pathfinderPathToGBPath(Trajectory traj) {
        ArrayList<Position> ret = new ArrayList<>();
        for (Trajectory.Segment seg : traj.segments) {
            ret.add(new Position(seg.x, seg.y, seg.heading));
        }
        return new Path(ret);
    }

    /**
     *
     * @param robot
     * @param radius
     * @param segStart
     * @param segEnd
     * @return
     */
    public static double[] intersections(Point robot, double radius, Point segStart, Point segEnd) {
        Point segment = Point.subtract(segEnd, segStart);
        Point robToSeg = Point.subtract(segStart, robot);

        //squared equation a*t^2 + b*t + c = 0
        double a = Point.normSquared(segment),
                b = 2 * Point.dotProduct(robToSeg, segment),
                c = Point.normSquared(robToSeg) - radius * radius;
        double discriminant = b * b - 4 * a * c;

        if (discriminant < 0) return null;

        double sqrtDis = Math.sqrt(discriminant);
        return new double[]{(-b + sqrtDis) / (2 * a), (-b - sqrtDis) / (2 * a)};
    }

    public Point getGoalPoint(Point robotLoc, double lookAhead, double epsilon) {
        if (Point.distSqared(m_path.get(m_path.size() - 1), robotLoc) <= epsilon*epsilon) {
            return null;
        }
        if (Point.distSqared(m_path.get(m_path.size() - 1), robotLoc) <= lookAhead*lookAhead)
            return m_path.get(m_path.size() - 1);
        Point closest = m_path.get(m_path.size() - 1);
        double[] ptlInt; //potential intersections
        for (int ind = m_path.size() - 2; ind >= 0; ind--) {
            if (Point.distSqared(m_path.get(ind), robotLoc) < Point.distSqared(closest, robotLoc))
                closest = m_path.get(ind);
            ptlInt = intersections(robotLoc, lookAhead, m_path.get(ind), m_path.get(ind + 1));
            if (ptlInt == null)
                continue;
            if (ptlInt[0] >= 0 && ptlInt[0] <= 1)
                return Point.weightedAvg(m_path.get(ind), m_path.get(ind + 1), ptlInt[0]);
            if (ptlInt[1] >= 0 && ptlInt[1] <= 1)
                return Point.weightedAvg(m_path.get(ind), m_path.get(ind + 1), ptlInt[1]);
        }
        return closest;
    }

    public Point getLast() {
        return m_path.get(m_path.size() - 1);
    }

    public List<Position> getPath(){
        return m_path;
    }

}
