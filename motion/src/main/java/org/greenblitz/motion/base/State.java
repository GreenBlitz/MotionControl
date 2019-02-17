package org.greenblitz.motion.base;

import java.util.Objects;

public class State extends Position {

    protected double linearVelocity /* meter/sec */, angularVelocity /* radians/sec */;
    protected double linearAccel /* meter/sec^2 */, angularAccel /* radians/sec^2 */;
    protected Vector2D velocity;

    public State(double x, double y, double angle, double linearVelocity, double angularVelocity) {
        super(x, y, angle);
        this.linearVelocity = linearVelocity;
        this.angularVelocity = angularVelocity;
        velocity = new Vector2D(linearVelocity*Math.sin(angle), linearVelocity*Math.cos(angle));
    }

    public State(double x, double y, double linearVelocity, double angularVelocity) {
        super(x, y);
        this.linearVelocity = linearVelocity;
        this.angularVelocity = angularVelocity;
        velocity = new Vector2D(0, linearVelocity);
    }

    public State(Point point, double angle, double linearVelocity, double angularVelocity) {
        this(point.x, point.y, angle, linearVelocity, angularVelocity);
    }

    public State(Point point, double linearVelocity, double angularVelocity) {
        this(point.x, point.y, linearVelocity, angularVelocity);
    }

    public State(double x, double y, double angle, double linearVelocity, double angularVelocity, double linearAccel, double angularAccel) {
        this(x, y, angle, linearVelocity, angularVelocity);
        this.linearAccel = linearAccel;
        this.angularAccel = angularAccel;
    }

    public State(double x, double y, double linearVelocity, double angularVelocity, double linearAccel, double angularAccel) {
        this(x, y, linearAccel, angularVelocity);
        this.linearAccel = linearAccel;
        this.angularAccel = angularAccel;
    }

    public State(Point point, double angle, double linearVelocity, double angularVelocity, double linearAccel, double angularAccel) {
        this(point, angle, linearVelocity, angularVelocity);
        this.linearAccel = linearAccel;
        this.angularAccel = angularAccel;
    }

    public State(Point point, double linearVelocity, double angularVelocity, double linearAccel, double angularAccel) {
        this(point, linearAccel, angularVelocity);
        this.linearVelocity = linearVelocity;
        this.angularVelocity = angularVelocity;
        this.linearAccel = linearAccel;
        this.angularAccel = angularAccel;
    }

    public State(double x, double y, double angle) {
        this(x, y, angle, 0,0, 0 ,0);
    }

    public State(double x, double y) {
        this(x, y, 0);
    }

    public State(Point point, double angle) {
        this(point.getX(), point.getY(), angle);
    }

    public State(Point point) {
        this(point, 0);
    }



    /**
     * Returns a new State with the same values
     */
    @Override
    public State clone() {
        return new State(x, y, angle, linearVelocity, angularVelocity, linearAccel, angularAccel);
    }

    @Override
    public State localizerToMathCoords(){
        return new State(-x,y,angle+Math.PI/2,
                linearVelocity, angularVelocity,
                linearAccel, angularAccel);
    }

    @Override
    public State mathToFrcCoords(){
        return new State(-x,y,angle-Math.PI/2,
                linearVelocity, angularVelocity,
                linearAccel, angularAccel);
    }

    @Override
    public String toString() {
        return "State{" +
                "linearVelocity=" + linearVelocity +
                ", angularVelocity=" + angularVelocity +
                ", linearAccel=" + linearAccel +
                ", angularAccel=" + angularAccel +
                ", angle=" + angle +
                ", x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        State state = (State) o;
        return Double.compare(state.linearVelocity, linearVelocity) == 0 &&
                Double.compare(state.angularVelocity, angularVelocity) == 0 &&
                Double.compare(state.linearAccel, linearAccel) == 0 &&
                Double.compare(state.angularAccel, angularAccel) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), linearVelocity, angularVelocity, linearAccel, angularAccel);
    }

    public double getLinearVelocity() {
        return linearVelocity;
    }

    public void setLinearVelocity(double linearVelocity) {
        this.velocity = this.velocity.scale(linearVelocity/this.linearVelocity);
        this.linearVelocity = linearVelocity;
    }

    public double getAngularVelocity() {
        return angularVelocity;
    }

    public void setAngularVelocity(double angularVelocity) { this.angularVelocity = angularVelocity; }

    public Vector2D getVelocity(){return velocity.clone();}

    public void setVelocity(Vector2D velocity){
        this.velocity = velocity.clone();
        this.linearVelocity = velocity.norm();
        this.angle = Math.atan2(velocity.y, velocity.x);
    }

    public double getLinearAccel() {
        return linearAccel;
    }

    public void setLinearAccel(double linearAccel) {
        this.linearAccel = linearAccel;
    }

    public double getAngularAccel() {
        return angularAccel;
    }

    public void setAngularAccel(double angularAccel) {
        this.angularAccel = angularAccel;
    }
}
