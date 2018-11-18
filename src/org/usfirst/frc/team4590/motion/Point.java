package org.usfirst.frc.team4590.motion;

/**
 * Represents a simple 2D point
 * @author Alexey
 *
 */
public class Point {
	
	/**
	 * the x coordinate
	 */
	protected double x;
	/**
	 * the y coordinate
	 */
	protected double y;
	
	/**
	 * 
	 * @param x
	 * @param y
	 */
	public Point(double x, double y) {
		this.setX(x);
		this.setY(y);
	}
	
	/**
	 * Returns a new point in the same location
	 */
	public Point clone() {
		return new Point(x, y);
	}
	
	/**
	 * 
	 * @return A double array of the x and y values in that order
	 */
	public double[] get(){
		return new double[] {x, y};
	}
	
	/**
	 * Set new coordinates to the point
	 * @param x
	 * @param y
	 */
	public void set(double x, double y){
		setX(x);
		setY(y);
	}
	
	/**
	 * Move the point by [x, y]
	 * @param x
	 * @param y
	 */
	public void translate(double x, double y){
		this.x += x;
		this.y += y;
	}
	
	/**
	 * Move by the x and y of the point
	 * @param p
	 */
	public void translate(Point p){
		translate(p.getX(), p.getY());
	}
	
	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}
	
}
