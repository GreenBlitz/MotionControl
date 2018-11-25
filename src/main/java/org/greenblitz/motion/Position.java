package org.greenblitz.motion;

/**
 * Represent a position in 2D space (for example of a org.greenblitz.robot) that consists of x, y, and angle (heading, the direction the object faces)
 * @author Alexey
 *
 */
public class Position extends Point {

	/**
	 * The angle of this position.
	 * This representation is like in math:
	 * 1. In radians
	 * 2. Between 0 and 2*PI
	 * 3. 0 radians = facing positive x
	 * 4. Goes counter clockwise
	 */
	protected double angle;
	
	/**
	 * Changes an angle to an equivalent angle between 0 and 2*PI
	 * @param angle
	 * @return
	 */
	public static double normalizeAngle(double angle) {
		angle %= (2 * Math.PI);
		if (angle < 0)
			angle += 2 * Math.PI;
		return angle;
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param angle Will automatically normalize the angle
	 */
	public Position(double x, double y, double angle) {
		super(x, y);
		this.angle = normalizeAngle(angle);
	}

	/**
	 * Angle is set to 0
	 * @param x
	 * @param y
	 */
	public Position(double x, double y) {
		super(x, y);
		this.angle = 0;
	}
	
	/**
	 * @return A double array of the x and y and angle values in that order
	 */
	@Override
	public double[] get(){
		return new double[] {x, y, angle};
	}
	
	/**
	 *
	 * @param x
	 * @param y
	 * @param angle
	 */
	public void set(double x, double y, double angle){
		super.set(x, y);
		setAngle(angle);
	}
	
	/**
	 * Rotate the Position around itself.
	 * i.e. change the angle of this point by the parameter
	 * @param angle
	 */
	public void changeAngleBy(double angle){
		setAngle(getAngle() + angle);
	}
	
	/**
	 * Returns a new location with the same values
	 */
	@Override
	public Position clone() {
		return new Position(x, y, angle);
	}

	public double getAngle() {
		return angle;
	}

	/**
	 * Will automatically normalize the angle
	 * @param angle
	 */
	public void setAngle(double angle) {
		this.angle = normalizeAngle(angle);
	}

    @Override
    public String toString() {
        return "Position{" +
                "angle=" + angle +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
