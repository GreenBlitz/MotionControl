package org.greenblitz.motion.base;

import java.util.Objects;

public class State extends Position {

    protected Vector2D velocity, acceleration;

    public State(double x, double y, double angle, Vector2D velocity, Vector2D acceleration) {
        super(x, y, angle);
        this.velocity = velocity;
        this.acceleration = acceleration;
    }

    public State(double x, double y, Vector2D velocity, Vector2D acceleration) {
        super(x, y);
        this.velocity = velocity;
        this.acceleration = acceleration;
    }

    public State(Point point, double angle, Vector2D velocity, Vector2D acceleration) {
        super(point, angle);
        this.velocity = velocity;
        this.acceleration = acceleration;
    }

    public State(Point point, Vector2D velocity, Vector2D acceleration) {
        super(point);
        this.velocity = velocity;
        this.acceleration = acceleration;
    }

    public State(double x, double y, double angle, Vector2D velocity) {
        super(x, y, angle);
        this.velocity = velocity;
    }

    public State(double x, double y, Vector2D velocity) {
        super(x, y);
        this.velocity = velocity;
    }

    public State(Point point, double angle, Vector2D velocity) {
        super(point, angle);
        this.velocity = velocity;
    }

    public State(Point point, Vector2D velocity) {
        super(point);
        this.velocity = velocity;
    }

    public State(double x, double y, double angle) {
        super(x, y, angle);
    }

    public State(double x, double y) {
        super(x, y);
    }

    public State(Point point, double angle) {
        super(point, angle);
    }

    public State(Point point) {
        super(point);
    }

    /**
     * Calls rotateWithAngle and also rotates the velocity and acceleration vectors.
     * @param ang
     * @return
     */
    public State rotateEverything(double ang){
        velocity.rotate(ang);
        acceleration.rotate(ang);
        return (State) rotateWithAngle(ang);
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
     * Returns a new State with the same values
     */
    @Override
    public State clone() {
        return new State(x, y, angle, velocity.clone(), acceleration.clone());
    }

    @Override
    public State frcToMathCoords(){
        return new State(-x,y,angle+Math.PI/2,
                new Vector2D(-velocity.x, velocity.y),
                new Vector2D(-acceleration.x, acceleration.y));
    }

    @Override
    public State mathToFrcCoords(){
        return new State(-x,y,angle-Math.PI/2,
                new Vector2D(-velocity.x, velocity.y),
                new Vector2D(-acceleration.x, acceleration.y));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        State state = (State) o;
        return velocity.equals(state.velocity) &&
                acceleration.equals(state.acceleration);
    }

    @Override
    public String toString() {
        return "State{" +
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
