package org.greenblitz.example.robot;

import edu.wpi.first.wpilibj.DriverStation;
import org.greenblitz.example.utils.PeriodicRunner;
import org.greenblitz.example.utils.SmartEncoder;
import org.greenblitz.motion.app.Localizer;
import org.greenblitz.motion.base.Position;

public class LocalizerRunner extends PeriodicRunner {

    private Localizer m_localizer;

    private SmartEncoder m_leftEncoder;
    private SmartEncoder m_rightEncoder;

    private boolean m_resetOnDisable = false;

    public LocalizerRunner(long period, double wheelBase, SmartEncoder leftEncoder, SmartEncoder rightEncoder) {
        super(period);
        m_localizer = new Localizer(wheelBase);
        m_leftEncoder = leftEncoder;
        m_rightEncoder = rightEncoder;
    }

    public LocalizerRunner(double wheelBase, SmartEncoder leftEncoder, SmartEncoder rightEncoder) {
        this(20, wheelBase, leftEncoder, rightEncoder);
    }

    public void resetOnDisableh() {
        setResetOnDisable(true);
    }

    public void dontResetOnDisable() {
        setResetOnDisable(false);
    }

    public void setResetOnDisable(boolean value) {
        m_resetOnDisable = value;
    }

    public boolean shouldResetOnDisable() {
        return m_resetOnDisable;
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    protected void whenActive() {
        m_localizer.update(m_leftEncoder.getDistance(), m_rightEncoder.getDistance());
    }

    @Override
    protected void whenInActive() {
        if (DriverStation.getInstance().isDisabled() && shouldResetOnDisable()) reset();
    }

    public Position getLocation() {
        return m_localizer.getLocation();
    }

    public void reset() {
        m_localizer.reset(m_leftEncoder.getDistance(), m_rightEncoder.getDistance());
    }
}
