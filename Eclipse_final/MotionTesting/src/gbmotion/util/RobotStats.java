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

		public static enum Chassis {
			HORIZONTAL_DISTANCE(0.565, "meters"), VERTICAL_DISTANCE(0.65, "meters"), WHEEL_RADIUS(0.7512, "meters");

			public final double value;
			public final String units;

			private Chassis(double val, String unit) {
				value = val;
				units = unit;
			}
		}
		
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
		}
		
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
		}
		
		public static enum EncoderInvert {
			LEFT(),
			RIGHT();
				
			public final int invert;
			public final boolean inverted;
			
			private EncoderInvert(boolean invert) {
				inverted = invert;
				this.invert = inverted ? -1 : 1;
			}
		}
	}
}
