package org.greenblitz.motion.base;

import java.util.ArrayList;

public class Path {

    private ArrayList<Point> m_path;

    public Path(ArrayList<Point> path){
        m_path = (ArrayList<Point>)path.clone();
    }

    private double[] intersection(Point robot, double radius, Point segStart, Point segEnd){
        Point segment = Point.sub(segEnd, segStart);
        Point robToSeg = Point.sub(segStart, robot);

        //squared equation a*t^2 + b*t + c = 0
        double a = Point.dot(segment, segment),
                b = 2*Point.dot(robToSeg, segment),
                c = Point.dot(robToSeg, robToSeg) - radius*radius;
        double discriminant = b*b - 4*a*c;

        if(discriminant < 0) return null;

        double sqrtDis = Math.sqrt(discriminant);
        return new double[]{(-b + sqrtDis)/(2*a),  (-b - sqrtDis)/(2*a)};
    }

    public Point intersection(Point robotLoc, double lookAhead){
        if(Point.distSqared(m_path.get(m_path.size()-1), robotLoc) <= lookAhead) return m_path.get(m_path.size()-1);
        double[] potInt;
        for(int ind = m_path.size()-2; ind>=0; ind--){
            potInt = intersection(robotLoc, lookAhead, m_path.get(ind), m_path.get(ind+1));
            if(potInt == null) continue;
            if(potInt[0] >= 0 && potInt[0] <= 1) return Point.weightedAvg(m_path.get(ind), m_path.get(ind + 1), potInt[0]);
            if(potInt[1] >= 0 && potInt[1] <= 1) return Point.weightedAvg(m_path.get(ind), m_path.get(ind + 1), potInt[1]);
        }
        return intersection(robotLoc, 2*lookAhead);
    }


}
