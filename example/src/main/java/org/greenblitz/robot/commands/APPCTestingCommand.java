package org.greenblitz.robot.commands;

import org.greenblitz.robot.RobotStats;
import org.greenblitz.robot.subsystems.Chassis;
import org.greenblitz.motion.app.AdaptivePurePursuitController;
import org.greenblitz.motion.app.Path;
import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class APPCTestingCommand extends PeriodicCommand {

    private Chassis m_chasis;
    private boolean m_finished;
    private Path m_path;
    private AdaptivePurePursuitController m_controller;

    public APPCTestingCommand(long period, double lookahead, double wheelbase, List<Position> points) {
        super(period);
        m_chasis = Chassis.getInstance();
        requires(m_chasis);
        m_path = new Path(points);
        m_controller = new AdaptivePurePursuitController(m_path, lookahead, wheelbase, false);
    }

    public APPCTestingCommand(double lookahead, double wheelbase, List<Position> points){
        this(50, lookahead, wheelbase, points);
    }

    public APPCTestingCommand(double lookahead, double wheelbase, Position... points){
        this(50, lookahead, wheelbase, (ArrayList<Position>) Arrays.asList(points));
    }

    @Override
    protected void periodic() {
        double[] moveValues = m_controller.iteration(m_chasis.getLocation());
        if (moveValues == null){
            m_chasis.tankDrive(0,0);
            m_finished = true;
            return;
        }
        m_chasis.tankDrive(moveValues[0], moveValues[1]);
    }

    @Override
    protected boolean isFinished() {
        return m_finished;
    }
}
