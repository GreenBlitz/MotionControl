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

	public static final class Lobiiiiiin {

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
		 * 
		 * @author karlo
		 */
		public static enum EncoderMetreScale {
			/**
			 * Left encoder ticks per meter in velocity gear
			 */
			LEFT_VELOCITY(681, Gear.VELOCITY), 
			
			/**
			 * Left encoder ticks per meter in power gear
			 */
			LEFT_POWER(2494, Gear.POWER),
			
			/**
			 * Right encoder ticks per meter in velocity gear
			 */
			RIGHT_VELOCITY(-685, Gear.VELOCITY),
			
			/**
			 * Right encoder ticks per meter in power gear
			 */
			RIGHT_POWER(-2466, Gear.POWER);

			public final int value;
			public final Gear gear;

			private EncoderMetreScale(int val, Gear gear) {
				this.value = val;
				this.gear = gear;
			}

			/**
			 * 
			 * @param gear
			 *            The gear in which the scale was measured
			 * @param dir
			 *            Direction- true for right, false for left
			 * @return Encoder Scale of the encoder which matches given data
			 */
			public static EncoderMetreScale of(Gear gear, boolean dir) {
				if (gear == Gear.POWER)
					return dir ? RIGHT_POWER : LEFT_POWER;
				else
					return dir ? RIGHT_VELOCITY : LEFT_VELOCITY;
			}

			/**
			 * 
			 * @return signum of this value
			 */
			public int invert() {
				return (int) Math.signum(value);
			}
			
			/**
			 * 
			 * @return true if the scale is smaller than 0
			 */
			public boolean inverted() {
				return value < 0;
			}
		}

		/**
		 * Encoders ticks per Radian
		 * 
		 * @author karlo
		 */
		public static enum EncoderRadianScale {
			/**
			 * Left encoder ticks per radian in velocity gear
			 */
			LEFT_VELOCITY(166 / Math.PI, Gear.VELOCITY),
			
			/**
			 * Left encoder ticks per radian in power gear
			 */
			LEFT_POWER(594 / Math.PI,Gear.POWER),
			
			/**
			 * Right encoder ticks per radian in velocity gear
			 */
			RIGHT_VELOCITY(-164 / Math.PI, Gear.VELOCITY),
			
			/**
			 * Right encoder ticks per radian in power gear 
			 */
			RIGHT_POWER(-579 / Math.PI, Gear.POWER);

			public final double value;
			public final Gear gear;

			private EncoderRadianScale(double val, Gear gear) {
				this.value = val;
				this.gear = gear;
			}

			/**
			 * 
			 * @param gear
			 *            The gear in which the scale was measured
			 * @param dir
			 *            Direction- true for right, false for left
			 * @return Encoder Radian Scale of the encoder which matches given
			 *         data
			 */
			public static EncoderRadianScale of(Gear gear, boolean dir) {
				if (gear == Gear.POWER)
					return dir ? RIGHT_POWER : LEFT_POWER;
				else
					return dir ? RIGHT_VELOCITY : LEFT_VELOCITY;
			}
			
			/**
			 * 
			 * @return signum of this value
			 */
			public int invert() {
				return (int) Math.signum(value);
			}
			
			/**
			 * 
			 * @return true if the scale is smaller than 0
			 */
			public boolean inverted() {
				return value < 0;
			}
		}
	}
}
