package org.greenblitz.motion.base;

import java.util.Objects;

public class PState extends Position {

    protected Vector2D velocity, acceleration;

    public PState(double x, double y, double angle, Vector2D velocity, Vector2D acceleration) {
        super(x, y, angle);
        this.velocity = velocity;
        this.acceleration = acceleration;
    }

    public PState(double x, double y, Vector2D velocity, Vector2D acceleration) {
        super(x, y);
        this.velocity = velocity;
        this.acceleration = acceleration;
    }

    public PState(Point point, double angle, Vector2D velocity, Vector2D acceleration) {
        super(point, angle);
        this.velocity = velocity;
        this.acceleration = acceleration;
    }

    public PState(Point point, Vector2D velocity, Vector2D acceleration) {
        super(point);
        this.velocity = velocity;
        this.acceleration = acceleration;
    }

    public PState(double x, double y, double angle, Vector2D velocity) {
        super(x, y, angle);
        this.velocity = velocity;
    }

    public PState(double x, double y, Vector2D velocity) {
        super(x, y);
        this.velocity = velocity;
    }

    public PState(Point point, double angle, Vector2D velocity) {
        super(point, angle);
        this.velocity = velocity;
    }

    public PState(Point point, Vector2D velocity) {
        super(point);
        this.velocity = velocity;
    }

    public PState(double x, double y, double angle) {
        super(x, y, angle);
    }

    public PState(double x, double y) {
        super(x, y);
    }

    public PState(Point point, double angle) {
        super(point, angle);
    }

    public PState(Point point) {
        super(point);
    }

    /**
     * Calls rotateWithAngle and also rotates the velocity and acceleration vectors.
     * @param ang
     * @return
     */
    public PState rotateEverything(double ang){
        velocity.rotate(ang);
        acceleration.rotate(ang);
        return (PState) rotateWithAngle(ang);
    }

    public Vector2D getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2D velocity) {
        this.velocity = velocity;
    }

    public Vector2D getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(Vector2D acceleration) {
        this.acceleration = acceleration;
    }

    /**
     * Returns a new PState with the same values
     */
    @Override
    public PState clone() {
        return new PState(x, y, angle, velocity.clone(), acceleration.clone());
    }

    @Override
    public PState localizerToMathCoords(){
        return new PState(-x,y,angle+Math.PI/2,
                new Vector2D(-velocity.x, velocity.y),
                new Vector2D(-acceleration.x, acceleration.y));
    }

    @Override
    public PState mathToFrcCoords(){
        return new PState(-x,y,angle-Math.PI/2,
                new Vector2D(-velocity.x, velocity.y),
                new Vector2D(-acceleration.x, acceleration.y));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PState pState = (PState) o;
        return velocity.equals(pState.velocity) &&
                acceleration.equals(pState.acceleration);
    }

    @Override
    public String toString() {
        return "PState{" +
                "velocity=" + velocity +
                ", acceleration=" + acceleration +
                ", angle=" + angle +
                ", x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), velocity, acceleration);
    }
}
