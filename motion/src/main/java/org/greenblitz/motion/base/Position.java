package org.greenblitz.motion.base;

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

    public Position(double x, double y, double angle) {
        super(x, y);
        this.angle = normalizeAngle(angle);
    }

    public Position(double x, double y) {
        this(x, y, 0);
    }

    public Position(Point point, double angle) {
        this(point.x, point.y, angle);
    }

    public Position(Point point) {
        this(point, 0);
    }

    public Position weightedAvg(Position b, double bWeight) {
        return new Position((1 - bWeight) * x + bWeight * b.x, (1 - bWeight) * y + bWeight * b.y,
                (1 - bWeight) * angle + bWeight * b.getAngle());
    }

    public static double normalizeAngle(double angle) {
        angle %= (2 * Math.PI);
        if (angle > Math.PI)
            angle -= 2 * Math.PI;
        if (angle <= -Math.PI)
            angle += 2 * Math.PI;
        return angle;
    }

    @Override
    public double[] get() {
        return new double[]{x, y, angle};
    }

    public void set(double x, double y, double angle) {
        super.set(x, y);
        setAngle(angle);
    }

    public Position changeAngleBy(double angle) {
        setAngle(getAngle() + angle);
        return this;
    }

    public Position rotateWithAngle(double angle) {
        return (Position) changeAngleBy(angle).rotate(angle);
    }

    @Override
    public Position clone() {
        return new Position(x, y, angle);
    }

    @Override
    public Position negate(){
        return new Position(super.negate(), -angle);
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = normalizeAngle(angle);
    }

    public Position translate(Position p) {
        return new Position(x + p.x, y + p.y, angle + p.angle);
    }

    public Position moveBy(double linearV, double angularV, double t) {
        if (t == 0 || (linearV == 0 && angularV == 0))
            return clone();
        if (linearV == 0)
            return clone().changeAngleBy(angularV * t);
        if (angularV == 0)
            return new Position(clone().rotate(-angle).translate(0, linearV * t).rotate(angle), angle);

        double curvature = angularV / linearV;
        return clone().rotateWithAngle(-angle).translate(new Position((1 - Math.cos(angularV * t)) / curvature, Math.sin(angularV * t) / curvature, angularV * t)).rotateWithAngle(angle);
    }

    @Override
    public Position localizerToMathCoords() {
        return new Position(-x, y, angle + Math.PI / 2);
    }

    @Override
    public Position weaverToLocalizerCoords() {
        return new Position(-x, -y, angle - Math.PI / 2);
    }

    @Override
    public Point mathToWeaverCoords() {
        return new Position(x, -y, angle);
    }

    @Override
    public Position mathToFrcCoords() {
        return new Position(-x, y, angle - Math.PI / 2);
    }

    @Override
    public String toString() {
        return "Position{" +
                "x=" + x +
                ", y=" + y +
                ", angle=" + angle * 180 / Math.PI +
                '}';
    }
}
