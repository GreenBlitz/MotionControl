package org.greenblitz.robot.commands;

import org.greenblitz.motion.linefollower.LineFollower;
import org.greenblitz.robot.subsystems.Chassis;
import org.greenblitz.utils.ColorSensor;

public class LineFollowerCommand extends PeriodicCommand{

    private Chassis m_chassis;
    private double speed;
    private boolean m_finished;
    private boolean m_firstRun;
    private LineFollower m_controller;
    private ColorSensor sensor;
    // red , green , blue
    private short[] line;
    private short[] backround;
    private double distance;
    private double initDistance;


    public LineFollowerCommand(long period, double kp, double ki, double kd, double speed, ColorSensor sensor, double distance){
        super(period);
        m_chassis = Chassis.getInstance();
        requires(m_chassis);
        m_controller = new LineFollower(kp, ki, kd);
        this.speed = speed;
        this.sensor = sensor;
        this.distance = distance;
        m_firstRun = true;
    }

    public LineFollowerCommand(double kp, double ki, double kd, double speed, ColorSensor sensor, double distance){
        this(50, kp, ki, kd, speed, sensor, distance);
    }

    @Override
    protected void periodic() {
        if(m_firstRun){
            m_controller.init(50.0,getPrecentage());
            initDistance = Chassis.getInstance().getDistance();
            m_firstRun = false;
        }
        m_chassis.arcadeDrive(speed,m_controller.calculatePID(50.0, getPrecentage()));

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

    public double getPrecentage(){
        short[] current = sensor.read();
        return 100.0*((current[0]- backround[0])/3.0/(line[0]-backround[0])
                + (current[1]- backround[1])/3.0/(line[1]- backround[1])
                + (current[2]- backround[2])/3.0/(line[2]-backround[2]));
    }
}
