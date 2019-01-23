package org.greenblitz.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import org.greenblitz.utils.PeriodicRunner;
import org.greenblitz.utils.SmartEncoder;
import org.greenblitz.motion.app.Localizer;
import org.greenblitz.motion.base.Position;

public class LocalizerRunner extends PeriodicRunner {

    private Localizer m_localizer;

    private SmartEncoder m_leftEncoder;
    private SmartEncoder m_rightEncoder;

    private boolean m_resetOnDisable = false;

    public LocalizerRunner(long period, double wheelBase, SmartEncoder leftEncoder, SmartEncoder rightEncoder) {
        super(period);
        m_localizer = Localizer.getInstance();
        m_localizer.configure(wheelBase, 0, 0);
        m_leftEncoder = leftEncoder; leftEncoder.reset();
        m_rightEncoder = rightEncoder; rightEncoder.reset();
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
