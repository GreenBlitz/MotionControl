package org.greenblitz.robot;

public final class RobotStats {
    public enum Gear {
        VELOCITY, POWER
    }

    public static class Ragnarok {
        public static class EncoderTicksPerMeter {
            public static final double LEFT_SPEED = 267.5;
            public static final double RIGHT_SPEED = 267.5;

            public static final double LEFT_POWER = 730;
            public static final double RIGHT_POWER = 730;
        }

        public static final double WHEELBASE = 0.69;
        public static final double WHEEL_RADIUS = 0.05;
    }
}