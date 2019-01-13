package org.greenblitz.motion.pathfinder;

import jaci.pathfinder.Trajectory;
import jaci.pathfinder.followers.EncoderFollower;
import jaci.pathfinder.modifiers.TankModifier;

import java.util.Objects;

public class PathFollower {

    private EncoderFollower m_rightFollower;
    private EncoderFollower m_leftFollower;

    private EncoderConfig m_leftConfiguration;
    private EncoderConfig m_rightConfiguration;

    private double m_wheelDiameter;

    public static class EncoderConfig {
        public final int ticksPerRotation;
        public int initialTicks;
        public final double kP, kI, kD, kV, kA;

        public EncoderConfig(int ticksPerRotation, double kP, double kI, double kD, double kV, double kA) {
            this.ticksPerRotation = ticksPerRotation;
            this.kP = kP;
            this.kI = kI;
            this.kD = kD;
            this.kV = kV;
            this.kA = kA;
        }

        public EncoderConfig(int ticksPerRotation, double kP, double kD, double kV, double kA) {
            this(ticksPerRotation, kP, 0, kD, kV, kA);
        }

        public EncoderConfig(int ticksPerRotation, double kP, double kV) {
            this(ticksPerRotation, kP, 0, kV, 0);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            EncoderConfig that = (EncoderConfig) o;
            return ticksPerRotation == that.ticksPerRotation &&
                    Double.compare(that.kP, kP) == 0 &&
                    Double.compare(that.kI, kI) == 0 &&
                    Double.compare(that.kD, kD) == 0 &&
                    Double.compare(that.kV, kV) == 0 &&
                    Double.compare(that.kA, kA) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(ticksPerRotation, kP, kI, kD, kV, kA);
        }

        @Override
        public String toString() {
            return "EncoderConfig{" +
                    "ticksPerRotation=" + ticksPerRotation +
                    ", kP=" + kP +
                    ", kI=" + kI +
                    ", kD=" + kD +
                    ", kV=" + kV +
                    ", kA=" + kA +
                    '}';
        }
    }

    public PathFollower(Trajectory left, Trajectory right, EncoderConfig leftConfig, EncoderConfig rightConfig, double wheelDiameter) {
        m_leftFollower = new EncoderFollower(left);
        m_rightFollower = new EncoderFollower(right);

        m_wheelDiameter = wheelDiameter;

        configure(leftConfig, rightConfig);
    }


    public PathFollower(Trajectory stateSpaceTrajectory,
                        double wheelDiameter,
                        EncoderConfig leftConfig, EncoderConfig rightConfig) {
        TankModifier mod = new TankModifier(stateSpaceTrajectory);
        mod.modify(wheelDiameter);
        Trajectory leftTraj = mod.getLeftTrajectory();
        Trajectory rightTraj = mod.getRightTrajectory();

        m_leftFollower = new EncoderFollower(leftTraj);
        m_rightFollower = new EncoderFollower(rightTraj);

        m_wheelDiameter = wheelDiameter;

        configure(leftConfig, rightConfig);
    }

    public PathFollower(Trajectory stateSpaceTrajectory,
                        double wheelDiameter,
                        EncoderConfig config) {
        this(stateSpaceTrajectory, wheelDiameter, config, config);
    }


    public void configure(EncoderConfig leftConfig, EncoderConfig rightConfig) {
        m_leftConfiguration = leftConfig;
        m_rightConfiguration = rightConfig;

        m_leftFollower.configureEncoder(leftConfig.initialTicks,
                leftConfig.ticksPerRotation,
                m_wheelDiameter);

        m_leftFollower.configurePIDVA(leftConfig.kV, leftConfig.kI, leftConfig.kD,
                leftConfig.kV, leftConfig.kA);

        m_rightFollower.configureEncoder(rightConfig.initialTicks,
                rightConfig.ticksPerRotation,
                m_wheelDiameter);

        m_rightFollower.configurePIDVA(rightConfig.kV, rightConfig.kI, rightConfig.kD,
                rightConfig.kV, rightConfig.kA);
    }

    /**
     * The IO update function of the controller.
     * Call this in fixed frequency or pathfinder and I will kill you in your sleep.
     *
     * @param currentLeftTicks  the current tick count of the left encoder
     * @param currentRightTicks the current tick count of the right encoder
     * @return the values that should be passed to the motors (in tank drive)
     */
    public double[] update(int currentLeftTicks, int currentRightTicks) {
        var left = m_leftFollower.calculate(currentLeftTicks);
        var right = m_rightFollower.calculate(currentRightTicks);
        return new double[]{left, right};
    }

    /**
     * Sets the encoder followers relative start position to current encoder tick
     */
    public void reset(int left, int right) {
        m_leftFollower.reset();
        m_rightFollower.reset();
        m_leftConfiguration.initialTicks = left;
        m_rightConfiguration.initialTicks = right;
        configure(m_leftConfiguration, m_rightConfiguration);
    }

    public boolean isFinished() {
        return m_leftFollower.isFinished() && m_rightFollower.isFinished();
    }
}
