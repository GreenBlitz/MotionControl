package org.greenblitz.motion.base;

public class MovingPosition extends Position {

    protected double velocity;

    public MovingPosition(double x, double y, double angle, double velocity) {
        super(x, y, angle);
        this.velocity = velocity;
    }

    public MovingPosition(double x, double y) {
        this(x, y, 0 , 0);
    }

    public MovingPosition(Point point, double angle) {
        this(point.getX(), point.getY(), angle, 0);
    }

    public MovingPosition(Point point) {
        this(point.getX(), point.getY(), 0, 0);
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    @Override
    public String toString() {
        return "MovingPosition{" +
                "velocity=" + velocity +
                ", angle=" + angle +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
