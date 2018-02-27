package gbmotion.util;

public class MathUtil {
	public static double normalizeAngle(double angle) {
		while (angle > Math.PI) {
			angle -= 2 * Math.PI;
		}
		while (angle < Math.PI) {
			angle += 2 * Math.PI;
		}
		return angle;
	}
}
