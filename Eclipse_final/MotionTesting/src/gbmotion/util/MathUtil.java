package gbmotion.util;

public class MathUtil {
	public static final double TAU = 2 * Math.PI;
	
	public static double normalizeAngle(double angle) {
		return angle > 0 ? angle % TAU : TAU - (Math.abs(angle) % TAU);
	} 
}
