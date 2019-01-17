package org.greenblitz.robot.commands.elevator;

import org.greenblitz.motion.pid.MultivariablePIDController;
import org.greenblitz.motion.pid.PIDController;
import org.greenblitz.motion.pid.PIDObject;
import org.greenblitz.motion.profiling.ActuatorLocation;
import org.greenblitz.motion.profiling.MotionProfile;
import org.greenblitz.motion.profiling.Profiler1D;
import org.greenblitz.motion.profiling.exception.ProfilingException;
import org.greenblitz.robot.commands.PeriodicCommand;
import org.greenblitz.robot.subsystems.ElevatorPrototype;
import org.greenblitz.utils.CSVWrapper;

import java.util.*;

public class MoveByProfileElevator extends PeriodicCommand {

    private long startTime;
    private MotionProfile profile; // MaxV = 3800, MaxA=30000
    private boolean done = false;
    private static final double MAX_VEL = 1.5;//63000;
    private static final double MAX_ACCEL = 115 / 10.0;//79;//33000000;
    private static final long PERIOD = 1;

    private static final double kV = 1.0 / MAX_VEL,
            kA = 0,
            kP = 0,
            ff = 0.3;

    private PIDController locationController;

    private CSVWrapper printer;

    public MoveByProfileElevator(ActuatorLocation... locs){
        this(Arrays.asList(locs));
    }

    public MoveByProfileElevator(List<ActuatorLocation> locs) {
        super(PERIOD);
        requires(ElevatorPrototype.getInstance());

        try {
            profile = Profiler1D.generateProfile(locs, MAX_VEL, MAX_ACCEL, -MAX_ACCEL);
        } catch (ProfilingException e) {
            e.printStackTrace();
            done = true;
        }

        printer = CSVWrapper.generateWrapper("testing.csv", 3,
                "t", "Profile Velocity", "Actual Velocity");

        locationController = new PIDController(new PIDObject(kP));

        System.out.println(profile);
    }

    @Override
    protected void initialize() {
        startTime = System.currentTimeMillis();
        done = false;
        start();
    }

    @Override
    protected void execute() {

    }

    @Override
    protected boolean isFinished() {
        return done;
    }

    @Override
    protected void interrupted() {
        end();
    }

    @Override
    protected void end() {
        if (printer != null)
            printer.flush();
        done = false;
        ElevatorPrototype.getInstance().stop();
    }

    @Override
    protected void periodic() {
        double t = (System.currentTimeMillis() - startTime) / 1000.0;

        if (profile.isOver(t)) {
            done = true;
            return;
        }

        if (printer != null)
            printer.addValues(t, profile.getVelocity(t), ElevatorPrototype.getInstance().getSpeed());

        ElevatorPrototype prototype = ElevatorPrototype.getInstance();

        double vPower = kV * profile.getVelocity(t);
        double aPower = kA * profile.getAcceleration(t);

        double power = vPower + aPower + ff + locationController.calculatePID(profile.getLocation(t),
                prototype.getDistance());

        prototype.set(-power);
    }

}
