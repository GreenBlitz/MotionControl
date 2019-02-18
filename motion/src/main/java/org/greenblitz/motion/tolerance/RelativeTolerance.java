package org.greenblitz.motion.tolerance;

public class RelativeTolerance implements ITolerance {
    private double m_percentTolerance;

    public RelativeTolerance(double percentTolerance) {
        if (percentTolerance <= 0 || 1 < percentTolerance)
            throw new IllegalArgumentException("tolerance has to be between 0 and 1");

        this.m_percentTolerance = percentTolerance;
    }

    @Override
    public boolean onTarget(double goal, double current) {
        return Math.abs(goal - current) < Math.abs(m_percentTolerance * goal);
    }
}
