package org.greenblitz.robot.commands.motion.Profiling;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.greenblitz.debug.RemoteCSVTarget;
import org.greenblitz.motion.base.Position;
import org.greenblitz.motion.base.State;
import org.greenblitz.motion.base.Vector2D;
import org.greenblitz.motion.profiling.MotionProfile2D;
import org.greenblitz.motion.profiling.ChassisProfiler2D;
import org.greenblitz.robot.commands.PeriodicCommand;
import org.greenblitz.robot.subsystems.Chassis;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class Profiling2DTestingCommand extends PeriodicCommand {

    private long startTime;
    private MotionProfile2D profile;
    private boolean isFinished;

    private static final double DEFAULT_MAX_VEL = 0; //3.2;
    private static final double DEFAULT_MAX_ACCEL = 0; //42.6951911765/2;
    private static final double DEFAULT_MAX_ANG_VEL = 0;
    private static final double DEFAULT_MAX_ANG_ACCEL = 0;
    private static final double ONE_THOUSANDTH = 0.001;

    private final double maxVel, maxAcc, maxAngVel, maxAngAcc;
    private final double kV, kA, kAV, kAA;

    private final Chassis m_chasis;

    private List<Double> tmpLogger;
    private final RemoteCSVTarget m_logger;

    public Profiling2DTestingCommand(State... locs){
        this(DEFAULT_MAX_VEL, DEFAULT_MAX_ACCEL, DEFAULT_MAX_ANG_VEL, DEFAULT_MAX_ANG_ACCEL);
    }

    public Profiling2DTestingCommand(double maxVel, double maxAcc, double maxAngVel, double maxAngAcc, State... locs) {
        this(Arrays.asList(locs), maxVel, maxAcc, maxAngVel, maxAngAcc);
    }

    public Profiling2DTestingCommand(List<State> locs, double maxVel, double maxAcc, double maxAngVel, double maxAngAcc) {
        this.maxVel = maxVel; kV = 1/maxVel;
        this.maxAcc = maxAcc; kA = 1/maxAcc;
        this.maxAngVel = maxAngVel; kAV = 1/maxAngVel;
        this.maxAngAcc = maxAngAcc; kAA = 1/maxAngAcc;
        profile = ChassisProfiler2D.generateProfile(locs, 0.05, Math.random(), Math.random(), Math.random(), Math.random());
        m_chasis = Chassis.getInstance();
        requires(m_chasis);
        tmpLogger = new LinkedList<>();
        m_logger = RemoteCSVTarget.initTarget("profileResults", "t", "left dist", "right dist", "left speed", "right speed");
    }

    @Override
    protected void initialize() {
        startTime = System.currentTimeMillis();

        isFinished = false;
    }

    @Override
    protected void periodic() {
        double t = (System.currentTimeMillis() - startTime) * ONE_THOUSANDTH;

        Vector2D vel = profile.getVelocity(t);
        Vector2D acc = profile.getAcceleration(t);

        isFinished = profile.isOver(t);
        if (isFinished) {
            return;
        }

        System.out.println(t);
        double vPower = kV * vel.getX();
        double aPower = kA * acc.getX();
        double angVPower = kAV * vel.getY();
        double angAPower = kAA * acc.getY();

        double linearPower = vPower + aPower;
        double angularPower = angVPower + angAPower;

        drive(linearPower, angularPower);

        tmpLogger.add(t);
        tmpLogger.add(m_chasis.getLeftDistance());
        tmpLogger.add(m_chasis.getRightDistance());
        tmpLogger.add(m_chasis.getLeftSpeed());
        tmpLogger.add(m_chasis.getRightSpeed());

    }

    @Override
    protected void end(){
        for(int ind=0; ind<tmpLogger.size(); ind+=5){
            m_logger.report(tmpLogger.get(ind),
                    tmpLogger.get(ind+1),
                    tmpLogger.get(ind+2),
                    tmpLogger.get(ind+3),
                    tmpLogger.get(ind+4));
            try{
                Thread.sleep(16);
            }catch (Exception e){}
        }
        System.out.println(profile);
        Position loc = m_chasis.getLocation();
        SmartDashboard.putNumber("end x", loc.getX());
        SmartDashboard.putNumber("end y", loc.getY());
        SmartDashboard.putNumber("end angle", loc.getAngle());
    }

    @Override
    protected boolean isFinished() {
        return isFinished;
    }

    protected void drive(double power, double angPower){
        m_chasis.arcadeDrive(power, angPower);
    }
}