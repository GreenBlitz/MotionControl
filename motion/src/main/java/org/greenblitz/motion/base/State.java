package org.greenblitz.motion.base;

public class State extends Position {

    public final double speed;
    public final Vector2D velocity;

    public State(double x, double y, double angle, double speed) {
        super(x, y, angle);

        this.speed = speed;
        velocity = new Vector2D(speed * Math.sin(angle), speed * Math.cos(angle));
    }

    public State(Point p, double angle, double speed) {
        this(p.x, p.y, angle, speed);
    }

    public State(Position p, double speed) {
        this(p.x, p.y, p.angle, speed);
    }

    public State(double x, double y, Vector2D velocity) {
        super(x, y, Math.atan2(velocity.y, velocity.x));

        this.speed = Point.norm(velocity);
        this.velocity = velocity;
    }

    public State(Point p, Vector2D velocity) {
        this(p.x, p.y, velocity);
    }

}
