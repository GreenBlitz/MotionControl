package org.greenblitz.robot.commands.motion.APPC;

import edu.wpi.first.wpilibj.command.Command;
import org.greenblitz.motion.app.Localizer;
import org.greenblitz.motion.base.Position;
import org.greenblitz.robot.subsystems.Chassis;
import org.greenblitz.utils.Navx;

public class SetLocalizerLocation extends Command {

    private Double m_startX,
                   m_startY,
                   m_startA;

    public SetLocalizerLocation(Double startX, Double startY, Double startA) {
        m_startX = startX;
        m_startY = startY;
        m_startA = startA;
    }

    public SetLocalizerLocation(Position startLoc) {
        m_startX = startLoc.getX();
        m_startY = startLoc.getY();
        m_startA = startLoc.getAngle();
    }

    @Override
    protected void initialize() {
        if (m_startX == null)
            m_startX = Localizer.getInstance().getLocation().getX();
        if (m_startY == null)
            m_startY = Localizer.getInstance().getLocation().getY();
        if (m_startA == null)
            m_startA = Localizer.getInstance().getLocation().getAngle();
        Localizer.getInstance().reset(Chassis.getInstance().getLeftDistance(), Chassis.getInstance().getRightDistance(),
                                      new Position(m_startX, m_startY, m_startA));
        Navx.getInstance().reset();
    }

    @Override
    protected boolean isFinished() {
        return true;
    }
}