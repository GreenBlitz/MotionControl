package gbmotion.util;

public final class RobotStats {
	public static enum Gear {
		VELOCITY, POWER
	}

	public static final class Ragnarok {
		public static final double HORIZONTAL_WHEEL_DIST = 0.68, VERTICAL_WHEEL_DIST = 0.7, WHEEL_RADIUS = 0.045,
				LEFT_ENCODER_SCALE = 168 / 45281.0, RIGHT_ENCODER_SCALE = 168 / 45321.0;

		public static final boolean CHASSIS_LEFT_ENCODER_INVERT = true, CHASSIS_RIGHT_ENCODER_INVERT = false;
	}

	public static final class Guillotine {

		/**
		 * Chassis size
		 * 
		 * @author karlo
		 */
		public static enum Chassis {
			/**
			 * Distance between parallel wheels
			 */
			HORIZONTAL_DISTANCE(0.565, "meters"),
			
			/**
			 * Distance between 2 most radical wheels on same side
			 */
			VERTICAL_DISTANCE(0.65, "meters"), 
			
			/**
			 * Radius of chassis wheels
			 */
			WHEEL_RADIUS(0.7512, "meters");

			public final double value;
			public final String units;

			private Chassis(double val, String unit) {
				value = val;
				units = unit;
			}
		}

		/**
		 * Encoders tick per metre
		 * @author karlo
		 *
		 */
		public static enum EncoderScale {
			LEFT_VELOCITY(, Gear.VELOCITY),
			LEFT_POWER(, Gear.POWER),
			RIGHT_VELOCITY(, Gear.VELOCITY),
			RIGHT_POWER(, Gear.POWER);
			
			public final double value;
			public final Gear gear;
			
			private EncoderScale(double val, Gear gear) {
				this.value = val;
				this.gear = gear;
			}
			
			/**
			 * 
			 * @param gear The gear in which the scale was measured
			 * @param dir Direction- true for right, false for left
			 * @return Encoder Scale of the encoder which matches given data
			 */
			public static EncoderScale of(Gear gear, boolean dir) {
				if (gear == Gear.POWER)
					return dir ? RIGHT_POWER : LEFT_POWER;
				else
					return dir ? RIGHT_VELOCITY : LEFT_VELOCITY;
			}
		}

		/**
		 * Encoders ticks per Radian
		 * @author karlo
		 */
		public static enum EncoderRadianScale {
			LEFT_VELOCITY(, Gear.VELOCITY),
			LEFT_POWER(, Gear.POWER),
			RIGHT_VELOCITY(, Gear.VELOCITY),
			RIGHT_POWER(, Gear.POWER);
			
			public final double value;
			public final Gear gear;
			
			private EncoderRadianScale(double val, Gear gear) {
				this.value = val;
				this.gear = gear;
			}

			/**
			 * 
			 * @param gear The gear in which the scale was measured
			 * @param dir Direction- true for right, false for left
			 * @return Encoder Radian Scale of the encoder which matches given data
			 */
			public static EncoderRadianScale of(Gear gear, boolean dir) {
				if (gear == Gear.POWER)
					return dir ? RIGHT_POWER : LEFT_POWER;
				else
					return dir ? RIGHT_VELOCITY : LEFT_VELOCITY;
			}
		}

		/**
		 * Which encoder is inverted
		 * 
		 * @author karlo
		 */
		public static enum EncoderInvert {
			LEFT(), RIGHT();

			public final int invert;
			public final boolean inverted;

			private EncoderInvert(boolean invert) {
				inverted = invert;
				this.invert = inverted ? -1 : 1;
			}
		}
	}
}
