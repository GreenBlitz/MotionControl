package gbmotion.util;

public class Shifter {
	private static Shifter instance;
	
	private Shifter() {}
	
	public static synchronized Shifter getInstance() {
		if (instance == null) {
			instance = new Shifter();
		}
		return instance;
	}
	
	public RobotStats.Gear getState() {
		return RobotStats.Gear.POWER;
	}
}
