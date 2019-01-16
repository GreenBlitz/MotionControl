package org.greenblitz.example.robot.commands.elevator;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.greenblitz.example.robot.subsystems.ElevatorPrototype;
import org.greenblitz.motion.motionprofiling.ActuatorLocation;
import org.greenblitz.motion.motionprofiling.MotionProfile;
import org.greenblitz.motion.motionprofiling.Profiler1D;
import org.greenblitz.motion.motionprofiling.exception.ProfilingException;

import java.util.ArrayList;
import java.util.List;

public class MoveByProfileElevator extends Command {

    MotionProfile profile; // MaxV = 3800, MaxA=30000
    long timeStart;
    boolean done = false;
    public static final double MAX_VEL = 1.5;//63000;
    public static final double MAX_ACCEL = 115/10.0;//79;//33000000;

    public MoveByProfileElevator(){
        requires(ElevatorPrototype.getInstance());
        List<ActuatorLocation> locs = new ArrayList<>();
        locs.add(new ActuatorLocation(0, 0));
        locs.add(new ActuatorLocation(1.3, 0));
        try {
            profile = Profiler1D.generateProfile(locs, MAX_VEL, MAX_ACCEL, -MAX_ACCEL);
        } catch (ProfilingException e){
            e.printStackTrace();
            done = true;
        }
        System.out.println(profile);
    }

    @Override
    protected void initialize(){
        timeStart = System.currentTimeMillis();
    }

    @Override
    protected void execute() {
        if (timeStart == 0)
            timeStart = System.currentTimeMillis();
        long deltaTime = System.currentTimeMillis() - timeStart;
        double t = deltaTime / 1000.0;
        if (profile.isOver(t)) {
            done = true;
            return;
        }
        double vPower = 2*(1.0 /MAX_VEL)*profile.getVelocity(t);
        double aPower = 1*(1.0/MAX_ACCEL)*profile.getAcceleration(t);
        SmartDashboard.putNumber("profV", profile.getVelocity(t));
        SmartDashboard.putNumber("Velocity", ElevatorPrototype.getInstance().getSpeed());

        if (Math.abs(profile.getAcceleration(t)) < 10E-5)
            System.out.println(ElevatorPrototype.getInstance().getSpeed());

        double error = -(ElevatorPrototype.getInstance().getDistance() - profile.getLocation(t));
        System.out.println("ERR: "  + error);
        double power = 0*error + vPower + aPower + 0.3;
        SmartDashboard.putNumber("power", power);
        ElevatorPrototype.getInstance().set(-power);
    }

    @Override
    protected boolean isFinished() {
        return done;
    }

    @Override
    protected void end() {
        timeStart = 0;
        done = false;
        System.out.println((System.currentTimeMillis() - timeStart)/1000.0);
        SmartDashboard.putNumber("End Location", ElevatorPrototype.getInstance().getDistance());
        ElevatorPrototype.getInstance().stop();
    }

}
