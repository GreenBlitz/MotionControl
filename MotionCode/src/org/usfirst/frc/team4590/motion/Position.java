package org.usfirst.frc.team4590.motion;

public class Position extends Point {

	protected double angle;

	protected double normalizeAngle(double angle) {
		angle %= (2 * Math.PI);
		if (angle < 0)
			angle += 2 * Math.PI;
		return angle;
	}

	public Position(double x, double y, double angle) {
		super(x, y);
		this.angle = normalizeAngle(angle);
	}

	public Position(double x, double y) {
		super(x, y);
		this.angle = 0;
	}
	
	@Override
	public double[] get(){
		return new double[] {x, y, angle};
	}
	
	public void set(double x, double y, double angle){
		super.set(x, y);
		setAngle(angle);
	}
	
	@Override
	public Position clone() {
		return new Position(x, y, angle);
	}

	public double getAngle() {
		return angle;
	}

	public void setAngle(double angle) {
		this.angle = normalizeAngle(angle);
	}

}
