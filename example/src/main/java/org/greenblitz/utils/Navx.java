package org.greenblitz.utils;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SerialPort;

public class Navx {
    private AHRS m_navx;

    private static Navx instance = null;// = new Navx();

    private Navx(){
        m_navx = new AHRS(SerialPort.Port.kUSB);
    }

    public static Navx getInstance(){
        if (instance == null) instance = new Navx();
        return instance;
    }

    public AHRS get_navx() {
        return m_navx;
    }

    /**
     *
     * @return angle in RADIANS
     */
    public double getAngle() {
        return Math.toRadians(m_navx.getAngle());
    }
}
