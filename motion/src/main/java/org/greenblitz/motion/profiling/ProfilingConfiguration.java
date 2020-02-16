package org.greenblitz.motion.profiling;

/**
 * @author Peleg
 */
public class ProfilingConfiguration {

    protected double velMultLin, accMultLin, jump, wheelPidKp, wheelPidKi, wheelPidKd, collapseConstPerWheel, angPidKp, angPidKi, angPidKd, collapseConstAngular;
    protected int smoothingTail;

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
        if (Double.compare(that.collapseConstPerWheel, collapseConstPerWheel) != 0) return false;
        if (Double.compare(that.collapseConstAngular, collapseConstAngular) != 0) return false;
        return smoothingTail == that.smoothingTail;
    }

    public ProfilingConfiguration(double velMultLin, double accMultLin, double jump,
                                  double wheelPidKp, double wheelPidKi, double wheelPidKd,
                                  double collapseConstPerWheel,
                                  double angPidKp, double angPidKi, double angPidKd,
                                  double collapseConstAngular, int smoothingTail) {
        this.velMultLin = velMultLin;
        this.accMultLin = accMultLin;
        this.jump = jump;
        this.wheelPidKp = wheelPidKp;
        this.wheelPidKi = wheelPidKi;
        this.wheelPidKd = wheelPidKd;
        this.collapseConstPerWheel = collapseConstPerWheel;
        this.angPidKp = angPidKp;
        this.angPidKi = angPidKi;
        this.angPidKd = angPidKd;
        this.collapseConstAngular = collapseConstAngular;
        this.smoothingTail = smoothingTail;
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

    public double getCollapseConstPerWheel() {
        return collapseConstPerWheel;
    }

    public double getCollapseConstAngular() {
        return collapseConstAngular;
    }

    public int getSmoothingTail() {
        return smoothingTail;
    }

    public ProfilingConfiguration setVelMultLin(double velMultLin) {
        return new ProfilingConfiguration(velMultLin, accMultLin, jump, wheelPidKp, wheelPidKi, wheelPidKd, collapseConstPerWheel,
                angPidKp, angPidKi, angPidKd, collapseConstAngular, smoothingTail);
    }

    public ProfilingConfiguration setAccMultLin(double accMultLin) {
        return new ProfilingConfiguration(velMultLin, accMultLin, jump, wheelPidKp, wheelPidKi, wheelPidKd, collapseConstPerWheel,
                angPidKp, angPidKi, angPidKd, collapseConstAngular, smoothingTail);
    }

    public ProfilingConfiguration setJump(double jump) {
        return new ProfilingConfiguration(velMultLin, accMultLin, jump, wheelPidKp, wheelPidKi, wheelPidKd, collapseConstPerWheel,
                angPidKp, angPidKi, angPidKd, collapseConstAngular, smoothingTail);
    }

    public ProfilingConfiguration setWheelPidKp(double wheelPidKp) {
        return new ProfilingConfiguration(velMultLin, accMultLin, jump, wheelPidKp, wheelPidKi, wheelPidKd, collapseConstPerWheel,
                angPidKp, angPidKi, angPidKd, collapseConstAngular, smoothingTail);
    }

    public ProfilingConfiguration setWheelPidKi(double wheelPidKi) {
        return new ProfilingConfiguration(velMultLin, accMultLin, jump, wheelPidKp, wheelPidKi, wheelPidKd, collapseConstPerWheel,
                angPidKp, angPidKi, angPidKd, collapseConstAngular, smoothingTail);
    }

    public ProfilingConfiguration setWheelPidKd(double wheelPidKd) {
        return new ProfilingConfiguration(velMultLin, accMultLin, jump, wheelPidKp, wheelPidKi, wheelPidKd, collapseConstPerWheel,
                angPidKp, angPidKi, angPidKd, collapseConstAngular, smoothingTail);
    }

    public ProfilingConfiguration setCollapseConstPerWheel(double collapseConstPerWheel) {
        return new ProfilingConfiguration(velMultLin, accMultLin, jump, wheelPidKp, wheelPidKi, wheelPidKd, collapseConstPerWheel,
                angPidKp, angPidKi, angPidKd, collapseConstAngular, smoothingTail);
    }

    public ProfilingConfiguration setAngPidKp(double angPidKp) {
        return new ProfilingConfiguration(velMultLin, accMultLin, jump, wheelPidKp, wheelPidKi, wheelPidKd, collapseConstPerWheel,
                angPidKp, angPidKi, angPidKd, collapseConstAngular, smoothingTail);
    }

    public ProfilingConfiguration setAngPidKi(double angPidKi) {
        return new ProfilingConfiguration(velMultLin, accMultLin, jump, wheelPidKp, wheelPidKi, wheelPidKd, collapseConstPerWheel,
                angPidKp, angPidKi, angPidKd, collapseConstAngular, smoothingTail);
    }

    public ProfilingConfiguration setAngPidKd(double angPidKd) {
        return new ProfilingConfiguration(velMultLin, accMultLin, jump, wheelPidKp, wheelPidKi, wheelPidKd, collapseConstPerWheel,
                angPidKp, angPidKi, angPidKd, collapseConstAngular, smoothingTail);
    }

    public ProfilingConfiguration setCollapseConstAngular(double collapseConstAngular) {
        return new ProfilingConfiguration(velMultLin, accMultLin, jump, wheelPidKp, wheelPidKi, wheelPidKd, collapseConstPerWheel,
                angPidKp, angPidKi, angPidKd, collapseConstAngular, smoothingTail);
    }

    public ProfilingConfiguration setSmoothingTail(int smoothingTail) {
        return new ProfilingConfiguration(velMultLin, accMultLin, jump, wheelPidKp, wheelPidKi, wheelPidKd, collapseConstPerWheel,
                angPidKp, angPidKi, angPidKd, collapseConstAngular, smoothingTail);
    }
}
