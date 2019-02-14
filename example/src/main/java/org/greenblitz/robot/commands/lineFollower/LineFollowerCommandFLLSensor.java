package org.greenblitz.robot.commands.lineFollower;

import org.greenblitz.motion.linefollower.LineFollower;
import org.greenblitz.robot.commands.PeriodicCommand;
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
    private int countMaxErrorRuns = 0;
    private int side; // right side = 1
    private double kp;
    private double kd;
    private int kdCounter = 0;

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
        this.kp = kp;
        this.kd = kd;
    }

    public LineFollowerCommandFLLSensor(double kp, double ki, double kd, double speed, double distance, boolean side) {
        this(50, kp, ki, kd, speed, distance, side);
    }

    @Override
    protected void periodic() {
        double value = Chassis.getInstance().getColorSensorValue();
        if (m_firstRun) {
            m_controller.init(0.45, value);
            initDistance = Chassis.getInstance().getDistance();
            m_firstRun = false;
        }
        countMaxErrorRuns = (value < 0.17 || value > 0.73)? countMaxErrorRuns + 1: 0;
        if (countMaxErrorRuns >  5) {
            m_controller.getPidObject().setKp(1.5*kp);
            kdCounter = 5;
        }
        else if (kdCounter > 0) {
            m_controller.getPidObject().setKd(2*kd);
            kdCounter --;
        }
        m_chassis.arcadeDrive(-speed, -side * m_controller.calculatePID(0.45, Chassis.getInstance().getColorSensorValue()));
        m_controller.getPidObject().setKp(kp);
        m_controller.getPidObject().setKd(kd);
        synchronized (this) {
            m_finished = Math.abs(Chassis.getInstance().getDistance() - initDistance) > distance;
        }
    }

    @Override
    protected boolean isFinished() {
        synchronized (this) {
            return m_finished;
        }
    }
}
