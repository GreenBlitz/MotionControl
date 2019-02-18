package org.greenblitz.robot.commands.lineFollower;

import org.greenblitz.motion.linefollower.LineFollower;
import org.greenblitz.motion.tolerance.ITolerance;
import org.greenblitz.robot.commands.PeriodicCommand;
import org.greenblitz.robot.subsystems.Chassis;
import org.greenblitz.utils.ColorSensor;

public class LineFollowerCommand extends PeriodicCommand {

    protected Chassis m_chassis;
    protected double speed;
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
    public LineFollowerCommand(long period, double kp, double ki, double kd, double speed, ColorSensor sensor, double distance, boolean side) {
        super(period);
        m_chassis = Chassis.getInstance();
        requires(m_chassis);
        m_controller = new LineFollower(kp, ki, kd);
        this.speed = speed;
        this.sensor = sensor;
        this.distance = distance;
        m_firstRun = true;
        this.side = side ? 1 : -1;
        m_controller.setGoal(50.0);
    }

    public LineFollowerCommand(double kp, double ki, double kd, double speed, ColorSensor sensor, double distance, boolean side) {
        this(50, kp, ki, kd, speed, sensor, distance, side);
    }

    @Override
    protected void periodic() {
        if (m_firstRun) {
            initDistance = Chassis.getInstance().getDistance();
            m_firstRun = false;
        }
        m_chassis.arcadeDrive(speed, side * m_controller.calculatePID(getPercentage(sensor.read())));

    }

    @Override
    protected boolean isFinished() {
        return Chassis.getInstance().getDistance() > initDistance + distance;
    }

    protected double getPercentage(short[] rgb) {
        return 100.0 / 3.0 * ((rgb[0] - backround[0]) / (line[0] - backround[0])
                + (rgb[1] - backround[1]) / (line[1] - backround[1])
                + (rgb[2] - backround[2]) / (line[2] - backround[2]));
    }
}
