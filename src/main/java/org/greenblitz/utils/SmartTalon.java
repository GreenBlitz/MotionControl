package org.greenblitz.utils;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class SmartTalon extends TalonSRX {
    private double mLastValue = 0.0;

    public SmartTalon(int deviceNumber) {
        super(deviceNumber);
    }

    public void set(double power) {
        if (power != mLastValue) {
            mLastValue = power;
            set(ControlMode.PercentOutput, mLastValue);
        }
    }

    public double getLastValue() {
        return mLastValue;
    }
}
