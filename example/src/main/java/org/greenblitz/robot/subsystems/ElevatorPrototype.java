package org.greenblitz.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.greenblitz.robot.commands.elevator.StopElevator;

public class ElevatorPrototype extends Subsystem {

    private static final double MAX_HEIGHT = 1.4;
    private static final double TICKS_PER_METER = 40000;

    private static ElevatorPrototype instance;

    private TalonSRX m_motor;

    public static void init() {
        if (instance == null)
            instance = new ElevatorPrototype();
    }

    public static ElevatorPrototype getInstance() {
        if (instance == null)
            init();
        return instance;
    }

    private ElevatorPrototype() {
        m_motor = new TalonSRX(6);
    }

    @Override
    protected void initDefaultCommand() {
        setDefaultCommand(new StopElevator());
    }

    public void set(double power) {
        if (getDistance() > MAX_HEIGHT && power < -0.3)
            power = -0.3;
        power = Math.min(-0.1, power);
        m_motor.set(ControlMode.PercentOutput, power);
    }

    public void stop() {
        set(0);
    }

    public double getDistance() {
        return m_motor.getSensorCollection().getQuadraturePosition() / TICKS_PER_METER;
    }

    public double getSpeed() {
        return (10*m_motor.getSensorCollection().getQuadratureVelocity()) / TICKS_PER_METER;
    }

    public void resetEncoder() {
        m_motor.getSensorCollection().setQuadraturePosition(0, 10);
    }

    public void update() {
        SmartDashboard.putNumber("Elevator::Location", getDistance());
        SmartDashboard.putNumber("Evelator::Velocity", getSpeed());
        SmartDashboard.putString("Elevator::Command", getCurrentCommandName());
    }
}