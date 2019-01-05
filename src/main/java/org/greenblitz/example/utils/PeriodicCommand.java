package org.greenblitz.example.utils;

import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.command.Command;

public abstract class PeriodicCommand extends Command {
    private final Notifier m_periodicRunner;

    protected final long m_period;

    public PeriodicCommand(long period) {
        m_period = period;
        m_periodicRunner = new Notifier(this::periodic);
    }

    public PeriodicCommand() {
        this(20);
    }

    @Override
    protected void initialize() {
        m_periodicRunner.startPeriodic(m_period / 1000.0);
    }

    @Override
    protected void end() {
        m_periodicRunner.stop();
    }

    protected abstract void periodic();

}
