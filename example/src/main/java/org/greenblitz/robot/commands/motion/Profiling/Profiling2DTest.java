package org.greenblitz.robot.commands.motion.Profiling;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.State;
import org.greenblitz.motion.pid.PIDController;
import org.greenblitz.motion.pid.PIDObject;
import org.greenblitz.motion.profiling.MotionProfile2D;
import org.greenblitz.robot.subsystems.Chassis;

import java.util.Arrays;
import java.util.List;


public class Profiling2DTest extends Command {

    private long startTime;
    private MotionProfile2D profile;
    private boolean isFinished;

    private double t, limitor;

    private static final double MAX_VEL = 0.3; //3.2;
    private static final double MAX_ACCEL = 789; //42.6951911765/2;
    private static final double ONE_THOUSANDTH = 0.001;

    private static final double kV = 0, //1.089 * (1 / MAX_VEL),
            kA = 0, //0.7*(1/MAX_ACCEL * 0.5),
            kAng = 0,
            kP = 0;

    private PIDController locationController;

    private final Chassis m_chasis;

    public Profiling2DTest(Subsystem requires, State... locs) {
        this(requires, Arrays.asList(locs));
    }

    public Profiling2DTest(Subsystem requires, List<State> locs) {
        super(requires);
        locationController = new PIDController(new PIDObject(kP));
        profile = new MotionProfile2D(locs);
        m_chasis = Chassis.getInstance();
    }

    @Override
    protected void initialize() {
        System.out.println(profile);

        locationController.init(0, 0);
        drive(0, 0);

        startTime = System.currentTimeMillis();

        t = 0.0;
        limitor = 0;
    }

    @Override
    protected void execute() {
        t = (System.currentTimeMillis() - startTime) * ONE_THOUSANDTH;

        Point loc = profile.getLocation(t);
        Point vel = profile.getVelocity(t);
        Point acc = profile.getAcceleration(t);
        double angVel = profile.getAngularVelocity(t);

        isFinished = profile.isOutOfProfile(t);
        if (isFinished) {
//            System.out.println("------------------");
//            System.out.printf(
//                    "t: %f\nposition: expected: %f\n          actual:   %f\nvelocity: expected: %f\n          actual:   %f\n",
//                    t,
//                    profile.getLocation(profile.getTEnd()),
//                    getLoction(),
//                    profile.getVelocity(profile.getTEnd()),
//                    getActualSpeed());
            return;
        }

        System.out.println(t);
        double vPower = kV * Point.norm(vel);
        double aPower = kA * Point.norm(acc);
        double angPower = kAng * angVel;

        //power = vPower + aPower + ff + locationController.calculatePID(profile.getLocation(t), prototype.getDistance());
        double power = vPower + aPower;

        drive(power, angPower);

//        if (t >= limitor) {
//            System.out.println("------------------");
//            System.out.printf(
//                    "t: %f\nposition: expected: %f\n          actual:   %f\nvelocity: expected: %f\n          actual:   %f\n",
//                    t,
//                    loc,
//                    getLoction(),
//                    vel,
//                    getActualSpeed());
//
//            limitor += Math.max(t-limitor, 0.1);
//        }

//            if (printerVel != null)
//                printerVel.report(t, profile.getVelocity(t), prototype.getSpeed());
//            if (printerLoc != null)
//                printerLoc.report(t, profile.getLocation(t), prototype.getDistance());
    }

    @Override
    protected boolean isFinished() {
        return isFinished;
    }

    protected double getActualSpeed(){
        return m_chasis.getSpeed();
    }
    protected Point getLoction(){
        return m_chasis.getLocation();
    }
    protected void drive(double power, double angPower){
        m_chasis.arcadeDrive(power, angPower);
    }
}