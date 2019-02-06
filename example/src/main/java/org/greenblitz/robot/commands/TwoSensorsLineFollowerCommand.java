package org.greenblitz.robot.commands;

import org.greenblitz.motion.linefollower.LineFollower;
import org.greenblitz.robot.subsystems.Chassis;
import org.greenblitz.utils.ColorSensor;

public class TwoSensorsLineFollowerCommand extends LineFollowerCommand {

    private ColorSensor leftSensor;
    private LineFollower m_leftController;

    public TwoSensorsLineFollowerCommand(long period, double kp, double ki, double kd, double speed,
                                         ColorSensor sensor, ColorSensor leftSensor, double distance){
        super(period, kp, ki, kd, speed, sensor, distance, true);
        this.leftSensor = leftSensor;
        m_leftController = new LineFollower(kp,ki,kd);
    }

    public TwoSensorsLineFollowerCommand(double kp, double ki, double kd, double speed,
                                         ColorSensor sensor, ColorSensor leftSensor, double distance){
        this(50, kp, ki, kd, speed, sensor, leftSensor, distance);
    }


    @Override
    protected void periodic() {
        if(m_firstRun){
            m_controller.init(50.0,getPrecentage(sensor.read()));
            m_leftController.init(50.0,getPrecentage(leftSensor.read()));
            initDistance = Chassis.getInstance().getDistance();
            m_firstRun = false;
        }
        m_chassis.arcadeDrive(speed, (m_controller.calculatePID(50.0, getPrecentage(sensor.read())) +
                m_leftController.calculatePID(50.0,getPrecentage(leftSensor.read())))/2.0);

        synchronized (this){
            m_finished = Chassis.getInstance().getDistance() > initDistance + distance;
        }
    }

}
