package org.greenblitz.utils;

import edu.wpi.first.wpilibj.Notifier;

public abstract class PeriodicRunner {
    private final Notifier m_periodicRunner;

    private final long m_period;
    private volatile boolean m_active = false;

    public PeriodicRunner(long period) {
        m_period = period;
        m_periodicRunner = new Notifier(this::_periodic);
        m_periodicRunner.startPeriodic(m_period / 1000.0);
    }

    public PeriodicRunner() {
        this(20);
    }

    public void start() {
        m_active = true;
    }

    public void stop() {
        m_active = false;
    }

    public void end() {
        m_periodicRunner.stop();
    }

    public boolean isActive() {
        return m_active;
    }

    public abstract boolean isFinished();

    protected abstract void whenActive();

    protected void whenInActive() {

    }

    private void _periodic() {
        if (isActive()) whenActive();
        else whenInActive();
    }

}
