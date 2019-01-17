package org.greenblitz.robot.commands.elevator;

import edu.wpi.first.wpilibj.command.Command;
import org.greenblitz.motion.pid.PIDController;
import org.greenblitz.motion.pid.PIDObject;
import org.greenblitz.motion.profiling.ActuatorLocation;
import org.greenblitz.motion.profiling.MotionProfile;
import org.greenblitz.motion.profiling.Profiler1D;
import org.greenblitz.motion.profiling.exception.ProfilingException;
import org.greenblitz.robot.subsystems.ElevatorPrototype;
import org.greenblitz.utils.CSVWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MoveByProfileElevator extends Command {

    MotionProfile profile; // MaxV = 3800, MaxA=30000
    boolean done = false;
    public static final double MAX_VEL = 1.5;//63000;
    public static final double MAX_ACCEL = 115 / 10.0;//79;//33000000;
    private static final long PERIOD = 5;
    private ProfileRunner runner;
    private Timer timer;

    private CSVWrapper printer;

    public MoveByProfileElevator() {
        requires(ElevatorPrototype.getInstance());
        List<ActuatorLocation> locs = new ArrayList<>();
        locs.add(new ActuatorLocation(0, 0));
        locs.add(new ActuatorLocation(1.3, 0));
        try {
            profile = Profiler1D.generateProfile(locs, MAX_VEL, MAX_ACCEL, -MAX_ACCEL);
        } catch (ProfilingException e) {
            e.printStackTrace();
            done = true;
        }
        printer = CSVWrapper.generateWrapper("testing.csv", 3,
                "t", "Profile Velocity", "Actual Velocity");
        PIDObject locObj = new PIDObject(0.01);
        PIDController locPID = new PIDController(locObj);
        runner = new ProfileRunner(profile, true, 30.0 / MAX_VEL, 2.0 / MAX_ACCEL, 0.3,
                locPID);
        System.out.println(profile);
    }

    @Override
    protected void initialize() {
        done = false;
        runner.reset();
        timer = new Timer(true);
        timer.schedule(runner, 0, PERIOD);
    }

    @Override
    protected void execute() {
        double t = runner.getTime();
        if (printer != null)
            printer.addValues(t, profile.getVelocity(t), ElevatorPrototype.getInstance().getSpeed());
        if (runner.isOver())
            done = true;
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
        timer.cancel();
        timer.purge();
        done = false;
        ElevatorPrototype.getInstance().stop();
    }

    private class ProfileRunner extends TimerTask {

        private long timeStart;
        private MotionProfile profile;
        private boolean done;
        private boolean flipped;
        private double kv;
        private double ka;
        private double ff;
        private PIDController locationController;
        private PIDController velocityController;
        private ElevatorPrototype prototype;

        public ProfileRunner(MotionProfile profile, boolean flipped, double kv, double ka, double ff,
                             PIDController locationController, PIDController velocityController) {
            this.profile = profile;
            this.flipped = flipped;
            this.done = false;
            this.kv = kv;
            this.ka = ka;
            this.ff = ff;
            this.locationController = locationController;
            this.velocityController = velocityController;
            this.prototype = ElevatorPrototype.getInstance();
        }

        public ProfileRunner(MotionProfile profile, boolean flipped, double kv, double ka, double ff,
                             PIDController locationController) {
            this(profile, flipped, kv, ka, ff, locationController, null);
        }

        public ProfileRunner(MotionProfile profile, boolean flipped, double kv, double ka, double ff) {
            this(profile, flipped, kv, ka, ff, null);
        }

        public void reset() {
            if (velocityController != null)
                velocityController.init(new double[]{0}, new double[]{0});
            if (locationController != null)
                locationController.init(new double[]{0}, new double[]{0});
            this.timeStart = System.currentTimeMillis();

            done = false;
        }

        @Override
        public void run() {

            double t = getTime();
            if (profile.isOver(t)) {
                done = true;
                return;
            }
            double vPower = kv * profile.getVelocity(t);
            double aPower = ka * profile.getAcceleration(t);

            double power = vPower + aPower + ff;

            if (locationController != null)
                power += locationController.calculatePID(new double[]{profile.getLocation(t)},
                        new double[]{prototype.getDistance()})[0];
            if (velocityController != null)
                power += velocityController.calculatePID(new double[]{profile.getVelocity(t)},
                        new double[]{prototype.getSpeed()})[0];

            prototype.set(flipped ? -power : power);
        }

        public double getTime() {
            return (System.currentTimeMillis() - timeStart) / 1000.0;
        }

        public boolean isOver() {
            return done;
        }
    }

}
