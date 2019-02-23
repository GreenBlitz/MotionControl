package org.greenblitz.motion.app;

import org.greenblitz.motion.base.State;
import org.greenblitz.motion.pathing.Path;

@Deprecated
public class AdaptivePStatePursuitController extends AbstractPositionPursuitController<State> {


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
