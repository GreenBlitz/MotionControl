package org.greenblitz.motion.app;

import org.greenblitz.motion.base.Position;
import org.greenblitz.motion.base.State;
import org.greenblitz.motion.base.Vector2D;
import org.greenblitz.motion.pathing.Path;

import java.util.List;

public class AdaptivePStatePursuitController extends AbstractPositionPursuitController<State> {

    /**
     * @param m_path
     * @param m_lookahead
     * @param m_wheelBase
     * @param m_tolerance
     */
    public AdaptivePStatePursuitController(Path<State> m_path, double m_lookahead, double m_wheelBase, double m_tolerance) {
        super(m_path, m_lookahead, m_wheelBase, m_tolerance);
    }

    @Override
    protected double getSpeed(State robotLoc, State goalPoint) {
        return super.getSpeed(robotLoc, goalPoint);
    }

    public void curveInterpolate(double t){

    }

    @Override
    protected double getCurvature(State robotLoc, State goalPoint) {
        return 0;
    }
}
