package APPC;

import java.util.ArrayList;

public class classForTesting {
	public static Path sideways(double length) {
		return sideways(length, 0.01);
	}
	public static Path sideways(double length, double metersPerPoint) {
		ArrayList<Point2D> path = new ArrayList<Point2D>();
		for(double i=metersPerPoint; i<length; i+=metersPerPoint) path.add(new Point2D(i, 0, 0));
		path.add(new Point2D(length, 0, 0));
		return new Path(path);
	}
	
	public static Path forwards(double length) {
		return forwards(length, 0.01);
	}
	public static Path forwards(double length, double metersPerPoint) {
		ArrayList<Point2D> path = new ArrayList<Point2D>();
		for(double i=metersPerPoint; i<length; i+=metersPerPoint) path.add(new Point2D(0, i, 0));
		path.add(new Point2D(0, length, 0));
		return new Path(path);
	}
	
	public static Path pathToPoint(Point2D point) {
		return pathToPoint(point, 0.01);
	}
	public static Path pathToPoint(Point2D point, double metersPerPoint) {
		double ratio = point.getY()/point.getX();
		
		double dy = metersPerPoint/Math.sqrt(Math.pow(ratio, 2)+1);
		double dx = ratio*dy;
		
		ArrayList<Point2D> path = new ArrayList<Point2D>();
		for(int i=1; i<point.getX()/dx; i++) path.add(new Point2D(dx*i, dy*i, 0));
		path.add(point);
		return new Path(path);		
	}
}
