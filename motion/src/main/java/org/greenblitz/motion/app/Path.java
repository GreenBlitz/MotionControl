package org.greenblitz.motion.app;

import jaci.pathfinder.Trajectory;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.greenblitz.debug.RemoteCSVTarget;
import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Position;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * a list of point / segments for the robot to follow
 *
 * @author Udi    ~ MudiAtalon
 * @author Alexey ~ savioor
 */
public class Path implements Iterable<Position> {

    private List<Position> m_path;

    private static final double HALF_PI = Math.PI / 2;

    public Path(List<Position> path) {
        m_path = new ArrayList<>();
        m_path.addAll(path);
    }

    public Path(Position... points) {
        m_path = Arrays.asList(points);
    }

    /**
     * Given a set op points (the current path) it will add point between given points to complete a path.
     * This is done using cubic splines (and thus the angle of the point matters).
     *
     * @param samples The number of new point to add between each old pair
     */
    public void interpolatePoints(int samples) {
        List<Position> newPath = new ArrayList<>();
        if (m_path.size() == 0) {
            return;
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

            if (samples == 999)
                System.out.println(a + " " + b + " " + c + " " + d);

            for (double j = 1; j <= samples; j++) {
                double section = j / samples;
                double currentX = x1 + (x2 - x1) * section;
                Position newPoint = new Position(currentX,
                        a * Math.pow(currentX, 3) + b * Math.pow(currentX, 2) + c * currentX + d,
                        Math.atan(3 * a * Math.pow(currentX, 2) + 2 * b * currentX + c));
                newPoint.rotate(-addAngle);
                newPoint.changeAngleBy(-addAngle);
                if (samples == 999)
                    System.out.println(newPoint.mathToFrcCoords());
                newPath.add(newPoint.mathToFrcCoords());
            }
        }
        m_path = newPath;
    }

    /**
     * Chooses reasonable angles for all points using the position of adjacent points.
     * Doesn't affect the angle of the first and last point.
     */
    public void interpolateAngles() {
        for (int i = 1; i < m_path.size() - 1; i++) {
            m_path.get(i).setAngle(Math.atan2(
                    m_path.get(i + 1).getY() - m_path.get(i - 1).getY(),
                    m_path.get(i + 1).getX() - m_path.get(i - 1).getX()
            ));
        }
    }

    /**
     * Calls interpolateAngles() and afterwards interpolatePoints()
     *
     * @param samples
     */
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
     * finds values representing potential intersections between a circle and a segment
     * 0 -> segStart
     * 1 -> segEnd
     * negative/greater than one -> not on the segment
     *
     * @param center   the circle center
     * @param radius   the circle radius
     * @param segStart the start of the segment
     * @param segEnd   the end of the segment
     * @return [value of minimum] iff there is no line-circle intersections
     * the intersection values [value of minimum, high value, low value] O.W.
     */
    public static double[] intersections(Point center, double radius, Point segStart, Point segEnd) {
        Point segment = Point.subtract(segEnd, segStart);
        Point robToSeg = Point.subtract(segStart, center);

        //squared equation a*t^2 + b*t + c = 0
        double a = Point.normSquared(segment),
                b = 2 * Point.dotProduct(robToSeg, segment),
                c = Point.normSquared(robToSeg) - radius * radius;
        double discriminant = b * b - 4 * a * c;
        if (discriminant < 0) return new double[]{-b / (2 * a)};

        double sqrtDis = Math.sqrt(discriminant);
        return new double[]{-b / (2 * a), (-b + sqrtDis) / (2 * a), (-b - sqrtDis) / (2 * a)};
    }

    /**
     * finds the minimum point (according to some distance function) on a segment
     *
     * @param segStart     start of segment
     * @param segEnd       end of segment
     * @param lineMinValue the minimum value of the line of the segment
     * @return minimum of segment
     */
    public static Point getSegmentMinimum(Point segStart, Point segEnd, double lineMinValue) {
        if (lineMinValue < 0)
            return segStart;
        if (lineMinValue > 1)
            return segEnd;
        return Point.weightedAvg(segStart, segEnd, lineMinValue);
    }

    /**
     * finds the goal point (the point to witch the robot drives) according to the APPC algorithm
     *
     * @param robotLoc  the robot location
     * @param lookAhead look ahead distance
     * @return the last intersection on the path with the look ahead circle iff such intersection exists
     * the closest point on the path O.W.
     */
    public Position getGoalPoint(Point robotLoc, double lookAhead) {
        if (Point.distSqared(m_path.get(m_path.size() - 1), robotLoc) <= lookAhead * lookAhead) {
            return m_path.get(m_path.size() - 1);
        }
        Position closest = m_path.get(m_path.size() - 1);
        double[] ptlInt; //potential intersections
        for (int ind = m_path.size() - 2; ind >= 0; ind--) {
            ptlInt = intersections(robotLoc, lookAhead, m_path.get(ind), m_path.get(ind + 1));
            if (Point.distSqared(getSegmentMinimum(m_path.get(ind), m_path.get(ind + 1), ptlInt[0]), robotLoc) < Point.distSqared(closest, robotLoc))
                closest = m_path.get(ind);
            if (ptlInt.length == 1)
                continue;
            if (ptlInt[1] >= 0 && ptlInt[1] <= 1) {
                return new Position(Point.weightedAvg(m_path.get(ind), m_path.get(ind + 1), ptlInt[1]), m_path.get(ind).getAngle());
            }
            if (ptlInt[2] >= 0 && ptlInt[2] <= 1)
                return new Position(Point.weightedAvg(m_path.get(ind), m_path.get(ind + 1), ptlInt[2]), m_path.get(ind).getAngle());
        }
        return closest;
    }

    public Point getLast() {
        return new Position(m_path.get(m_path.size() - 1));
    }

    public void sendToCSV(String fileName) {
        RemoteCSVTarget printer = RemoteCSVTarget.getTarget(fileName);
        for (Position p : m_path) {
            printer.report(p.getX(), p.getY());
        }
    }

    public void saveAsCSV(String fileName){
        try {
            CSVPrinter printer = CSVFormat.EXCEL.withHeader(
                    "x",
                    "y"
            ).print(new File(fileName), Charset.defaultCharset());

            for (Point p : m_path){
                printer.printRecord(p.getX(), p.getY());
            }

            printer.flush();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Deprecated
    public List<Position> getPath() {
        return m_path;
    }

    @Override
    public Iterator<Position> iterator() {
        return m_path.iterator();
    }

}
