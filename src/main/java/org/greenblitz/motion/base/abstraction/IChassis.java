package org.greenblitz.motion.base.abstraction;

public interface IChassis {

    void tankDrive(double left, double right);

    default void arcadeDrive(double power, double rotation) {
        double leftMotorSpeed;
        double rightMotorSpeed;

        if (power > 0.0) {
            if (rotation > 0.0) {
                leftMotorSpeed = power - rotation;
                rightMotorSpeed = Math.max(power, rotation);
            } else {
                leftMotorSpeed = Math.max(power, -rotation);
                rightMotorSpeed = power + rotation;
            }
        } else {
            if (rotation > 0.0) {
                leftMotorSpeed = -Math.max(-power, rotation);
                rightMotorSpeed = power + rotation;
            } else {
                leftMotorSpeed = power - rotation;
                rightMotorSpeed = -Math.max(-power, -rotation);
            }
        }

        tankDrive(leftMotorSpeed, rightMotorSpeed);
    }

    default void stop() {
        tankDrive(0, 0);
    }

    IEncoder getLeftEncoder();

    IEncoder getRightEncoder();

    double getWheelRadius();

    double getWheelbaseWidth();
}
