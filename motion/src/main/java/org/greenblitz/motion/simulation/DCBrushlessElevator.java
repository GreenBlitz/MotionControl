package org.greenblitz.motion.simulation;

public class DCBrushlessElevator {

    private double torqueConst, // Perform experiment with known torque and divide
            velocityConst,      // VoltsMax / OmegaMax
            motorResistance;    // Check online/on data sheet

    private double elevatorMass,// Measure
            puellyRadius;       // Measure

    private static final double g = 9.81;

    private double elevatorX, elevatorV;

    public double getElevatorX() {
        return elevatorX;
    }

    public double getElevatorV() {
        return elevatorV;
    }

    public DCBrushlessElevator(double torqueConst, double velocityConst, double motorResistance, double elevatorMass, double puellyRadius) {
        this.torqueConst = torqueConst;
        this.velocityConst = velocityConst;
        this.motorResistance = motorResistance;
        this.elevatorMass = elevatorMass;
        this.puellyRadius = puellyRadius;
        this.elevatorX = 0;
        this.elevatorV = 0;
    }

    public double getTorque(double volts, double omega) {
        return torqueConst * (volts - velocityConst * omega) / motorResistance;
    }

    public double getForce(double torque) {
        return torque / puellyRadius;
    }

    public double getAcceleration(double torque) {
        return (getForce(torque) / elevatorMass) - g;
    }

    public double getOmega() {
        return elevatorV / (4 * Math.pow(Math.PI, 2) * puellyRadius);
    }

    public void runSimulation(double dt, double volts, double noiseMultiplier) {
        double elevatorA = getAcceleration(getTorque(volts, getOmega()));
        double noise = (Math.random() * 2 - 1) * elevatorA * noiseMultiplier;
        elevatorX += 0.5 * dt * dt * (elevatorA + noise) + dt * elevatorV;
        elevatorV += dt * (elevatorA + noise);
        if (elevatorX < 0)
            elevatorX = 0;
        if (elevatorX == 0 && elevatorV < 0)
            elevatorV = 0;
    }

}
