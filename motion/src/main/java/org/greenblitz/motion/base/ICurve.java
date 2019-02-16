package org.greenblitz.motion.base;

public interface ICurve {

    double getX(double t);
    double getY(double t);

    double getCurvature(double t);

    ICurve getSubCurve(double tStart, double tEnd);

}
