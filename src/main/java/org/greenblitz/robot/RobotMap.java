package org.greenblitz.robot;

public class RobotMap {

    public static class JoystickID {
        public static final int MAIN = 0, SIDE = 2;
    }

    public static class ChassisPort {
        public static final int FRONT_LEFT = 3, FRONT_RIGHT = 2, REAR_LEFT = 8, REAR_RIGHT = 10;
    }

    //Shifter
    public static final int SHIFTER_SOLENOID_FORWARD_PORT = 3,
            SHIFTER_SOLENOID_REVERSE_PORT = 2;

    //Claw
    public static final int CLAW_MOTOR_PORT = 7,
            CLAW_OPEN_MICROSWITCH_PORT = 3;

    //Intake
    public static final int INTAKE_WHEELS_MOTOR_PORT = 6;

    //Pitcher
    public static final int PITCHER_MOTOR_PORT = 9,
            PITCHER_POTENTIOMETER_PORT = 1;

    //Climber
    public static final int CLIMBER_MOTOR_PORT = 0,
            CLIMBER_TOP_MICROSWITCH_PORT = 4;

    //Cannon
    public static final int CANNON_MOTOR_PORT = 4,
            CANNON_ROPE_MICROSWITCH_PORT = 0,
            CANNON_PLATFORM_MICROSWITCH_PORT = 1;

    //Pin
    public static final int PIN_SOLENOID_FORWARD_PORT = 0,
            PIN_SOLENOID_BACKWARD_PORT = 1;

    //NewClaw
    public static final int NEWCLAW_SOLENOID_FORWARD_PORT = 0,
            NEWCLAW_SOLENOID_BACKWARD_PORT = 0;

    //Enum values
    //Pitcher

    public static final double PITCHER_COLLECT_VALUE = 0d / 180,
            PITCHER_SWITCH_FORWARD_VALUE = 75d / 180, //70
            PITCHER_SWITCH_BACKWARD_VALUE = 155d / 180,
            PITCHER_PLATE_VALUE = 180d / 180,
            PITCHER_EXCHANGE_VALUE = 20d / 180;

    //Shooter rotation
    public static final double SHOOTER_ROTATION_LEFT_VALUE = 0,
            SHOOTER_ROTATION_MIDDLE_VALUE = 0;
}