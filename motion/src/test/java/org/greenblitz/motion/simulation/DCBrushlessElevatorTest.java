package org.greenblitz.motion.simulation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DCBrushlessElevatorTest {

    @Test
    void runSimulation() {
        DCBrushlessElevator model = new DCBrushlessElevator(10, 6,
                5, 5, 0.05);
        for (int i = 0; i < 100; i++){
            model.runSimulation(0.01, 12, 0.2);
            System.out.println(model.getElevatorX() + " " + model.getElevatorV());
        }

        for (int i = 0; i < 100; i++){
            model.runSimulation(0.01, 0.01*12, 0.2);
            System.out.println(model.getElevatorX() + " " + model.getElevatorV());
        }
    }
}