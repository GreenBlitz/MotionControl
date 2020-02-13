package org.greenblitz.motion.profiling;

/**
 * @author Peleg
 */

public class ProfilingConfiguration {

    protected double velMultLin, accMultLin, jump, wheelPidKp, wheelPidKi, wheelPidKd, collapseConstaPerWheel, angPidKp, angPidKi, angPidKd, collapseConstAngular;
    protected int smoothingTale;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProfilingConfiguration that = (ProfilingConfiguration) o;

        if (Double.compare(that.velMultLin, velMultLin) != 0) return false;
        if (Double.compare(that.accMultLin, accMultLin) != 0) return false;
        if (Double.compare(that.jump, jump) != 0) return false;
        if (Double.compare(that.wheelPidKp, wheelPidKp) != 0) return false;
        if (Double.compare(that.wheelPidKd, wheelPidKd) != 0) return false;
        if (Double.compare(that.wheelPidKi, wheelPidKi) != 0) return false;
        if (Double.compare(that.angPidKp, angPidKp) != 0) return false;
        if (Double.compare(that.angPidKd, angPidKd) != 0) return false;
        if (Double.compare(that.angPidKi, angPidKi) != 0) return false;
        if (Double.compare(that.collapseConstaPerWheel, collapseConstaPerWheel) != 0) return false;
        if (Double.compare(that.collapseConstAngular, collapseConstAngular) != 0) return false;
        return smoothingTale == that.smoothingTale;
    }

    public ProfilingConfiguration(double velMultLin, double accMultLin, double jump,
                                  double wheelPidKp, double wheelPidKi, double wheelPidKd,
                                  double collapseConstaPerWheel,
                                  double angPidKp, double angPidKi, double angPidKd,
                                  double collapseConstAngular, int smoothingTale) {
        this.velMultLin = velMultLin;
        this.accMultLin = accMultLin;
        this.jump = jump;
        this.wheelPidKp = wheelPidKp;
        this.wheelPidKi = wheelPidKi;
        this.wheelPidKd = wheelPidKd;
        collapseConstaPerWheel = collapseConstaPerWheel;
        this.angPidKp = angPidKp;
        this.angPidKi = angPidKi;
        this.angPidKd = angPidKd;
        collapseConstAngular = collapseConstAngular;
        this.smoothingTale = smoothingTale;
    }

    public double getVelMultLin() {
        return velMultLin;
    }

    public double getAccMultLin() {
        return accMultLin;
    }

    public double getJump() {
        return jump;
    }

    public double getWheelPidKp() {
        return wheelPidKp;
    }

    public double getWheelPidKd() {
        return wheelPidKd;
    }

    public double getWheelPidKi() {
        return wheelPidKi;
    }

    public double getAngPidKp() {
        return angPidKp;
    }

    public double getAngPidKd() {
        return angPidKd;
    }

    public double getAngPidKi() {
        return angPidKi;
    }

    public double getCollapseConstaPerWheel() {
        return collapseConstaPerWheel;
    }

    public double getCollapseConstAngular() {
        return collapseConstAngular;
    }

    public int getSmoothingTale() {
        return smoothingTale;
    }

    public ProfilingConfiguration setVelMultLin(double velMultLin) {
        return new ProfilingConfiguration(velMultLin, accMultLin, jump, wheelPidKp, wheelPidKi, wheelPidKd, collapseConstaPerWheel,
                angPidKp, angPidKi, angPidKd, collapseConstAngular, smoothingTale);
    }

    public ProfilingConfiguration setAccMultLin(double accMultLin) {
        return new ProfilingConfiguration(velMultLin, accMultLin, jump, wheelPidKp, wheelPidKi, wheelPidKd, collapseConstaPerWheel,
                angPidKp, angPidKi, angPidKd, collapseConstAngular, smoothingTale);
    }

    public ProfilingConfiguration setJump(double jump) {
        return new ProfilingConfiguration(velMultLin, accMultLin, jump, wheelPidKp, wheelPidKi, wheelPidKd, collapseConstaPerWheel,
                angPidKp, angPidKi, angPidKd, collapseConstAngular, smoothingTale);
    }

    public ProfilingConfiguration setWheelPidKp(double wheelPidKp) {
        return new ProfilingConfiguration(velMultLin, accMultLin, jump, wheelPidKp, wheelPidKi, wheelPidKd, collapseConstaPerWheel,
                angPidKp, angPidKi, angPidKd, collapseConstAngular, smoothingTale);
    }

    public ProfilingConfiguration setWheelPidKi(double wheelPidKi) {
        return new ProfilingConfiguration(velMultLin, accMultLin, jump, wheelPidKp, wheelPidKi, wheelPidKd, collapseConstaPerWheel,
                angPidKp, angPidKi, angPidKd, collapseConstAngular, smoothingTale);
    }

    public ProfilingConfiguration setWheelPidKd(double wheelPidKd) {
        return new ProfilingConfiguration(velMultLin, accMultLin, jump, wheelPidKp, wheelPidKi, wheelPidKd, collapseConstaPerWheel,
                angPidKp, angPidKi, angPidKd, collapseConstAngular, smoothingTale);
    }

    public ProfilingConfiguration setCollapseConstaPerWheel(double collapseConstaPerWheel) {
        return new ProfilingConfiguration(velMultLin, accMultLin, jump, wheelPidKp, wheelPidKi, wheelPidKd, collapseConstaPerWheel,
                angPidKp, angPidKi, angPidKd, collapseConstAngular, smoothingTale);
    }

    public ProfilingConfiguration setAngPidKp(double angPidKp) {
        return new ProfilingConfiguration(velMultLin, accMultLin, jump, wheelPidKp, wheelPidKi, wheelPidKd, collapseConstaPerWheel,
                angPidKp, angPidKi, angPidKd, collapseConstAngular, smoothingTale);
    }

    public ProfilingConfiguration setAngPidKi(double angPidKi) {
        return new ProfilingConfiguration(velMultLin, accMultLin, jump, wheelPidKp, wheelPidKi, wheelPidKd, collapseConstaPerWheel,
                angPidKp, angPidKi, angPidKd, collapseConstAngular, smoothingTale);
    }

    public ProfilingConfiguration setAngPidKd(double angPidKd) {
        return new ProfilingConfiguration(velMultLin, accMultLin, jump, wheelPidKp, wheelPidKi, wheelPidKd, collapseConstaPerWheel,
                angPidKp, angPidKi, angPidKd, collapseConstAngular, smoothingTale);
    }

    public ProfilingConfiguration setCollapseConstAngular(double collapseConstAngular) {
        return new ProfilingConfiguration(velMultLin, accMultLin, jump, wheelPidKp, wheelPidKi, wheelPidKd, collapseConstaPerWheel,
                angPidKp, angPidKi, angPidKd, collapseConstAngular, smoothingTale);
    }

    public ProfilingConfiguration setSmoothingTale(int smoothingTale) {
        return new ProfilingConfiguration(velMultLin, accMultLin, jump, wheelPidKp, wheelPidKi, wheelPidKd, collapseConstaPerWheel,
                angPidKp, angPidKi, angPidKd, collapseConstAngular, smoothingTale);
    }
}
