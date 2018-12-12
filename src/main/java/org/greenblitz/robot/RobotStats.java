package org.greenblitz.robot;

import org.greenblitz.robot.subsystems.Chassis;

import java.io.CharArrayReader;

public final class RobotStats {
    public enum Gear {
        VELOCITY, POWER
    }

    @Deprecated
    public static final class Ragnarok {
        public static final double HORIZONTAL_WHEEL_DIST = 0.68, VERTICAL_WHEEL_DIST = 0.7, WHEEL_RADIUS = 0.045,
                LEFT_ENCODER_SCALE = 168 / 45281.0, RIGHT_ENCODER_SCALE = 168 / 45321.0;

        public static final boolean CHASSIS_LEFT_ENCODER_INVERT = true, CHASSIS_RIGHT_ENCODER_INVERT = false;
    }

    @Deprecated
    public static final class Gildaboi {
        /**
         * Chassis size
         */
        public enum Chassis {
            /**
             * Distance between parallel wheels
             */
            HORIZONTAL_DISTANCE(0.60, "meters"),

            /**
             * Distance between 2 most radical wheels on same side
             */
            VERTICAL_DISTANCE(0.71, "meters"),

            /**
             * Radius of chassis wheels
             */
            WHEEL_RADIUS(0.0762, "meters");

            public final double value;
            public final String units;

            private Chassis(double val, String unit) {
                value = val;
                units = unit;
            }
        }

        /**
         * Encoders tick per metre
         *
         * @author karlo
         */
        public enum EncoderMetreScale {
            /**
             * Left encoder ticks per meter in velocity gear
             */
            LEFT_VELOCITY(-2549, Gear.VELOCITY),

            /**
             * Left encoder ticks per meter in power gear
             */
            LEFT_POWER(-2549, Gear.POWER),

            /**
             * Right encoder ticks per meter in velocity gear
             */
            RIGHT_VELOCITY(2569, Gear.VELOCITY),

            /**
             * Right encoder ticks per meter in power gear
             */
            RIGHT_POWER(2569, Gear.POWER);

            public final int value;
            public final Gear gear;

            private EncoderMetreScale(int val, Gear gear) {
                this.value = val;
                this.gear = gear;
            }

            /**
             * @param gear The gear in which the scale was measured
             * @param dir  Direction- true for right, false for left
             * @return Encoder Scale of the encoder which matches given data
             */
            public static EncoderMetreScale of(Gear gear, boolean dir) {
                if (gear == Gear.POWER)
                    return dir ? RIGHT_POWER : LEFT_POWER;
                else
                    return dir ? RIGHT_VELOCITY : LEFT_VELOCITY;
            }

            /**
             * @return signum of this value
             */
            public int invert() {
                return (int) Math.signum(value);
            }

            /**
             * @return true if the scale is smaller than 0
             */
            public boolean inverted() {
                return value < 0;
            }
        }
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
            public static final double HORIZONTAL_DISTANCE = 0.565 * ((Math.PI + 0.25) / (Math.PI));

            /**
             * Distance between 2 most radical wheels on same side
             */
            public static final double VERTICAL_DISTANCE = 0.65;

            /**
             * Radius of chassis wheels
             */
            public static final double WHEEL_RADIUS = 0.0762;

            public static final double MAX_VELOCITY = 0.08, MAX_ACCELERATION = 3.15, MAX_JERK = 160;//at 0.6 power
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
            public static final double LEFT_VELOCITY = 166, LEFT_POWER = 594, RIGHT_VELOCITY = -164, RIGHT_POWER = -579;

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