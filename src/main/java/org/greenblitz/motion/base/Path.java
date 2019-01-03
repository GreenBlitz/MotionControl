package org.greenblitz.motion.base;

import java.util.ArrayList;

public class Path {

    private ArrayList<Point> m_path;

    public Path(ArrayList<Point> path){
        m_path = path;
    }

    private double[] findIntersection(Point robot, double radius, Point segStart, Point segEnd){
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

    
}
