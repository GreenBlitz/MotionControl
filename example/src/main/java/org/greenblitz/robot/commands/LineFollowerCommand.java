package org.greenblitz.robot.commands;

import org.greenblitz.motion.linefollower.LineFollower;
import org.greenblitz.robot.subsystems.Chassis;
import org.greenblitz.utils.ColorSensor;

public class LineFollowerCommand extends PeriodicCommand{

    protected Chassis m_chassis;
    protected double speed;
    protected boolean m_finished;
    protected boolean m_firstRun;
    protected LineFollower m_controller;
    protected ColorSensor sensor;
    // red , green , blue
    protected short[] line;
    protected short[] backround;
    protected double distance;
    protected double initDistance;
    private int side; // right side = 1

// right side = true
    public LineFollowerCommand(long period, double kp, double ki, double kd, double speed, ColorSensor sensor, double distance, boolean side){
        super(period);
        m_chassis = Chassis.getInstance();
        requires(m_chassis);
        m_controller = new LineFollower(kp, ki, kd);
        this.speed = speed;
        this.sensor = sensor;
        this.distance = distance;
        m_firstRun = true;
        this.side = side ? 1: -1;
    }

    public LineFollowerCommand(double kp, double ki, double kd, double speed, ColorSensor sensor, double distance, boolean side){
        this(50, kp, ki, kd, speed, sensor, distance, side);
    }

    @Override
    protected void periodic() {
        if(m_firstRun){
            m_controller.init(50.0,getPrecentage(sensor.read()));
            initDistance = Chassis.getInstance().getDistance();
            m_firstRun = false;
        }
        m_chassis.arcadeDrive(speed,side * m_controller.calculatePID(50.0, getPrecentage(sensor.read())));

        synchronized (this){
            m_finished = Chassis.getInstance().getDistance() > initDistance + distance;
        }
    }

    @Override
    protected boolean isFinished() {
        synchronized (this) {
            return m_finished;
        }
    }

    protected double getPrecentage(short[] rgb){
        short[] current = rgb;
        return 100.0/3.0*((current[0]- backround[0])/(line[0]-backround[0])
                + (current[1]- backround[1])/(line[1]- backround[1])
                + (current[2]- backround[2])/(line[2]-backround[2]));
    }
}
