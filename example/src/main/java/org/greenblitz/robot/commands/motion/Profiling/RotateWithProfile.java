package org.greenblitz.robot.commands.motion.Profiling;

import edu.wpi.first.wpilibj.command.Command;
import org.greenblitz.motion.profiling.ActuatorLocation;
import org.greenblitz.motion.profiling.MotionProfile1D;
import org.greenblitz.motion.profiling.Profiler1D;
import org.greenblitz.motion.profiling.followers.FeedForwards1DFollower;
import org.greenblitz.robot.subsystems.Chassis;

public class RotateWithProfile extends Command {

    protected FeedForwards1DFollower profile;

    public RotateWithProfile(double goal, double maxRotVel, double maxRotAcc, double currentAngularVel){
        requires(Chassis.getInstance());
        profile = new FeedForwards1DFollower(Profiler1D.generateProfile(maxRotVel, -maxRotVel, maxRotAcc, -maxRotAcc,
                new ActuatorLocation(0, currentAngularVel),
                new ActuatorLocation(goal, 0)),
                1.0/maxRotVel, 1.0/maxRotAcc);
    }


    @Override
    protected void initialize() {
        profile.init();
    }

    @Override
    protected void execute() {
         Chassis.getInstance().arcadeDrive(0, profile.run());
    }

    @Override
    protected void end() {
        Chassis.getInstance().stop();
    }

    @Override
    protected boolean isFinished() {
        return profile.isFinished();
    }
}
