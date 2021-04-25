package org.greenblitz.motion.profiling;

public interface IMotorFormula {
    double v(double a, double f);
    double a(double v, double f);
    double f(double v, double a);
}
