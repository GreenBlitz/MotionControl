package org.greenblitz.motion.pathfinder;

import jaci.pathfinder.Trajectory;
import jaci.pathfinder.followers.EncoderFollower;
import org.greenblitz.motion.base.IChassis;
import org.greenblitz.motion.base.IEncoder;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class PathFollower {

    private EncoderFollower m_rightFollower;
    private EncoderFollower m_leftFollower;

    private EncoderConfig m_leftConfiguration;
    private EncoderConfig m_rightConfiguration;

    private long m_period;
    private double m_wheelDiameter;
    private IChassis m_chassis;

    private boolean m_isActive = false;
    private PathFollowerTask m_currentFollowerTask;

    private Timer m_timer = new Timer();

    private class PathFollowerTask extends TimerTask {

        IEncoder left = m_chassis.getLeftEncoder();
        IEncoder right = m_chassis.getRightEncoder();
        {
            System.out.println("created follower task");
        }
        @Override
        public void run() {
            m_chassis.tankDrive(m_leftFollower.calculate(left.getTicks()), m_rightFollower.calculate(right.getTicks()));
        }
    }

    public static class EncoderConfig {
        final int ticksPerRotation;

        final double kP, kI, kD, kV, kA;

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

    public PathFollower(IChassis chassis, double wheelDiameter, long period, Trajectory left, Trajectory right, EncoderConfig leftConfig, EncoderConfig rightConfig) {
        m_chassis = chassis;
        m_period = period;
        m_wheelDiameter = wheelDiameter;

        m_leftFollower = new EncoderFollower(left);
        m_rightFollower = new EncoderFollower(right);

        configure(leftConfig, rightConfig);
    }

    public void configure(EncoderConfig leftConfig, EncoderConfig rightConfig) {
        m_leftConfiguration = leftConfig;
        m_rightConfiguration = rightConfig;

        m_leftFollower.configureEncoder(m_chassis.getLeftEncoder().getTicks(),
                leftConfig.ticksPerRotation,
                m_wheelDiameter);

        m_leftFollower.configurePIDVA(leftConfig.kV, leftConfig.kI, leftConfig.kD,
                leftConfig.kV, leftConfig.kA);

        m_rightFollower.configureEncoder(m_chassis.getRightEncoder().getTicks(),
                rightConfig.ticksPerRotation,
                m_wheelDiameter);

        m_rightFollower.configurePIDVA(rightConfig.kV, rightConfig.kI, rightConfig.kD,
                rightConfig.kV, rightConfig.kA);
    }

    public void start() {
        if (isActive()) return;

        m_isActive = true;
        reset();
        m_currentFollowerTask = new PathFollowerTask();
        m_timer.schedule(m_currentFollowerTask, 0, m_period);
    }

    private boolean isActive() {
        return m_isActive;
    }

    public void stop() {
        m_currentFollowerTask.cancel();
        m_isActive = false;
    }

    public boolean isFinished() {
        return m_leftFollower.isFinished() && m_rightFollower.isFinished();
    }

    /**
     * Sets the encoder followers relative start position to current encoder tick
     */
    public void reset() {
        configure(m_leftConfiguration, m_rightConfiguration);
    }
}
