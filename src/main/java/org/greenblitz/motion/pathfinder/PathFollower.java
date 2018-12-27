package org.greenblitz.motion.pathfinder;

import jaci.pathfinder.Trajectory;
import jaci.pathfinder.followers.EncoderFollower;
import org.greenblitz.motion.base.IChassis;
import org.greenblitz.motion.base.IEncoder;
import org.greenblitz.robot.RobotStats;

import java.util.Timer;
import java.util.TimerTask;

public class PathFollower {

    private EncoderFollower m_rightFollower;
    private EncoderFollower m_leftFollower;

    private long m_period;

    private double m_wheelDiameter;

    private boolean m_isActive = false;

    private IChassis m_chassis;

    private Timer m_timer = new Timer();

    private class PathFollowerTask extends TimerTask {

        IEncoder left = m_chassis.getLeftEncoder();
        IEncoder right = m_chassis.getRightEncoder();

        @Override
        public void run() {
            m_chassis.tankDrive(m_leftFollower.calculate(left.getTicks()), m_rightFollower.calculate(right.getTicks()));
        }
    }

    public static class EncoderConfig {
        final int ticksPerRotation;
        final int initialPosition;

        final double kP, kI, kD, kV, kA;

        public EncoderConfig(int ticksPerRotation, int initialPosition, double kP, double kI, double kD, double kV, double kA) {
            this.ticksPerRotation = ticksPerRotation;
            this.initialPosition = initialPosition;
            this.kP = kP;
            this.kI = kI;
            this.kD = kD;
            this.kV = kV;
            this.kA = kA;
        }

        public EncoderConfig(int ticksPerRotation, int initialPosition, double kP, double kD, double kV, double kA) {
            this(ticksPerRotation, initialPosition, kP, 0, kD, kV, kA);
        }
    }

    public PathFollower(IChassis chassis, double wheelDiameter, long period, Trajectory left, Trajectory right, EncoderConfig leftConfig, EncoderConfig rightConfig) {
        m_chassis = chassis;
        m_period = period;
        m_wheelDiameter = wheelDiameter;

        m_leftFollower = new EncoderFollower(left);
        m_rightFollower = new EncoderFollower(right);

        reconfigure(leftConfig, rightConfig);
    }

    public void reconfigure(EncoderConfig leftConfig, EncoderConfig rightConfig) {
        m_leftFollower.configureEncoder(leftConfig.initialPosition,
                leftConfig.ticksPerRotation,
                m_wheelDiameter);

        m_leftFollower.configurePIDVA(leftConfig.kV, 0.0, leftConfig.kD,
                leftConfig.kV, leftConfig.kA);

        m_rightFollower.configureEncoder(rightConfig.initialPosition,
                rightConfig.ticksPerRotation,
                m_wheelDiameter);

        m_rightFollower.configurePIDVA(rightConfig.kV, 0.0, rightConfig.kD,
                rightConfig.kV, rightConfig.kA);
    }

    public void start() {
        if (isActive()) return;

        m_isActive = true;

        m_leftFollower.reset();
        m_rightFollower.reset();

        m_timer.schedule(new PathFollowerTask(), 0, m_period);
    }

    private boolean isActive() {
        return m_isActive;
    }

    public void stop() {
        m_timer.cancel();
        m_isActive = false;
    }

    public boolean isFinished() {
        return m_leftFollower.isFinished() && m_rightFollower.isFinished();
    }
}
