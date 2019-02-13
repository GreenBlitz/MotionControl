package org.greenblitz.motion.app;

import org.greenblitz.motion.base.PState;
import org.greenblitz.motion.pathing.Path;

@Deprecated
public class AdaptivePStatePursuitController extends AbstractPositionPursuitController<PState> {

    /**
     * @param m_path
     * @param m_lookahead
     * @param m_wheelBase
     * @param m_tolerance
     */
    public AdaptivePStatePursuitController(Path<PState> m_path, double m_lookahead, double m_wheelBase, double m_tolerance) {
        super(m_path, m_lookahead, m_wheelBase, m_tolerance);
    }

    @Override
    protected double getSpeed(PState robotLoc, PState goalPoint) {
        return super.getSpeed(robotLoc, goalPoint);
    }

    public void curveInterpolate(double t){

    }

    @Override
    protected double getCurvature(PState robotLoc, PState goalPoint) {
        return 0;
    }
}
