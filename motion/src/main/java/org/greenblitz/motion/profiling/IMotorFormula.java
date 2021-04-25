package org.greenblitz.motion.profiling;

public interface IMotorFormula {

    double getVel(double acc, double power);

    double getAcc(double vel, double power);

    double getPower(double vel, double acc);
}
