package org.greenblitz.utils;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SerialPort;

import java.util.ArrayList;
import java.util.LinkedList;

public class Navx {
    private AHRS m_navx;
    private ArrayList<Double> past_angs;
    private static final int AVG_SIZE = 5;

    private static Navx instance = null;// = new Navx();

    private Navx(){
        m_navx = new AHRS(SerialPort.Port.kUSB);
        past_angs = new ArrayList<>();
        for (int i = 0; i < AVG_SIZE; i++)
            past_angs.add(m_navx.getAngle());
    }

    public void updateAngle(){
        past_angs.remove(0);
        past_angs.add(m_navx.getAngle());
    }

    public static Navx getInstance(){
        if (instance == null) instance = new Navx();
        return instance;
    }

    public void reset() {
        m_navx.reset();
        past_angs = new ArrayList<>();
        for (int i = 0; i < AVG_SIZE; i++)
            past_angs.add(0.0);
    }

    public AHRS get_navx() {
        return m_navx;
    }

    /**
     *
     * @return angle in RADIANS
     */
    public double getAngle() {
        double val = 0;
        for (Double d : past_angs)
            val += d;
        return Math.toRadians(val / AVG_SIZE);
    }
}