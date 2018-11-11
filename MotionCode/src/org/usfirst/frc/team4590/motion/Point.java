package org.usfirst.frc.team4590.motion;

public class Point {
	
	protected double x;
	protected double y;
	
	public Point(double x, double y) {
		this.setX(x);
		this.setY(y);
	}
	
	public Point clone() {
		return new Point(x, y);
	}
	
	public double[] get(){
		return new double[] {x, y};
	}
	
	public void set(double x, double y){
		setX(x);
		setY(y);
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
