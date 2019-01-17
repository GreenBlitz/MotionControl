package org.greenblitz.robot.commands.elevator;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.greenblitz.motion.pid.PIDController;
import org.greenblitz.motion.pid.PIDObject;
import org.greenblitz.motion.profiling.ActuatorLocation;
import org.greenblitz.motion.profiling.MotionProfile;
import org.greenblitz.motion.profiling.Profiler1D;
import org.greenblitz.motion.profiling.exception.ProfilingException;
import org.greenblitz.robot.subsystems.ElevatorPrototype;
import org.greenblitz.utils.CSVWrapper;

import java.util.Arrays;
import java.util.List;

public class MoveByProfileElevator extends Command implements Runnable {

    private long startTime;
    private MotionProfile profile;
    private boolean done = false;
    private static final double MAX_VEL = 1.5;
    private static final double MAX_ACCEL = 115 / 10.0;

    private static final double kV = 30.0 / MAX_VEL,
            kA = 0,
            kP = 0,
            ff = 0.3;

    private PIDController locationController;
    private CSVWrapper printer;
    private Thread thread;

    public MoveByProfileElevator(ActuatorLocation... locs) {
        this(Arrays.asList(locs));
    }

    public MoveByProfileElevator(List<ActuatorLocation> locs) {
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
        thread = new Thread(this);
        ElevatorPrototype.getInstance().resetEncoder();
        if (!thread.isAlive())
            thread.start();
        done = false;
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
    public void run() {
        locationController.init(0, 0);
        startTime = System.currentTimeMillis();
        ElevatorPrototype prototype = ElevatorPrototype.getInstance();
        double vPower, aPower, power;
        while (true) {
            double t = (System.currentTimeMillis() - startTime) / 1000.0;

            if (profile.isOver(t)) {
                done = true;
                return;
            }

            if (printer != null)
                printer.addValues(t, profile.getVelocity(t), prototype.getSpeed());

            vPower = kV * profile.getVelocity(t);
            aPower = kA * profile.getAcceleration(t);

            power = vPower + aPower + ff + locationController.calculatePID(profile.getLocation(t),
                    prototype.getDistance());
            SmartDashboard.putNumber("power", power);

            prototype.set(-power);
        }
    }

}
