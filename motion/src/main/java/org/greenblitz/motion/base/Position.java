package org.greenblitz.motion.base;

import jaci.pathfinder.Waypoint;

/**
 * Represent a position in 2D space (for example of a org.greenblitz.example.robot) that consists of x, y, and angle (heading, the direction the object faces)
 *
 * @author Alexey
 */
public class Position extends Point {

    /**
     * The angle of this position.
     * This representation is like in math:
     * 1. In radians
     * 2. Between -PI and PI
     * 3. 0 radians = facing positive y (in FRC coords)
     * 4. Goes counter clockwise
     */
    protected double angle;

    /**
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
     *
     * @param x
     * @param y
     */
    public Position(double x, double y) {
        this(x, y, 0);
    }

    /**
     * @param point
     * @param angle
     */
    public Position(Point point, double angle) {
        this(point.x, point.y, angle);
    }

    /**
     * @param point
     */
    public Position(Point point) {
        this(point, 0);
    }

    @Override
    public Position weightedAvg(Point b, double bWeight) {
        return new Position((1 - bWeight) * x + bWeight * b.x, (1 - bWeight) * y + bWeight * b.y);
    }

    public static Waypoint toWaypoint(Position p){
        return new Waypoint(p.getX(), p.getY(), p.getAngle());
    }

    /**
     * Changes an angle to an equivalent angle between -PI and PI
     *
     * @param angle
     * @return
     */
    public static double normalizeAngle(double angle) {
        angle %= (2 * Math.PI);
        if (angle > Math.PI)
            angle -= 2 * Math.PI;
        if (angle <= -Math.PI)
            angle += 2 * Math.PI;
        return angle;
    }

    /**
     * @return A double array of the x and y and angle values in that order
     */
    @Override
    public double[] get() {
        return new double[]{x, y, angle};
    }

    /**
     * @param x
     * @param y
     * @param angle
     */
    public void set(double x, double y, double angle) {
        super.set(x, y);
        setAngle(angle);
    }

    /**
     * Rotate the Position around itself.
     * i.e. change the angle of this point by the parameter
     *
     * @param angle
     */
    public Position changeAngleBy(double angle) {
        setAngle(getAngle() + angle);
        return this;
    }

    /**
     * Calls changeAngleBy and afterwards rotate.
     * @param angle
     * @return
     */
    public Position rotateWithAngle(double angle){
        return (Position) changeAngleBy(angle).rotate(angle);
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
     *
     * @param angle
     */
    public void setAngle(double angle) {
        this.angle = normalizeAngle(angle);
    }

    @Override
    public Position localizerToMathCoords(){
        return new Position(-x,y,angle+Math.PI/2);
    }

    @Override
    public Position weaverToLocalizerCoords() {
        return new Position(-x, -y, 0); // TODO ask atsmon
    }

    @Override
    public Point mathToWeaverCoords() {
        return new Position(-y, x, 0); // TODO ask atsmon
    }

    @Override
    public Position mathToFrcCoords(){
        return new Position(-x,y,angle-Math.PI/2);
    }

    @Override
    public String toString() {
        return "Position{" +
                "x=" + x +
                ", y=" + y +
                ", angle=" + angle*180/Math.PI +
                '}';
    }
}
