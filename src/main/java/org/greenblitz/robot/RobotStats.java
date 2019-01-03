package org.greenblitz.robot;

public final class RobotStats {
    public enum Gear {
        VELOCITY, POWER
    }

    public static final class Picasso {

        /**
         * Chassis size
         *
         * @author karlo
         */
        public final class Chassis {

            /**
             * Distance between parallel wheels
             */
            public static final double HORIZONTAL_DISTANCE = 0.59;// 0.565 * ((Math.PI + 0.25) / (Math.PI));

            /**
             * Distance between 2 most radical wheels on same side
             */
            public static final double VERTICAL_DISTANCE = 0.65;

            /**
             * Radius of chassis wheels
             */
            public static final double WHEEL_RADIUS = 0.0762;

            public static final double MAX_VELOCITY = 2, MAX_ACCELERATION = 30, MAX_JERK = 1000;

            public static final double WHEEL_DIAMETER = 2 * WHEEL_RADIUS;

            public static final double WHEEL_CIRCUMFERENCE = Math.PI * WHEEL_DIAMETER;
        }

        /**
         * Encoders tick per metre
         *
         * @author karlo
         */
        public static class EncoderMetreScale {

            /**
             * Encoder ticks per meter on each side in each gear
             */
            public static final double LEFT_VELOCITY = 2549, LEFT_POWER = 2549, RIGHT_VELOCITY = 2569, RIGHT_POWER = 2569;

            /**
             * @param gear The gear in which the scale was measured
             * @param dir  Direction- true for right, false for left
             * @return Encoder Scale of the encoder which matches given
             * data
             */
            public static double of(Gear gear, boolean dir) {
                if (gear == Gear.POWER)
                    return dir ? RIGHT_POWER : LEFT_POWER;
                else
                    return dir ? RIGHT_VELOCITY : LEFT_VELOCITY;
            }

        }

        /**
         * Encoders ticks per Radian
         *
         * @author karlo
         */

        public static class EncoderRadianScale {

            /**
             * Encoder ticks per radian on each side in each gear
             */
            public static final double LEFT_VELOCITY = EncoderMetreScale.LEFT_VELOCITY * Chassis.WHEEL_RADIUS,
                    LEFT_POWER = EncoderMetreScale.LEFT_POWER * Chassis.WHEEL_RADIUS,
                    RIGHT_VELOCITY = EncoderMetreScale.LEFT_VELOCITY * Chassis.WHEEL_RADIUS,
                    RIGHT_POWER = EncoderMetreScale.RIGHT_POWER * Chassis.WHEEL_RADIUS;

            /**
             * @param gear The gear in which the scale was measured
             * @param dir  Direction- true for right, false for left
             * @return Encoder Radian Scale of the encoder which matches given
             * data
             */
            public static double of(Gear gear, boolean dir) {
                if (gear == Gear.POWER)
                    return dir ? RIGHT_POWER : LEFT_POWER;
                else
                    return dir ? RIGHT_VELOCITY : LEFT_VELOCITY;
            }
        }

    }
}