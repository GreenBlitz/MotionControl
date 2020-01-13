package org.greenblitz.motion.profiling;

import java.util.Objects;

/**
 * @author alexey
 */
public class ProfilingData {

    protected double maxLinearVelocity, maxLinearAccel, maxAngularVelocity, maxAngularAccel;

    /**
     *
     * @param maxLinearVelocity
     * @param maxLinearAccel
     * @param maxAngularVelocity
     * @param maxAngularAccel
     */
    public ProfilingData(double maxLinearVelocity, double maxLinearAccel, double maxAngularVelocity, double maxAngularAccel) {
        this.maxLinearVelocity = maxLinearVelocity;
        this.maxLinearAccel = maxLinearAccel;
        this.maxAngularVelocity = maxAngularVelocity;
        this.maxAngularAccel = maxAngularAccel;
    }

    public double getMaxLinearVelocity() {
        return maxLinearVelocity;
    }

    public double getMaxLinearAccel() {
        return maxLinearAccel;
    }

    public double getMaxAngularVelocity() {
        return maxAngularVelocity;
    }

    public double getMaxAngularAccel() {
        return maxAngularAccel;
    }

    @Override
    public String toString() {
        return "ProfilingData{" +
                "maxLinearVelocity=" + maxLinearVelocity +
                ", maxLinearAccel=" + maxLinearAccel +
                ", maxAngularVelocity=" + maxAngularVelocity +
                ", maxAngularAccel=" + maxAngularAccel +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProfilingData that = (ProfilingData) o;
        return Double.compare(that.maxLinearVelocity, maxLinearVelocity) == 0 &&
                Double.compare(that.maxLinearAccel, maxLinearAccel) == 0 &&
                Double.compare(that.maxAngularVelocity, maxAngularVelocity) == 0 &&
                Double.compare(that.maxAngularAccel, maxAngularAccel) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maxLinearVelocity, maxLinearAccel, maxAngularVelocity, maxAngularAccel);
    }
}
