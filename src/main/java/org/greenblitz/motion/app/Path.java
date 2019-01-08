package org.greenblitz.motion.app;

import jaci.pathfinder.Trajectory;
import javafx.geometry.Pos;
import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Path {

    private List<Point> m_path;

    @SuppressWarnings("unchecked")
    public Path(List<Point> path) {
        m_path = path;
    }

    public Path(Point... points){
        m_path = Arrays.asList(points);
    }

    /*public void interpolatePoints(int samples){
        List<Point> newPath = new ArrayList<>();
        for (int i = 0; i < m_path.size() - 1; i++){
            newPath.add(m_path.get(i));
            Point first = m_path.get(i);
            Point last = m_path.get(i + 1);
        }
    }

    public void interpolateAngles(){
        for (int i = 1; i < m_path.size() - 1; i++){
            m_path.get(i).setAngle(Math.atan2(
                    m_path.get(i+1).getY() - m_path.get(i-1).getY(),
                    m_path.get(i+1).getX() - m_path.get(i-1).getX()
            ));
        }
    }

    public void interpolate(int samples){
        interpolateAngles();
        interpolatePoints(samples);
    }

    public static Path pathfinderPathToGBPath(Trajectory traj){
        ArrayList<Point> ret = new ArrayList<>();
        for (Trajectory.Segment seg : traj.segments){
            ret.add(new Point(seg.x, seg.y ,seg.heading));
        }
        return new Path(ret);
    }*/

    protected static double[] intersections(Point robot, double radius, Point segStart, Point segEnd) {
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
        if (Point.distSqared(m_path.get(m_path.size() - 1), robotLoc) <= epsilon)
            return null;
        if (Point.distSqared(m_path.get(m_path.size() - 1), robotLoc) <= lookAhead)
            return m_path.get(m_path.size() - 1);
        Point closest = m_path.get(m_path.size() - 1);
        double[] potInt; //potential intersections
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

    public Point getLast(){
        return m_path.get(m_path.size()-1);
    }

}
