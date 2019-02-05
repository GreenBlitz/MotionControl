package org.greenblitz.robot.commands;

import org.greenblitz.motion.linefollower.LineFollower;
import org.greenblitz.robot.subsystems.Chassis;
import org.greenblitz.utils.ColorSensor;

public class LineFollowerCommandFLLSensor extends PeriodicCommand {

    protected Chassis m_chassis;
    protected double speed;
    protected boolean m_finished;
    protected boolean m_firstRun;
    protected LineFollower m_controller;
    protected double distance;
    protected double initDistance;
    private int side; // right side = 1

    // right side = true
    public LineFollowerCommandFLLSensor(long period, double kp, double ki, double kd, double speed, double distance, boolean side) {
        super(period);
        m_chassis = Chassis.getInstance();
        requires(m_chassis);
        m_controller = new LineFollower(kp, ki, kd);
        this.speed = speed;
        this.distance = distance;
        m_firstRun = true;
        this.side = side ? 1 : -1;
    }

    public LineFollowerCommandFLLSensor(double kp, double ki, double kd, double speed, double distance, boolean side) {
        this(50, kp, ki, kd, speed, distance, side);
    }

    @Override
    protected void periodic() {
        if (m_firstRun) {
            m_controller.init(0.45, Chassis.getInstance().getColorSensorValue());
            initDistance = Chassis.getInstance().getDistance();
            m_firstRun = false;
        }
        m_chassis.arcadeDrive(-speed, -side * m_controller.calculatePID(0.45, Chassis.getInstance().getColorSensorValue()));

        synchronized (this) {
            m_finished = Chassis.getInstance().getDistance() > initDistance + distance;
        }
    }

    @Override
    protected boolean isFinished() {
        synchronized (this) {
            return m_finished;
        }
    }
}
