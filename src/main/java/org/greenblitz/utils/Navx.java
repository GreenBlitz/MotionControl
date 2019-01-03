package org.greenblitz.utils;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SerialPort;
import org.greenblitz.motion.base.IGyro;

public class Navx implements IGyro {
    private AHRS m_navx;

    private static Navx instance = null;// = new Navx();

    private Navx(){
        m_navx = new AHRS(SerialPort.Port.kMXP);
    }

    public static Navx getInstance(){
        if (instance == null) instance = new Navx();
        return instance;
    }

    @Override
    public double getAngle() {
        return m_navx.getAngle();
    }
}
