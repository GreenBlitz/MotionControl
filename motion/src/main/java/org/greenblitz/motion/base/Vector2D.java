package org.greenblitz.motion.base;

/**
 * This just represents a different thing.
 */
public class Vector2D extends Point {
    public Vector2D(double x, double y) {
        super(x, y);
    }

    @Override
    public String toString() {
        return "Vector2D{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    /**
     * Returns a new Vector2D in the same location
     */
    @Override
    public Vector2D clone() {
        return new Vector2D(x, y);
    }

    @Override
    public Vector2D scale(double scale){
        return new Vector2D(scale*x, scale*y);
    }
}
