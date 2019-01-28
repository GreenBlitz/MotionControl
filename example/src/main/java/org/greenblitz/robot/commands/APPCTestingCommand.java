package org.greenblitz.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.greenblitz.debug.RemoteCSVTarget;
import org.greenblitz.motion.app.AdaptivePolynomialPursuitController;
import org.greenblitz.motion.app.AdaptivePurePursuitController;
import org.greenblitz.motion.app.Localizer;
import org.greenblitz.motion.app.Path;
import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Position;
import org.greenblitz.robot.subsystems.Chassis;

import java.util.Arrays;

public class APPCTestingCommand extends Command {

    private Chassis m_chasis;
    private boolean m_finished;
    private Path m_path;
    private AdaptivePurePursuitController m_controller;

    public APPCTestingCommand(double lookahead, double wheelbase, Path points, double factor) {
        m_chasis = Chassis.getInstance();
        m_chasis.setOutputScale(factor);
        requires(m_chasis);
        m_path = points;
        m_path.interpolatePoints( 100);

        RemoteCSVTarget.initTarget("path", "x", "y");

        m_controller = new AdaptivePurePursuitController(m_path, lookahead*factor, wheelbase, false);
    }

    public APPCTestingCommand(double lookahead, double wheelbase, Path points) {
        this(lookahead, wheelbase, points, 1);
    }


    public APPCTestingCommand(double lookahead, double wheelbase, Position... points){
        this(lookahead, wheelbase, new Path(Arrays.asList(points)));
    }

    public APPCTestingCommand(double lookahead, double wheelbase, double factor, Position... points){
        this(lookahead, wheelbase, new Path(Arrays.asList(points)), factor);
    }

    @Override
    protected void initialize() {
        m_chasis.setBrake();
        m_path.sendToCSV("path");
        super.initialize();
    }

    @Override
    protected void execute() {
        double[] moveValues = m_controller.iteration(m_chasis.getLocation());
        if (moveValues == null){
            m_chasis.tankDrive(0,0);
            m_finished = true;
            return;
        }
        System.out.println(Arrays.toString(moveValues));
        System.out.println("driving");
        m_chasis.tankDrive(moveValues[0], moveValues[1]);
    }

    @Override
    protected void end(){
        SmartDashboard.putNumber("APPC final x", m_chasis.getLocation().getX());
        SmartDashboard.putNumber("APPC final y", m_chasis.getLocation().getY());
        m_chasis.setCoast();
    }

    protected void savePath(String fileName){
        m_path.saveAsCSV(fileName);
    }

    @Override
    protected boolean isFinished() {
        return m_finished && Point.isFuzzyEqual(Chassis.getInstance().getSpeed(), 0, 0.5);
    }
}
