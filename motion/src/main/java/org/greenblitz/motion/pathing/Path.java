package org.greenblitz.motion.pathing;

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
public class Path<T extends Point> implements Iterable<T> {

    private List<T> m_path;

    public Path(List<T> path) {
        m_path = new ArrayList<>();
        m_path.addAll(path);
    }

    public Path(T... points) {
        m_path = Arrays.asList(points);
    }

    public static Path<Position> pathfinderPathToGBPath(Trajectory traj) {
        ArrayList<Position> ret = new ArrayList<>();
        for (Trajectory.Segment seg : traj.segments) {
            ret.add(new Position(seg.x, seg.y, seg.heading));
        }
        return new Path<>(ret);
    }

    public T getLast() {
        return m_path.get(m_path.size() - 1);
    }

    public T get(int ind){
        return m_path.get(ind);
    }

    public int size(){
        return m_path.size();
    }

    public void sendToCSV(String fileName) {
        RemoteCSVTarget printer = RemoteCSVTarget.getTarget(fileName);
        for (T p : m_path) {
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

    public List<T> getPath() {
        List<T> toSend = new ArrayList<>();
        for (T p:m_path){
            toSend.add((T)p.clone());
        }
        return toSend;
    }

    @Override
    public Iterator<T> iterator() {
        return m_path.iterator();
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
    public T getSegmentMinimum(T segStart, T segEnd, double lineMinValue) {
        if (lineMinValue < 0)
            return segStart;
        if (lineMinValue > 1)
            return segEnd;
        return (T)segStart.weightedAvg(segEnd, lineMinValue);
    }

}
