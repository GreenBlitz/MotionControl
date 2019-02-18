package org.greenblitz.motion.tolerance;

public class RelativeTolerance implements ITolerance {
    private double m_percentTolerance;

    public RelativeTolerance(double percentTolerance) {
        if (percentTolerance <= 0)
            throw new IllegalArgumentException("tolerance must be greater than 0");

        m_percentTolerance = percentTolerance;
    }

    @Override
    public boolean onTarget(double goal, double current) {
        return Math.abs(goal - current) < Math.abs(m_percentTolerance * goal);
    }
}
