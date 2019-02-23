package org.greenblitz.motion.profiling;

/**
 * Represents a position of an actuator, for example the position of an elevator (height and velocity.
 */
public class ActuatorLocation {

    /**
     * The 'location' of the actuator in 1D space
     */
    protected double x;

    /**
     * The 'velocity' of the actuator at this location. essentially it's the derivative of the location at x.
     */
    protected double v;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ActuatorLocation that = (ActuatorLocation) o;

        if (Double.compare(that.x, x) != 0) return false;
        return Double.compare(that.v, v) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(v);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "ActuatorLocation{" +
                "x=" + x +
                ", v=" + v +
                '}';
    }


    public ActuatorLocation(double x, double v) {
        this.x = x;
        this.v = v;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getV() {
        return v;
    }

    public void setV(double v) {
        this.v = v;
    }
}
